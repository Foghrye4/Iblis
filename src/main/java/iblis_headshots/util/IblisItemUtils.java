package iblis_headshots.util;

import com.google.common.collect.Multimap;

import iblis_headshots.item.HelmetsConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.ResourceLocation;

public class IblisItemUtils {
	private static AttributeMap attributeMap = new AttributeMap();
	private static IAttributeInstance armor = new ModifiableAttributeInstance(attributeMap,
			SharedMonsterAttributes.ARMOR);
	private static IAttributeInstance armorToughness = new ModifiableAttributeInstance(attributeMap,
			SharedMonsterAttributes.ARMOR);

	public static float getHeadgearProtection(ItemStack stack) {
		ResourceLocation registryName = stack.getItem().getRegistryName();
		if(HelmetsConfig.HELMETS_REGISTRY.containsKey(registryName)) {
			return HelmetsConfig.HELMETS_REGISTRY.getFloat(registryName);
		}
		Multimap<String, AttributeModifier> aMods = stack.getAttributeModifiers(EntityEquipmentSlot.HEAD);
		if (aMods.isEmpty())
			return 1.0f;
		for (AttributeModifier mod : aMods.get(armor.getAttribute().getName()))
			armor.applyModifier(mod);
		for (AttributeModifier mod : aMods.get(armorToughness.getAttribute().getName()))
			armorToughness.applyModifier(mod);
		float headGearDamageAbsorbMultiplier = CombatRules.getDamageAfterAbsorb(1.0f, (float) armor.getAttributeValue(),
				(float) armorToughness.getAttributeValue());
		float headGearDamageAbsorbMultiplier2 = headGearDamageAbsorbMultiplier * headGearDamageAbsorbMultiplier;
		headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
		headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
		headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
		for (AttributeModifier mod : aMods.get(armor.getAttribute().getName()))
			armor.removeModifier(mod);
		for (AttributeModifier mod : aMods.get(armorToughness.getAttribute().getName()))
			armorToughness.removeModifier(mod);
		return headGearDamageAbsorbMultiplier2;
	}

}
