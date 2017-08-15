package iblis.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public enum PlayerSkills {

	SWORDSMANSHIP(SharedIblisAttributes.SWORDSMANSHIP,0.001d),
	ARCHERY(SharedIblisAttributes.ARCHERY,0.001d),
	SHARPSHOOTING(SharedIblisAttributes.SHARPSHOOTING,0.001d),
	ARMORSMITH(SharedIblisAttributes.ARMORSMITH,0.01d),
	WEAPONSMITH(SharedIblisAttributes.WEAPONSMITH,0.01d);
	
	private final double pointsPerLevel;
	private final IAttribute attribute;
	PlayerSkills(IAttribute attributeIn, double pointsPerLevelIn){
		this.attribute = attributeIn;
		this.pointsPerLevel = pointsPerLevelIn;
	}
	
	public void raiseSkill(EntityPlayer player, double d) {
		int divider = 1;
		for (IAttribute iattribute = this.attribute; 
				iattribute != SharedIblisAttributes.INTELLIGENCE;
				iattribute = iattribute.getParent()) {
			double value = player.getEntityAttribute(iattribute).getBaseValue();
			value+=pointsPerLevel*d/divider;
			player.getEntityAttribute(iattribute).setBaseValue(value);
			divider<<=2;
		}
	}
	
	public void raiseSkillTo(EntityPlayer playerIn, double raiseTo) {
		int divider = 1;
		double oldValue = playerIn.getEntityAttribute(attribute).getBaseValue();
		double difference = raiseTo-oldValue;
		for (IAttribute iattribute = this.attribute; 
				iattribute != SharedIblisAttributes.INTELLIGENCE;
				iattribute = iattribute.getParent()) {
			double value = playerIn.getEntityAttribute(iattribute).getBaseValue();
			value+=difference/divider;
			playerIn.getEntityAttribute(iattribute).setBaseValue(value);
			divider<<=2;
		}
	}

	public double getCurrentValue(EntityPlayer player) {
		return player.getEntityAttribute(attribute).getBaseValue();
	}
	
	public double getFullSkillValue(EntityLivingBase entityLivingBase) {
		double value = 0d;
		for (IAttribute iattribute = this.attribute; 
				iattribute != null;
				iattribute = iattribute.getParent()) {
			value += entityLivingBase.getEntityAttribute(iattribute).getAttributeValue();
		}
		return value;
	}

	public double getMaxValue(EntityPlayer player) {
		return player.getEntityAttribute(attribute).getAttribute().clampValue(Double.MAX_VALUE);
	}

	public String getNiceName() {
		return attribute.getName();
	}

	public IAttributeInstance getAttributeInstance(EntityPlayerMP player) {
		return player.getEntityAttribute(attribute);
	}
	
	public IAttribute getAttribute() {
		return attribute;
	}

}
