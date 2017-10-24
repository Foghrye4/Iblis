package iblis.item;


import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis.player.SharedIblisAttributes;
import iblis.util.NBTTagsKeys;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

public class ItemHeavyShield extends ItemShield {

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.OFFHAND) {
			multimap.put(SharedIblisAttributes.RUNNING.getName(),
					new AttributeModifier(SharedIblisAttributes.SHIELD_RUNNING_MODIFIER, "Running skill modifier", -0.4d, 1));
		}
		return multimap;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTTagsKeys.DURABILITY))
			return stack.getTagCompound().getInteger(NBTTagsKeys.DURABILITY);
		return super.getMaxDamage(stack);
	}
	
	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
		return true;
	}
}
