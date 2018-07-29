package iblis.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiElementItemStack extends GuiElement {
	private final ItemStack stack;
	private final String description;
	
	public GuiElementItemStack(ItemStack stackIn) {
		super(90, 18);
		stack = stackIn;
		description = I18n.format(stack.getUnlocalizedName());
	}

	public void render(int x, int y) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = mc.getRenderItem();
		GL11.glTranslatef(x, y, 0);
		IBakedModel model = renderItem.getItemModelWithOverrides(stack, mc.world, mc.player);
		renderItem.renderItem(stack, model);
		Minecraft.getMinecraft().fontRenderer.drawString(description, 18, 0, 0x000000);
		GL11.glTranslatef(-x, -y, 0);
	}
}