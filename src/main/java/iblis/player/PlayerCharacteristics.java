package iblis.player;

import iblis.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public enum PlayerCharacteristics {
	MAX_HP(SharedMonsterAttributes.MAX_HEALTH,10d,2d),
	MELEE_DAMAGE_REDUCTION(SharedIblisAttributes.MELEE_DAMAGE_REDUCTION,0d,0.1d),
	FIRE_DAMAGE_REDUCTION(SharedIblisAttributes.FIRE_DAMAGE_REDUCTION,0d,0.1d),
	EXPLOSION_DAMAGE_REDUCTION(SharedIblisAttributes.EXPLOSION_DAMAGE_REDUCTION,0d,0.1d),
	PROJECTILE_DAMAGE_REDUCTION(SharedIblisAttributes.PROJECTILE_DAMAGE_REDUCTION,0d,0.1d),
	MELEE_DAMAGE_BONUS(SharedIblisAttributes.MELEE_DAMAGE_BONUS,0d,0.1d),
	ATTACK_SPEED(SharedMonsterAttributes.ATTACK_SPEED,4d,0.1d),
	LUCK(SharedMonsterAttributes.LUCK,0d,0.1d),
	INTELLIGENCE(SharedIblisAttributes.INTELLIGENCE,0d,0.1d),
	GLUTTONY(SharedIblisAttributes.GLUTTONY,10d,1d);
	
	public double cap = 1000.0d;
	public final double defaultPointsPerLevel;
	public final double defaultStartLevel;
	public double pointsPerLevel;
	public double startLevel;
	private final IAttribute attribute;
	public boolean enabled = true;
	PlayerCharacteristics(IAttribute attributeIn, double startLevelIn,double pointsPerLevelIn){
		this.attribute = attributeIn;
		this.startLevel = startLevelIn;
		this.pointsPerLevel = pointsPerLevelIn;
		this.defaultStartLevel = startLevelIn;
		this.defaultPointsPerLevel = pointsPerLevelIn;
	}
	
	public int getCurrentLevel(EntityPlayer player) {
		return (int) (Math.round((player.getEntityAttribute(attribute).getBaseValue()-startLevel)/pointsPerLevel)+1);
	}

	public boolean raiseCharacteristic(EntityPlayer player) {
		if(PlayerUtils.isCharacteristicCouldBeRaised(this, player)){
			player.addExperienceLevel(-getCurrentLevel(player));
			double value = player.getEntityAttribute(attribute).getBaseValue();
			value+=pointsPerLevel;
			player.getEntityAttribute(attribute).setBaseValue(value);
			if (this == MELEE_DAMAGE_BONUS) {
				IAttributeInstance aiAttackDamage = player.getAttributeMap()
						.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
				aiAttackDamage.removeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER);
				aiAttackDamage.applyModifier(new AttributeModifier(
						SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER, "Characteristic modifier",
						PlayerCharacteristics.MELEE_DAMAGE_BONUS.getCurrentValue(player), 1));
			}
			return true;
		}
		return false;
	}
	
	public double getCurrentValue(EntityLivingBase living) {
		return living.getEntityAttribute(attribute).getBaseValue();
	}
	
	public double getMaxValue(EntityPlayer player) {
		return player.getEntityAttribute(attribute).getAttribute().clampValue(cap);
	}

	public String getNiceName() {
		return attribute.getName();
	}

	public IAttributeInstance getAttributeInstance(EntityPlayer player) {
		return player.getEntityAttribute(attribute);
	}
	
	public IAttribute getAttribute() {
		return attribute;
	}

	public void resetToDefault(EntityPlayer player) {
		player.getEntityAttribute(attribute).setBaseValue(this.startLevel);
	}
}
