package iblis.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.init.IblisSounds;
import iblis.util.HeadShotHandler;
import iblis.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
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
		int ammoIn = nbt.getInteger(NBTTagsKeys.AMMO);
		int cockedBowString = nbt.getInteger(NBTTagsKeys.COCKED_STATE);
		playerIn.getEntityData().setInteger("reload_tick", 0);
		if (cockedBowString > ammoIn) {
			this.playDropBowstringSoundEffect(playerIn);
			nbt.setInteger(NBTTagsKeys.COCKED_STATE, --cockedBowString);
			return;
		} else if (ammoIn <= 0) {
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
		this.shoot(worldIn, pLook, playerIn, isCritical, divider);
		if (!playerIn.capabilities.isCreativeMode) {
			nbt.setInteger(NBTTagsKeys.AMMO, --ammoIn);
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

	protected abstract void shoot(World worldIn, Vec3d aim, EntityPlayer playerIn, boolean isCritical, double accuracy);

	@SuppressWarnings("unchecked")
	@Nullable
	protected List<EntityLivingBase>[] findEntitiesOnPath(World world, Entity shooter, Vec3d start, Vec3d end) {
		List<Entity> list = world.getEntitiesInAABBexcluding(shooter, (new AxisAlignedBB(start, end)).grow(0.5d),
				BULLET_TARGETS);
		List<EntityLivingBase> headShots = new ArrayList<EntityLivingBase>();
		Iterator<Entity> ei = list.iterator();
		while (ei.hasNext()) {
			Entity entity = ei.next();
			if (!(entity instanceof EntityLivingBase)) {
				ei.remove();
				continue;
			}
			if (HeadShotHandler.traceHeadShot((EntityLivingBase) entity, start, end) != null) {
				headShots.add((EntityLivingBase) entity);
				ei.remove();
				continue;
			}
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
			if (raytraceresult == null) {
				ei.remove();
			}
		}
		return new List[] { list, headShots };
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