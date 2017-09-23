package iblis.item;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShotgun extends Item {

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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (itemstack.getTagCompound() == null)
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		NBTTagCompound nbt = itemstack.getTagCompound();
		int ammoIn = nbt.getInteger("ammo");
		if (!worldIn.isRemote)
			playerIn.getEntityData().setInteger("reload_tick", 0);
		if (ammoIn == 0) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.shotgun_hammer_click,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		} else {
			playerIn.resetCooldown();
			Vec3d pLook = playerIn.getLookVec();
			if (!worldIn.isRemote) {
				int blockReachDistance = 256;
				Random rand = worldIn.rand;
				double sharpshootingSkillValue = PlayerSkills.SHARPSHOOTING.getFullSkillValue(playerIn);
				double luckValue = playerIn.getEntityAttribute(SharedMonsterAttributes.LUCK).getAttributeValue();
				boolean isCritical = rand.nextDouble()<(sharpshootingSkillValue+luckValue-8d)/100d;
				double divider = (sharpshootingSkillValue + 1d) * (1d + playerIn.getCooledAttackStrength(0.0F))
						* (playerIn.isSneaking() ? 2d : 1d) * (playerIn.isSprinting() ? 0.5d : 1d);
				pLook = pLook.addVector((rand.nextFloat() - .5f) / divider, (rand.nextFloat() - .5f) / divider,
						(rand.nextFloat() - .5f) / divider);
				Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.eyeHeight, playerIn.posZ);
				Vec3d vec3d2 = vec3d.addVector(pLook.x * blockReachDistance, pLook.y * blockReachDistance,
						pLook.z * blockReachDistance);
				RayTraceResult rtr = worldIn.rayTraceBlocks(vec3d, vec3d2, false, true, true);
				if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
					vec3d2 = rtr.hitVec;
					int bsId = Block.getStateId(worldIn.getBlockState(rtr.getBlockPos()));
					IblisMod.network.spawnBlockParticles((EntityPlayerMP) playerIn, vec3d2, pLook, bsId);
				}
				double bulletDamage = playerIn.getAttributeMap()
						.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue();
				if(isCritical)
					bulletDamage*=100d;
				List<Entity> targets = this.findEntitiesOnPath(worldIn, playerIn, vec3d, vec3d2);
				DamageSource damageSource = DamageSource.causePlayerDamage(playerIn);
				damageSource.setProjectile();
				damageSource.damageType="shotgun";
				for (Entity target : targets) {
					target.attackEntityFrom(damageSource, (float) bulletDamage);
					if(isCritical)
						IblisMod.network.spawnParticles((EntityPlayerMP) playerIn, target.getPositionVector().add(new Vec3d(0,1,0)), pLook, EnumParticleTypes.CRIT);
				}
				nbt.setInteger("ammo", --ammoIn);
		        itemstack.damageItem(1, playerIn);
			}
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, playerIn.posX + pLook.x,
					playerIn.posY + pLook.y + playerIn.eyeHeight, playerIn.posZ + pLook.z, 0, 0.1, 0);
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.shoot,
					SoundCategory.PLAYERS, 1.0f, 1.0f);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
	}

	@Nullable
	protected List<Entity> findEntitiesOnPath(World world, Entity shooter, Vec3d start, Vec3d end) {
		List<Entity> list = world.getEntitiesInAABBexcluding(shooter, new AxisAlignedBB(start, end), BULLET_TARGETS);
		Iterator<Entity> ei = list.iterator();
		while (ei.hasNext()) {
			Entity entity = ei.next();
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
			if (raytraceresult == null) {
				ei.remove();
			}
		}
		return list;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		int[] oreIds = OreDictionary.getOreIDs(repair);
		for (int oreId : oreIds) {
			if (OreDictionary.getOreName(oreId).equals("ingotSteel"))
				return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedIblisAttributes.PROJECTILE_DAMAGE.getName(),
					new AttributeModifier(SharedIblisAttributes.BULLET_DAMAGE_MODIFIER, "Weapon modifier", 4.0d, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, 0));
		}
		return multimap;
	}
	
	public ItemStack getReloading(ItemStack stack) {
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		ItemStack loadingGun = new ItemStack(IblisItems.SHOTGUN_RELOADING,1,stack.getItemDamage());
		loadingGun.setTagCompound(stack.getTagCompound());
		return loadingGun;
	}

}
