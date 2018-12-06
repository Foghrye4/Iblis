package iblis.ebwizardy_integration;


import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import electroblob.wizardry.constants.Element;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EBWizardyEventHandler {
	
    public static final IAttribute MAGIC = (new RangedAttribute(SharedIblisAttributes.WISDOM, "iblis.magic", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Magic").setShouldWatch(true);
    public static IAttribute[] elementToAttributeMap;
    public static PlayerSkills[] elementToSkillMap;
    
	public static void initSkills() {
		elementToAttributeMap = new IAttribute[Element.values().length];
		elementToSkillMap = new PlayerSkills[Element.values().length];
		for(Element e:Element.values()) {
			elementToAttributeMap[e.ordinal()] = (new RangedAttribute(MAGIC, "iblis.ebwizardy."+e.unlocalisedName, 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Magic").setShouldWatch(true);
			elementToSkillMap[e.ordinal()] = new PlayerSkills(e.name(), elementToAttributeMap[e.ordinal()], 0.001f);
		}
	}
	
	public static void registerAttributes(AbstractAttributeMap attributeMap) {
		attributeMap.registerAttribute(MAGIC);
		for(IAttribute attribute:elementToAttributeMap) {
			attributeMap.registerAttribute(attribute);
		}
	}
	
	@SubscribeEvent
	public void onBeforeSpellCast(SpellCastEvent.Pre event) {
		if(!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		Spell spell = event.spell;
		int elementId = spell.element.ordinal();
		PlayerSkills skill = elementToSkillMap[elementId];
		double skillLevel = skill.getFullSkillValue(player);
		skillLevel -= spell.tier.level;
		for(Entry<String, Float> entry :event.modifiers.multiplierMap.entrySet()) {
			float value = entry.getValue();
			value = (float) PlayerUtils.modifyDoubleValueBySkill(false, value, skillLevel);
			event.modifiers.set(entry.getKey(), value, false);
		}
	}
	
	@SubscribeEvent
	public void onAfterSpellCast(SpellCastEvent.Post event) {
		if(!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		Spell spell = event.spell;
		int elementId = spell.element.ordinal();
		PlayerSkills skill = elementToSkillMap[elementId];
		skill.raiseSkill(player, 1.0);
	}
	
	@SubscribeEvent
	public void onEntityConstructingEvent(EntityEvent.EntityConstructing event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			registerAttributes(player.getAttributeMap());
		}
	}

}
