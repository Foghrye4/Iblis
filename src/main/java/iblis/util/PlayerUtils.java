package iblis.util;

import java.util.Iterator;

import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.world.WorldSavedDataPlayers;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlayerUtils {

	public static final int MAX_SPRINT_SPEED = 32;
	private static final Int2IntMap knockState = new Int2IntOpenHashMap();
	private static final Int2IntMap sprintingButtonCounterState = new Int2IntOpenHashMap();
	private static final Int2ObjectMap<float[]> aimState = new Int2ObjectOpenHashMap<float[]>();
	public static final int KNOCK_BY_SHIELD = 1;
	public static final int KNOCK_BY_KICK = 2;
	
	private final static String[] ATTRIBUTES_AFFECTED_BY_CRAFTING_SKILL = new String[] {
			SharedMonsterAttributes.ARMOR.getName(), 
			SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
			SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
			SharedIblisAttributes.PROJECTILE_DAMAGE.getName(),
			SharedIblisAttributes.MELEE_DAMAGE_REDUCTION.getName(),
			SharedIblisAttributes.PROJECTILE_DAMAGE_REDUCTION.getName(),
			SharedIblisAttributes.FIRE_DAMAGE_REDUCTION.getName(),
			SharedIblisAttributes.EXPLOSION_DAMAGE_REDUCTION.getName()
	};

	public static boolean isCharacteristicsCouldBeRaised(EntityPlayer player) {
		for (PlayerCharacteristics characteristic : PlayerCharacteristics.values()) {
			if (isCharacteristicCouldBeRaised(characteristic, player)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCharacteristicCouldBeRaised(PlayerCharacteristics characteristic, EntityPlayer player) {
		if(!characteristic.enabled)
			return false;
		int experienceLevel = player.experienceLevel;
		long characteristicLevel = characteristic.getCurrentLevel(player);
		if (characteristicLevel <= experienceLevel
				&& characteristic.getCurrentValue(player) < characteristic.getMaxValue(player)) {
			return true;
		}
		return false;
	}

	public static double getQualityModifierValue(double skillValue, ItemStack output1,
			EntityEquipmentSlot slot, String attributeName, boolean additive) {
		double baseValue = 0;
		Iterator<AttributeModifier> ami = output1.getAttributeModifiers(slot).get(attributeName).iterator();
		while (ami.hasNext()) {
			AttributeModifier am = ami.next();
			baseValue += am.getAmount();
		}
		System.out.println("base value " + baseValue);
		if (isAttributeAffectedByCraftingSkill(attributeName)) {
			return modifyDoubleValueBySkill(additive, baseValue, skillValue);
		}
		return baseValue;
	}
	
	public static double modifyDoubleValueBySkill(boolean additive, double baseValue, double skillValue){
		if(additive)
			return baseValue + skillValue * 0.1d;
		if (skillValue < 0d)
			return baseValue / (1.0d - skillValue);
		if (skillValue > 0d)
			return baseValue * skillValue * 0.1d + baseValue;
		return baseValue;
	}
	
	public static int modifyIntValueBySkill(boolean additive, int baseValue, double skillValue){
		if(additive)
			return MathHelper.ceil(baseValue + skillValue * 0.1d);
		if (skillValue < 0d)
			return MathHelper.floor(baseValue / (1.0d - skillValue));
		if (skillValue > 0d)
			return MathHelper.ceil(baseValue * skillValue * 0.1d + baseValue);
		return baseValue;
	}

	private static boolean isAttributeAffectedByCraftingSkill(String attributeNameIn) {
		for (String attributeName : ATTRIBUTES_AFFECTED_BY_CRAFTING_SKILL)
			if (attributeNameIn.equals(attributeName))
				return true;
		return false;
	}
	
	public static void applySprintingSpeedModifier(EntityPlayer player, int sprintCounter){
		IAttributeInstance msi = player.getAttributeMap()
				.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		msi.removeModifier(SharedIblisAttributes.SPRINTING_SPEED_MODIFIER);
		if (sprintCounter > 0 && PlayerSkills.RUNNING.enabled) {
			double ss = (PlayerSkills.RUNNING.getFullSkillValue(player) - 0.1) * 0.1;
			if (ss > 0)
				ss = ss * sprintCounter / PlayerUtils.MAX_SPRINT_SPEED;
			msi.applyModifier(new AttributeModifier(SharedIblisAttributes.SPRINTING_SPEED_MODIFIER,
						"Sprinting speed boost", ss, 2));
		}
	}

	public static void saveSprintButtonCounterState(EntityPlayer player, int sprintButtonCounter) {
		sprintingButtonCounterState.put(player.getEntityId(), sprintButtonCounter);
	}
	
	public static int getSprintButtonCounterState(EntityPlayer player) {
		// Return default value if not specified. '0' for Java.
		return sprintingButtonCounterState.get(player.getEntityId());
	}
	
	public static void saveKnockState(EntityPlayer player, int state) {
		knockState.put(player.getEntityId(), state);
	}
	
	public static int getKnockState(EntityPlayer player) {
		// Return default value if not specified. '0' for Java.
		return knockState.get(player.getEntityId());
	}

	public static boolean canJump(EntityPlayer player) {
		return player.getFoodStats().getFoodLevel() > 6;
	}

	public static WorldSavedDataPlayers getOrCreateWorldSavedData(World worldIn) {
		WorldSavedDataPlayers playersData = (WorldSavedDataPlayers) worldIn.getPerWorldStorage().getOrLoadData(WorldSavedDataPlayers.class, WorldSavedDataPlayers.DATA_IDENTIFIER);
		if(playersData == null){
			playersData = new WorldSavedDataPlayers(WorldSavedDataPlayers.DATA_IDENTIFIER);
			worldIn.getPerWorldStorage().setData(WorldSavedDataPlayers.DATA_IDENTIFIER, playersData);
		}
		return playersData;
	}

	public static Vec3d getRightHandPosition(EntityPlayer player) {
		return getVectorForRotation(player.rotationPitch - 15f, player.rotationYaw + 9f);
	}
	
	public static Vec3d getVectorForRotation(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
	}

	public static double getShootingAccuracyDivider(EntityPlayer playerIn) {
		int useCount = 0;
		if(playerIn.isHandActive())
			useCount = playerIn.getHeldItemMainhand().getMaxItemUseDuration() - playerIn.getItemInUseCount();
		double sharpshootingSkillValue = PlayerSkills.SHARPSHOOTING.getFullSkillValue(playerIn);
		return (sharpshootingSkillValue + 1d + useCount * 0.1) * (1d + playerIn.getCooledAttackStrength(0.0F))
		* (playerIn.isSneaking() ? 2d : 1d) * (playerIn.isSprinting() ? 0.5d : 1d);
	}

	/**
	 * @return is pitch and yaw changed for more than 12 degrees **/
	public static boolean saveAndComparePitchAndYaw(EntityLivingBase player) {
		float[] savedState = aimState.get(player.getEntityId());
		if (savedState == null) {
			savedState = new float[] { player.rotationPitch, player.rotationYaw };
			aimState.put(player.getEntityId(), savedState);
			return false;
		}
		float dp = savedState[0] - player.rotationPitch;
		float dy = savedState[1] - player.rotationYaw;
		savedState[0] = player.rotationPitch;
		savedState[1] = player.rotationYaw;
		if(dp*dp+dy*dy>144f)
			return true;
		return false;
	}
}
