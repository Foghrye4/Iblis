package iblis.player;

import java.util.Iterator;

import iblis.world.WorldSavedDataPlayers;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
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

	public static final int MAX_SPRINT_SPEED = 64;
	private static final Int2IntMap sprintingButtonCounterState = new Int2IntOpenHashMap();
	
	private final static String[] ATTRIBUTES_AFFECTED_BY_CRAFTING_SKILL = new String[] {
			SharedMonsterAttributes.ARMOR.getName(), 
			SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
			SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
			SharedIblisAttributes.PROJECTILE_DAMAGE.getName()};

	public static boolean isCharacteristicsCouldBeRaised(EntityPlayer player) {
		for (PlayerCharacteristics characteristic : PlayerCharacteristics.values()) {
			if (isCharacteristicCouldBeRaised(characteristic, player)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCharacteristicCouldBeRaised(PlayerCharacteristics characteristic, EntityPlayer player) {
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
		if (isAttributeAffectedByCraftingSkill(attributeName)) {
			return modifyDoubleValueBySkill(additive, baseValue, skillValue);
		}
		return baseValue;
	}
	
	public static double modifyDoubleValueBySkill(boolean additive, double baseValue, double skillValue){
		if(additive)
			return baseValue + skillValue * 0.1d;
		if (skillValue < 0d)
			return baseValue / (1.0d - skillValue * 0.2d);
		if (skillValue > 0d)
			return baseValue * skillValue * 0.1d + baseValue;
		return baseValue;
	}
	
	public static int modifyIntValueBySkill(boolean additive, int baseValue, double skillValue){
		if(additive)
			return MathHelper.ceil(baseValue + skillValue * 0.1d);
		if (skillValue < 0d)
			return MathHelper.floor(baseValue / (1.0d - skillValue * 0.2d));
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
		// Return default value if not specified. '0' for java.
		return sprintingButtonCounterState.get(player.getEntityId());
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
		return getVectorForRotation(player.rotationPitch - 15f, player.rotationYaw + 18f);
	}
	
	private static Vec3d getVectorForRotation(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * 0.017453292F);
		float f3 = MathHelper.sin(-pitch * 0.017453292F);
		return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
	}
}
