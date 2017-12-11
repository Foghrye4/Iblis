package iblis.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.entity.EntityBoulder;
import iblis.entity.EntityThrowingKnife;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemThrowingWeapon extends Item {

	private final ThrowableType type;

	public ItemThrowingWeapon(ThrowableType typeIn) {
		super();
		type = typeIn;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (!playerIn.capabilities.isCreativeMode) {
			itemstack.shrink(1);
		}
		worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
				SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if (!worldIn.isRemote) {
			IProjectile entity = null;
			Vec3d rightHandPos = PlayerUtils.getRightHandPosition(playerIn);
			switch (type) {
			case BOULDER:
				entity = new EntityBoulder(worldIn, playerIn, playerIn.posX + rightHandPos.x,
						playerIn.posY + rightHandPos.y + playerIn.eyeHeight, playerIn.posZ + rightHandPos.z);
				break;
			case IRON_KNIFE:
				entity = new EntityThrowingKnife(worldIn, playerIn, playerIn.posX + rightHandPos.x,
						playerIn.posY + rightHandPos.y + playerIn.eyeHeight, playerIn.posZ + rightHandPos.z);
				break;
			}
			double skill = PlayerSkills.THROWING.getFullSkillValue(playerIn) + 1.0d;
			float speed = 0.4f * playerIn.getCooledAttackStrength(0.0F) + (float) skill * playerIn.getCooledAttackStrength(0.0F) / 10f;
			speed /= type.weight;
			float rotationPitchIn = playerIn.rotationPitch;
			float rotationYawIn = playerIn.rotationYaw - 1f;
			float x = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
			float y = -MathHelper.sin((rotationPitchIn) * 0.017453292F);
			float z = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
			double mx = playerIn.motionX;
			double my = playerIn.motionY;
			double mz = playerIn.motionZ;
			speed += MathHelper.sqrt(mx * mx + my * my + mz * mz);
			entity.setThrowableHeading(x + mx, y + my, z + mz, speed, (float) (10.0f / skill));
			playerIn.resetCooldown();
			worldIn.spawnEntity((Entity) entity);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedIblisAttributes.PROJECTILE_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", type.damage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1.4000000953674316D, 0));
		}
		return multimap;
	}

	public enum ThrowableType {
		BOULDER(2.0f, 1.0f), 
		IRON_KNIFE(1.0f, 2.0f);
		public final float weight;
		public final float damage;

		ThrowableType(float weightIn, float damageIn) {
			weight = weightIn;
			damage = damageIn;
		}
	}
}
