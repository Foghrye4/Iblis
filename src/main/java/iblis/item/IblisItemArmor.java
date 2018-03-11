package iblis.item;

import java.util.UUID;

import com.google.common.collect.Multimap;

import iblis.constants.NBTTagsKeys;
import iblis.player.SharedIblisAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IblisItemArmor extends ItemArmor {

    private static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	public IblisItemArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		String repairOreDictName = "ingot" + this.getArmorMaterial().toString();
		for (int oreId : OreDictionary.getOreIDs(repair)) {
			if (repairOreDictName.equalsIgnoreCase(OreDictionary.getOreName(oreId)))
				return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTTagsKeys.DURABILITY))
			return stack.getTagCompound().getInteger(NBTTagsKeys.DURABILITY);
		return super.getMaxDamage(stack);
	}

	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if (equipmentSlot == this.armorType) {
			multimap.put(SharedIblisAttributes.EXPLOSION_DAMAGE_REDUCTION.getName(),
					new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Explosion damage reduction modifier", this.toughness / 2d, 0));
			multimap.put(SharedIblisAttributes.FIRE_DAMAGE_REDUCTION.getName(),
					new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Fire damage reduction modifier", this.toughness / 2d, 0));
			multimap.put(SharedIblisAttributes.PROJECTILE_DAMAGE_REDUCTION.getName(),
					new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Projectile damage reduction modifier", this.toughness / 2d, 0));
			multimap.put(SharedIblisAttributes.MELEE_DAMAGE_REDUCTION.getName(),
					new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Melee damage reduction modifier", this.toughness / 2d, 0));
		}
		return multimap;
	}

}
