package iblis.player;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public enum PlayerSkills {

	SWORDSMANSHIP(SharedIblisAttributes.SWORDSMANSHIP,0.001f),
	ARCHERY(SharedIblisAttributes.ARCHERY,0.001f),
	SHARPSHOOTING(SharedIblisAttributes.SHARPSHOOTING,0.001f),
	ARMORSMITH(SharedIblisAttributes.ARMORSMITH,0.02f),
	WEAPONSMITH(SharedIblisAttributes.WEAPONSMITH,0.02f),
	MEDICAL_AID(SharedIblisAttributes.MEDICAL_AID,0.02f),
	DIGGING(SharedIblisAttributes.DIGGING,0.001f),
	RUNNING(SharedIblisAttributes.RUNNING,0.001f),
	JUMPING(SharedIblisAttributes.JUMPING,0.001f), 
	FALLING(SharedIblisAttributes.FALLING,0.02f);
	
	public double pointsPerLevel;
	public final float defaultPointsPerLevel;
	private final IAttribute attribute;
	public boolean enabled = true;
	PlayerSkills(IAttribute attributeIn, float pointsPerLevelIn){
		this.attribute = attributeIn;
		this.pointsPerLevel = this.defaultPointsPerLevel = pointsPerLevelIn;
	}
	
	public void raiseSkill(EntityPlayer player, double d) {
		if(player.world.isRemote)
			throw new IllegalStateException("Skills should be raised only at server side.");
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
	
	public double getFullSkillValue(EntityPlayer entityLivingBase) {
		if(!enabled)
			return 0d;
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

	public IAttributeInstance getAttributeInstance(EntityPlayer player) {
		return player.getEntityAttribute(attribute);
	}
	
	public IAttribute getAttribute() {
		return attribute;
	}

}
