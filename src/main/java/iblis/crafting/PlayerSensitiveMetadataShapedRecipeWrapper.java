package iblis.crafting;

import java.util.Map.Entry;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.player.PlayerSkills;
import iblis.util.PlayerUtils;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlayerSensitiveMetadataShapedRecipeWrapper extends ShapedRecipeRaisingSkillWrapper {

	private double minimalSkill = 0;

	public PlayerSensitiveMetadataShapedRecipeWrapper(IRecipe recipeIn) {
		super(recipeIn);
	}

	public PlayerSensitiveMetadataShapedRecipeWrapper setSesitiveTo(PlayerSkills skillIn, double requiredskill, double skillXPIn) {
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
		return this.getCraftingResult(output, skillValue, false);
	}
	
	/** Modify item stack and return it instance. Does not create a copy. */
	public ItemStack getCraftingResult(ItemStack output1, double skillValue, boolean additive){
		output1.setItemDamage((int)skillValue);
		return output1;
	}

	public double getRequiredSkill() {
		return this.minimalSkill;
	}
}
