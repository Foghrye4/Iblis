package iblis.item;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.init.IblisParticles;
import iblis.init.IblisSounds;
import iblis.util.IblisMathUtil;
import iblis.util.PlayerUtils;
import iblis_headshots.IblisHeadshotsMod;
import iblis_headshots.util.HeadShotHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ItemFirearmsBase extends Item implements ICustomLeftClickItem {

	private static final Predicate<Entity> BULLET_TARGETS = new Predicate<Entity>() {
		@Override
		public boolean apply(@Nullable Entity entity) {
			return EntitySelectors.NOT_SPECTATING.apply(entity) && EntitySelectors.IS_ALIVE.apply(entity)
					&& entity.canBeCollidedWith();
		}

		@Override
		public boolean test(@Nullable Entity entity) {
			return apply(entity);
		}
	};

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return true;
	}

	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (attacker instanceof EntityPlayer)
			this.onShoot(attacker.world, (EntityPlayer) attacker, EnumHand.MAIN_HAND);
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public void onLeftClick(World world, EntityPlayerMP player, EnumHand mainHand) {
		this.onShoot(world, player, EnumHand.MAIN_HAND);
	}

	// Server side only
	public void onShoot(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (itemstack.getTagCompound() == null)
			return;
		NBTTagCompound nbt = itemstack.getTagCompound();
		NBTTagList ammoIn = nbt.getTagList(NBTTagsKeys.AMMO, 10);
		int cockedBowString = nbt.getInteger(NBTTagsKeys.COCKED_STATE);
		playerIn.getEntityData().setInteger("reload_tick", 0);
		if (cockedBowString > ammoIn.tagCount()) {
			this.playDropBowstringSoundEffect(playerIn);
			nbt.setInteger(NBTTagsKeys.COCKED_STATE, --cockedBowString);
			return;
		} else if (ammoIn.tagCount() <= 0) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.shotgun_hammer_click,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
			return;
		}
		Vec3d pLook = playerIn.getLookVec();
		Random rand = worldIn.rand;
		double divider = PlayerUtils.getShootingAccuracyDivider(playerIn);
		double luckValue = playerIn.getEntityAttribute(SharedMonsterAttributes.LUCK).getAttributeValue();
		boolean isCritical = rand.nextDouble() < (divider + luckValue - 4d) / 100d;
		pLook = pLook.addVector((rand.nextFloat() - .5f) / divider, (rand.nextFloat() - .5f) / divider,
				(rand.nextFloat() - .5f) / divider);
		NBTTagCompound cartridge = ammoIn.getCompoundTagAt(ammoIn.tagCount() - 1);
		this.shoot(worldIn, pLook, playerIn, isCritical, divider, cartridge.getFloat(NBTTagsKeys.DAMAGE),
				cartridge.getInteger(NBTTagsKeys.AMMO_TYPE));
		if (!playerIn.capabilities.isCreativeMode) {
			ammoIn.removeTag(ammoIn.tagCount() - 1);
			nbt.setTag(NBTTagsKeys.AMMO, ammoIn);
			if (cockedBowString > 0)
				nbt.setInteger(NBTTagsKeys.COCKED_STATE, --cockedBowString);
			itemstack.damageItem(1, playerIn);
		}
		if (playerIn.isHandActive()) {
			playerIn.resetActiveHand();
			playerIn.setActiveHand(EnumHand.MAIN_HAND);
		}
		playerIn.resetCooldown();
		IblisMod.network.resetCooldownAndActiveHand(playerIn);
	}

	protected abstract void shoot(World worldIn, Vec3d aim, EntityPlayer playerIn, boolean isCritical, double accuracy,
			float projectileDamageIn, int ammoTypeIn);

	@Nullable
	protected EntityLivingBase damageEntitiesOnPath(World world, DamageSource source, Entity shooter,
			Vec3d start, Vec3d end, float projectileDamage, float splashDamageCone) {
		EntityLivingBase lastVictim = null;
		boolean splashDamage = false;
		if (splashDamageCone != 0.0f) {
			splashDamage = true;
		}
		List<Entity> list = world.getEntitiesInAABBexcluding(shooter, (new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z)).grow(0.5d),
				BULLET_TARGETS);
		Iterator<Entity> ei = list.iterator();
		while (ei.hasNext()) {
			Entity entity = ei.next();
			if (!(entity instanceof EntityLivingBase)) {
				continue;
			}
			EntityLivingBase target = (EntityLivingBase) entity;
			if (HeadShotHandler.traceHeadShot(target, start, end) != null) {
				if (target.getHealth() < projectileDamage && target instanceof EntitySlime
						&& ((EntitySlime) target).getSlimeSize() > 1) {
					((EntitySlime) target).setSlimeSize(0, false);
				}
				target.attackEntityFrom(source, projectileDamage * 4.0f);
				lastVictim = target;
				IblisHeadshotsMod.network.spawnHeadshotParticle(world,
						target.getPositionVector().add(new Vec3d(0, 2, 0)), new Vec3d(0d, 0.2d, 0d), 15);
				continue;
			}
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
			if (splashDamage) {
				// -0.5 is right in point. 0.5 is one width shift for axis.
				float[] traceResult = IblisMathUtil.calculateOverlappingAmount(axisalignedbb, start, end);
				float threshold  = traceResult[1] * splashDamageCone;
				float precision = traceResult[0]*2.0f+1.0f;
				float overlap = 1.0f - precision  + threshold;
				if(overlap<=0.0f)
					continue;
				if(overlap>1.0f)
					overlap = 1.0f;
				overlap/=threshold;
				if(overlap>1.0f)
					overlap = 1.0f;
				target.attackEntityFrom(source, projectileDamage * overlap);
				lastVictim = target;
			} else {
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
				if (raytraceresult != null) {
					target.attackEntityFrom(source, projectileDamage);
					lastVictim = target;
				}
			}
		}
		return lastVictim;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTTagsKeys.DURABILITY))
			return stack.getTagCompound().getInteger(NBTTagsKeys.DURABILITY);
		return super.getMaxDamage(stack);
	}

	public abstract ItemStack getReloading(ItemStack stack);

	public abstract void playReloadingSoundEffect(EntityPlayer player);

	public abstract void playDropBowstringSoundEffect(EntityPlayer playerIn);
}