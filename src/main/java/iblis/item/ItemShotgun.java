package iblis.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.init.IblisSounds;
import iblis.player.SharedIblisAttributes;
import iblis.util.BloodHandler;
import iblis.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
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
	protected void shoot(World worldIn, Vec3d aim, EntityPlayer playerIn, boolean isCritical, double accuracy, float projectileDamageIn, int ammoTypeIn){
		int blockReachDistance = 256;
		Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.eyeHeight, playerIn.posZ);
		Vec3d vec3d2 = vec3d.addVector(aim.x * blockReachDistance, aim.y * blockReachDistance,
				aim.z * blockReachDistance);
		RayTraceResult rtr = worldIn.rayTraceBlocks(vec3d, vec3d2, false, true, true);
		boolean addDecal = false;
		if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
			vec3d2 = rtr.hitVec;
			BlockPos pos = rtr.getBlockPos();
			IBlockState bstate = worldIn.getBlockState(pos);
			while(rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK 
					&& bstate.getMaterial() == Material.LEAVES){
				Vec3d vec3d3 = vec3d2.addVector(aim.x * 1.8, aim.y * 1.8, aim.z * 1.8);
				Vec3d vec3d4 = vec3d2.addVector(aim.x * blockReachDistance, aim.y * blockReachDistance,
						aim.z * blockReachDistance);
				rtr = worldIn.rayTraceBlocks(vec3d3, vec3d4, false, true, true);
				if(rtr!=null) {
					vec3d2 = rtr.hitVec;
					pos = rtr.getBlockPos();
					bstate = worldIn.getBlockState(pos);
				}
			}
			int bsId = Block.getStateId(bstate);
			IblisMod.network.spawnBlockParticles((EntityPlayerMP) playerIn, vec3d2, aim, bsId);
			if ((bstate.getMaterial() == Material.GLASS || bstate.getMaterial() == Material.ICE)
					&& bstate.getBlockHardness(worldIn, pos) < 0.6f) {
				if (!MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(worldIn, pos, bstate, playerIn))) {
	                IblisMod.network.playEvent(playerIn, 2001, pos, Block.getStateId(bstate));
					Block block = bstate.getBlock();
					// Explosion to avoid NPE
					Explosion explosion = new Explosion(worldIn, playerIn, vec3d2.x, vec3d2.y, vec3d2.z, 1, false, true);
					if (block.canDropFromExplosion(explosion)) {
						bstate.getBlock().dropBlockAsItemWithChance(worldIn, pos, bstate, 1.0F, 0);
					}
					block.onBlockExploded(worldIn, pos, explosion);
				}
			}
			else {
				addDecal = true;
			}
		}
		float bulletDamage = (float) playerIn.getAttributeMap()
				.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue();
		bulletDamage *=projectileDamageIn;
		if (isCritical)
			bulletDamage *= 100f;
		float splashDamageCone = ammoTypeIn==0?0.0f:0.02f;
		DamageSource damageSource = DamageSource.causePlayerDamage(playerIn);
		damageSource.setProjectile();
		damageSource.damageType = "shotgun";
		EntityLivingBase lastHit = this.damageEntitiesOnPath(worldIn, damageSource, playerIn, vec3d, vec3d2, bulletDamage, splashDamageCone);
		if (addDecal) {
			if(ammoTypeIn == 0) {
				IblisMod.network.addDecal(worldIn, vec3d2, IblisParticles.BULLET_HOLE, rtr.sideHit, -1, 0.6f);
			}
			else {
				IblisMod.network.addDecal(worldIn, vec3d2, IblisParticles.TRACE_OF_SHOT, rtr.sideHit, -1, (float)vec3d.distanceTo(vec3d2)*splashDamageCone*2.0f + 0.3f);
			}
			if (lastHit != null) {
				AxisAlignedBB axisalignedbb = lastHit.getEntityBoundingBox();
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
				if (raytraceresult != null) {
					Vec3d from = raytraceresult.hitVec;
					Vec3d to = new Vec3d(from.x + aim.x*4.0,from.y + aim.y*4.0 - 2.0,from.z + aim.z*4.0);
					rtr = worldIn.rayTraceBlocks(from, to, false, true, true);
					if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
						int bc = BloodHandler.getBloodColour(lastHit);
						if(bc!=-1)
							IblisMod.network.addDecal(worldIn, rtr.hitVec, IblisParticles.BLOOD_SPLATTER, rtr.sideHit,
								bc, 1.6f);
					}
				}
			}
		}
		Vec3d rightHandPos = Vec3d.ZERO;
		if(!playerIn.isHandActive())
			rightHandPos = PlayerUtils.getRightHandPosition(playerIn);
		IblisMod.network.spawnParticle(playerIn, playerIn.posX + rightHandPos.x, playerIn.posY + rightHandPos.y + playerIn.eyeHeight, playerIn.posZ + rightHandPos.z, 
				0.0d,0.1d,0.0d,EnumParticleTypes.SMOKE_NORMAL);
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
	public void playReloadingSoundEffect(EntityPlayer player) {
		World world = player.getEntityWorld();
		world.playSound(null, player.posX, player.posY, player.posZ, IblisSounds.shotgun_charging,
				SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.2f + 0.8f);
	}

	@Override
	public void playDropBowstringSoundEffect(EntityPlayer playerIn) {}
}
