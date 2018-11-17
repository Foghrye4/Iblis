package iblis.crafting;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraftforge.oredict.OreDictionary;

import static iblis.crafting.CraftingHandler.*;

public class PlayerSensitiveRecipeRepairItem extends RecipeRepairItem {
	
	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack output1 = super.getCraftingResult(inv);
		for (PlayerSensitiveShapedRecipeWrapper recipeReplacement : replacements) {
			if (OreDictionary.itemMatches(output1, recipeReplacement.getRecipeOutput(), false)) {
				double skillValue = IblisMod.proxy.getPlayerSkillValue(recipeReplacement.sensitiveSkill, inv) - recipeReplacement.minimalSkill;;
				PlayerSensitiveShapedRecipeWrapper.getCraftingResult(output1, skillValue, false);
				break;
			}
		}
		return output1;
	}

}
