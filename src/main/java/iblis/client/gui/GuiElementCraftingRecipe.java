package iblis.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

public class GuiElementCraftingRecipe extends GuiElement {
	private final IRecipe recipe;

	public GuiElementCraftingRecipe(IRecipe recipeIn) {
		super(90, 54);
		recipe = recipeIn;
	}

	public void render(int x, int y) {
		List<Ingredient> ingridients = recipe.getIngredients();
		for (int i = 0; i < ingridients.size(); i++) {
			ItemStack stack = ingridients.get(i).getMatchingStacks()[0];
			int lx = (i % 3) * 18;
			int ly = i / 3 * 18;
			this.renderItem(stack, x+lx, y+ly);
		}
		ItemStack stack = recipe.getRecipeOutput();
		int lx = 18 * 4;
		int ly = 18;
		this.renderItem(stack, x+lx, y+ly);
		Minecraft.getMinecraft().fontRenderer.drawString("=>", x+18 * 3, y+18, 0x000000);
	}
}