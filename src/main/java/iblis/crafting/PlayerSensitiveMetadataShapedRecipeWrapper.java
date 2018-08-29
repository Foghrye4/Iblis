package iblis.crafting;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

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
	public ItemStack getCraftingResult(ItemStack output1, double skillValue, boolean additive) {
		int meta = (int) skillValue;
		output1.setItemDamage(meta > 0 ? meta : 0);
		return output1;
	}

	public double getRequiredSkill() {
		return this.minimalSkill;
	}
}
