package iblis.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
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
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = mc.getRenderItem();
		List<Ingredient> ingridients = recipe.getIngredients();
		GL11.glTranslatef(x, y, 0);
		for (int i = 0; i < ingridients.size(); i++) {
			ItemStack stack = ingridients.get(i).getMatchingStacks()[0];
			IBakedModel model = renderItem.getItemModelWithOverrides(stack, mc.world, mc.player);
			int lx = (x % 3) * 18;
			int ly = y / 3 * 18;
			GL11.glTranslatef(lx, ly, 0);
			renderItem.renderItem(stack, model);
			GL11.glTranslatef(-lx, -ly, 0);
		}
		ItemStack stack = recipe.getRecipeOutput();
		IBakedModel model = renderItem.getItemModelWithOverrides(stack, mc.world, mc.player);
		int lx = 18 * 4;
		int ly = 18;
		GL11.glTranslatef(lx, ly, 0);
		renderItem.renderItem(stack, model);
		GL11.glTranslatef(-lx, -ly, 0);
		Minecraft.getMinecraft().fontRenderer.drawString("=>", 18 * 3, 18, 0x000000);
		GL11.glTranslatef(-x, -y, 0);
	}
}