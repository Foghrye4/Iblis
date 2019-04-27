package iblis.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerSkills {

	private static final List<PlayerSkills> values = new ArrayList<PlayerSkills>();
	private static final Map<String, PlayerSkills> valuesOf = new HashMap<String, PlayerSkills>();
	public static final PlayerSkills BOXING = new PlayerSkills("BOXING",SharedIblisAttributes.BOXING,0.005f);
	public static final PlayerSkills SWORDSMANSHIP = new PlayerSkills("SWORDSMANSHIP",SharedIblisAttributes.SWORDSMANSHIP,0.001f);
	public static final PlayerSkills PARRY = new PlayerSkills("PARRY",SharedIblisAttributes.PARRY,0.001f);
	public static final PlayerSkills ARCHERY = new PlayerSkills("ARCHERY",SharedIblisAttributes.ARCHERY,0.001f);
	public static final PlayerSkills THROWING = new PlayerSkills("THROWING",SharedIblisAttributes.THROWING,0.0002f);
	public static final PlayerSkills SHARPSHOOTING = new PlayerSkills("SHARPSHOOTING",SharedIblisAttributes.SHARPSHOOTING,0.001f);
	public static final PlayerSkills ARMORSMITH = new PlayerSkills("ARMORSMITH",SharedIblisAttributes.ARMORSMITH,0.02f);
	public static final PlayerSkills WEAPONSMITH = new PlayerSkills("WEAPONSMITH",SharedIblisAttributes.WEAPONSMITH,0.02f);
	public static final PlayerSkills MECHANICS = new PlayerSkills("MECHANICS",SharedIblisAttributes.MECHANICS,0.02f);
	public static final PlayerSkills MEDICAL_AID = new PlayerSkills("MEDICAL_AID",SharedIblisAttributes.MEDICAL_AID,0.02f);
	public static final PlayerSkills DIGGING = new PlayerSkills("DIGGING",SharedIblisAttributes.DIGGING,0.002f);
	public static final PlayerSkills CHEMISTRY = new PlayerSkills("CHEMISTRY",SharedIblisAttributes.CHEMISTRY,0.02f);
	public static final PlayerSkills RUNNING = new PlayerSkills("RUNNING",SharedIblisAttributes.RUNNING,0.0001f);
	public static final PlayerSkills JUMPING = new PlayerSkills("JUMPING",SharedIblisAttributes.JUMPING,0.001f);
	public static final PlayerSkills FALLING = new PlayerSkills("FALLING",SharedIblisAttributes.FALLING,0.02f);
	public static final PlayerSkills EQUILIBRIUM = new PlayerSkills("EQUILIBRIUM",SharedIblisAttributes.EQUILIBRIUM,0.001f);
	
	public double pointsPerLevel;
	public final float defaultPointsPerLevel;
	private final IAttribute attribute;
	public boolean enabled = true;
	private final String name;
	
	public PlayerSkills(String nameIn, IAttribute attributeIn, float pointsPerLevelIn){
		this.name = nameIn;
		this.attribute = attributeIn;
		this.pointsPerLevel = this.defaultPointsPerLevel = pointsPerLevelIn;
		values.add(this);
		valuesOf.put(name, this);
	}
	
	public void raiseSkill(EntityPlayer player, double d) {
		if(player.world.isRemote)
			throw new IllegalStateException("Skills should be raised only at server side.");
		int divider = 1;
		for (IAttribute iattribute = this.attribute; 
				iattribute != SharedIblisAttributes.INTELLIGENCE;
				iattribute = iattribute.getParent()) {
			double value = player.getEntityAttribute(iattribute).getBaseValue();
			value += pointsPerLevel * d / divider / (value*0.5d + 1.0d);
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

	public static List<PlayerSkills> values() {
		return values;
	}

	public String name() {
		return name;
	}

	public static PlayerSkills valueOf(String name) {
		return valuesOf.get(name);
	}

}
