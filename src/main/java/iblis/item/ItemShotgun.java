package iblis.item;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.init.IblisSounds;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShotgun extends ItemFirearmsBase {

	public ItemShotgun(){
		super();
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
	protected void shoot(World worldIn, Vec3d aim, EntityPlayer playerIn, boolean isCritical, double accuracy){
		int blockReachDistance = 256;
		Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.eyeHeight, playerIn.posZ);
		Vec3d vec3d2 = vec3d.addVector(aim.x * blockReachDistance, aim.y * blockReachDistance,
				aim.z * blockReachDistance);
		RayTraceResult rtr = worldIn.rayTraceBlocks(vec3d, vec3d2, false, true, true);
		if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
			vec3d2 = rtr.hitVec;
			int bsId = Block.getStateId(worldIn.getBlockState(rtr.getBlockPos()));
			IblisMod.network.spawnBlockParticles((EntityPlayerMP) playerIn, vec3d2, aim, bsId);
		}
		double bulletDamage = playerIn.getAttributeMap()
				.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue();
		if (isCritical)
			bulletDamage *= 100d;
		List<EntityLivingBase>[] targets = this.findEntitiesOnPath(worldIn, playerIn, vec3d, vec3d2);
		DamageSource damageSource = DamageSource.causePlayerDamage(playerIn);
		damageSource.setProjectile();
		damageSource.damageType = "shotgun";
		for (EntityLivingBase target : targets[0]) {
			target.attackEntityFrom(damageSource, (float) bulletDamage);
			if (isCritical)
				IblisMod.network.spawnParticles((EntityPlayerMP) playerIn,
						target.getPositionVector().add(new Vec3d(0, 1, 0)), aim, EnumParticleTypes.CRIT);
		}
		bulletDamage *= 4f;
		for (EntityLivingBase target : targets[1]) {
			if (target.getHealth() < bulletDamage && target instanceof EntitySlime
					&& ((EntitySlime) target).getSlimeSize() > 1) {
				((EntitySlime) target).setSlimeSize(0, false);
			}
			target.attackEntityFrom(damageSource, (float) bulletDamage);
			IblisMod.network.spawnCustomParticle(playerIn.world,
					target.getPositionVector().add(new Vec3d(0, 2, 0)), new Vec3d(0d, 0.2d, 0d),
					IblisParticles.HEADSHOT);
		}
		Vec3d rightHandPos = PlayerUtils.getRightHandPosition(playerIn);
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, playerIn.posX + rightHandPos.x,
				playerIn.posY + rightHandPos.y + playerIn.eyeHeight, playerIn.posZ + rightHandPos.z, 0, 0.1, 0);
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.shoot,
				SoundCategory.PLAYERS, 1.0f, 1.0f);
	}


	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedIblisAttributes.PROJECTILE_DAMAGE.getName(),
					new AttributeModifier(SharedIblisAttributes.BULLET_DAMAGE_MODIFIER, "Weapon modifier", 12.0d, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, 0));
		}
		return multimap;
	}
	
	@Override
	public ItemStack getReloading(ItemStack stack) {
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		ItemStack loadingGun = new ItemStack(IblisItems.SHOTGUN_RELOADING,1,stack.getItemDamage());
		loadingGun.setTagCompound(stack.getTagCompound());
		return loadingGun;
	}

	@Override
	public void playReloadingSoundEffect(EntityPlayerMP player) {
		World world = player.getEntityWorld();
		world.playSound(null, player.posX, player.posY, player.posZ, IblisSounds.shotgun_charging,
				SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.2f + 0.8f);
	}
}
