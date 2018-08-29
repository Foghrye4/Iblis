package iblis.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

public abstract class GuiElement {
	public final int width;
	public final int height;

	public GuiElement(int widthIn, int heightIn) {
		width = widthIn;
		height = heightIn;
	}

	public abstract void render(int x, int y);
	
	protected void renderItem(ItemStack stack, int x, int y) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderItem renderItem = mc.getRenderItem();
		renderItem.renderItemAndEffectIntoGUI(mc.player, stack, x, y);
		renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
	}
}
