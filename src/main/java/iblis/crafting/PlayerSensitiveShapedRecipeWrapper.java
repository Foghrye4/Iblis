package iblis.crafting;

import java.util.Map.Entry;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerSensitiveShapedRecipeWrapper extends ShapedRecipeRaisingSkillWrapper {

	double minimalSkill = 0;

	public PlayerSensitiveShapedRecipeWrapper(IRecipe recipeIn) {
		super(recipeIn);
	}

	public PlayerSensitiveShapedRecipeWrapper setSesitiveTo(PlayerSkills skillIn, double requiredskill, double skillXPIn) {
		super.setSesitiveTo(skillIn, skillXPIn);
		minimalSkill = requiredskill;
		return this;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack output = super.getCraftingResult(inv);
		if(!this.sensitiveSkill.enabled)
			return output;
		double skillValue = IblisMod.proxy.getPlayerSkillValue(sensitiveSkill, inv);
		skillValue -= minimalSkill;
		return getCraftingResult(output, skillValue, false);
	}
	
	/** Modify item stack and return it instance. Does not create a copy. */
	public static ItemStack getCraftingResult(ItemStack output1, double skillValue, boolean additive){
		if (!output1.hasTagCompound())
			output1.setTagCompound(new NBTTagCompound());
		output1.getTagCompound().setInteger("quality", (int) skillValue);
		NBTTagList attributeModifiersNBTList = output1.getTagCompound().getTagList("AttributeModifiers",10);
		if(output1.getTagCompound().hasKey(NBTTagsKeys.DURABILITY)){
			int baseValue = output1.getTagCompound().getInteger(NBTTagsKeys.DURABILITY);
			int modifiedValue = PlayerUtils.modifyIntValueBySkill(additive, baseValue, skillValue);
			output1.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, modifiedValue);
		}
		if(output1.getItem() instanceof ItemBow && !output1.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).containsKey(SharedIblisAttributes.PROJECTILE_DAMAGE.getName())) {
			double modifierValue = 2.0d;
			NBTTagCompound modifierNBT = SharedMonsterAttributes.writeAttributeModifierToNBT(
					new AttributeModifier(SharedIblisAttributes.ARROW_DAMAGE_MODIFIER, "Arrow damage", modifierValue, 0));
			modifierNBT.setString("Slot", EntityEquipmentSlot.MAINHAND.getName());
			modifierNBT.setString("AttributeName", SharedIblisAttributes.PROJECTILE_DAMAGE.getName());
			attributeModifiersNBTList.appendTag(modifierNBT);
			output1.getTagCompound().setTag("AttributeModifiers", attributeModifiersNBTList);
		}
		attributeModifiersNBTList = new NBTTagList();
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			for (Entry<String, AttributeModifier> entry : output1.getAttributeModifiers(slot).entries()) {
				double modifierValue = PlayerUtils.getQualityModifierValue(skillValue, output1, slot,
						entry.getKey(), additive);
				if (modifierValue != 0d) {
					NBTTagCompound modifierNBT = SharedMonsterAttributes
							.writeAttributeModifierToNBT(new AttributeModifier(entry.getValue().getID(),
									entry.getValue().getName(), modifierValue, entry.getValue().getOperation()));
					modifierNBT.setString("Slot", slot.getName());
					modifierNBT.setString("AttributeName", entry.getKey());
					attributeModifiersNBTList.appendTag(modifierNBT);
				}
			}
		}
		output1.getTagCompound().setTag("AttributeModifiers", attributeModifiersNBTList);
		return output1;
	}

	public double getRequiredSkill() {
		return this.minimalSkill;
	}
}
