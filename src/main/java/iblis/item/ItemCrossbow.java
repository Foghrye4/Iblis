package iblis.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.entity.EntityCrossbowBolt;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCrossbow extends ItemFirearmsBase {

	public ItemCrossbow(){
		super();
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		int[] oreIds = OreDictionary.getOreIDs(repair);
		for (int oreId : oreIds) {
			if (OreDictionary.getOreName(oreId).equals("plankWood"))
				return true;
		}
		return super.getIsRepairable(toRepair, repair);
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
		ItemStack loadingGun = new ItemStack(IblisItems.CROSSBOW_RELOADING,1,stack.getItemDamage());
		loadingGun.setTagCompound(stack.getTagCompound());
		return loadingGun;
	}

	@Override
	protected void shoot(World worldIn, Vec3d aim, EntityPlayer playerIn, boolean isCritical, double accuracy, float projectileDamageIn, int ammoTypeIn) {
			EntityCrossbowBolt entity = null;
			float rotationPitchIn = playerIn.rotationPitch - 1f;
			float rotationYawIn = playerIn.rotationYaw;
			if(playerIn.isHandActive()) {
				entity = new EntityCrossbowBolt(worldIn, playerIn, playerIn.posX,
						playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
			}
			else {
				Vec3d rightHandPos = PlayerUtils.getVectorForRotation(rotationPitchIn+4f, rotationYawIn + 4f);
				entity = new EntityCrossbowBolt(worldIn, playerIn, playerIn.posX + rightHandPos.x,
						playerIn.posY + rightHandPos.y + playerIn.getEyeHeight(), playerIn.posZ + rightHandPos.z);
				rotationYawIn--;
				rotationPitchIn--;
			}
			float speed = 8f;
			double mx = playerIn.motionX;
			double my = playerIn.motionY;
			double mz = playerIn.motionZ;
			speed += MathHelper.sqrt(mx * mx + my * my + mz * mz);
			entity.setAim(playerIn, rotationPitchIn, rotationYawIn, 0, speed, (float) (10d/accuracy));
			float damage = (float) playerIn.getAttributeMap()
					.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue();
			damage *= projectileDamageIn;
			if (isCritical)
				damage *= 100d;
			entity.setDamage(damage);
			playerIn.resetCooldown();
			worldIn.spawnEntity((Entity) entity);
			this.playDropBowstringSoundEffect(playerIn);
	}
	
	@Override
	public void playDropBowstringSoundEffect(EntityPlayer playerIn) {
		World worldIn = playerIn.world;
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.crossbow_shot,
				SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
	}
	
	@Override
	public void playReloadingSoundEffect(EntityPlayer player) {}
}
