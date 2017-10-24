package iblis.crafting;

import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ShapedRecipeRaisingSkillWrapper extends ShapedRecipes implements IRecipeRaiseSkill {

	PlayerSkills sensitiveSkill = PlayerSkills.WEAPONSMITH;
	private double skillXP = 1.0d;
	private IRecipe wrappedRecipe;

	public ShapedRecipeRaisingSkillWrapper(IRecipe recipeIn) {
		super(recipeIn.getGroup(), 
			(recipeIn instanceof  ShapedRecipes)?((ShapedRecipes)recipeIn).recipeWidth:3,
			(recipeIn instanceof  ShapedRecipes)?((ShapedRecipes)recipeIn).recipeHeight:3, 
			recipeIn.getIngredients(), recipeIn.getRecipeOutput());
		wrappedRecipe = recipeIn;
	}

	public ShapedRecipeRaisingSkillWrapper setSesitiveTo(PlayerSkills skillIn, double skillXPIn) {
		sensitiveSkill = skillIn;
		skillXP = skillXPIn;
		return this;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return this.wrappedRecipe.matches(inv, worldIn);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.wrappedRecipe.getRecipeOutput();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		for (IContainerListener listener : inv.eventHandler.listeners) {
			if (listener instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) listener;
				this.raiseSkill(player, 1);
			}
		}
		return this.wrappedRecipe.getRemainingItems(inv);
	}
	
	@Override
	public void raiseSkill(EntityPlayer player, int times){
		sensitiveSkill.raiseSkill(player, skillXP * times);
	}

	@Override
	public boolean canFit(int width, int height) {
		return this.wrappedRecipe.canFit(width, height);
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.wrappedRecipe.getIngredients();
	}
}
