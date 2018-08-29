package iblis.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiElementItemStack extends GuiElement {
	private final ItemStack stack;
	private final String description;
	
	public GuiElementItemStack(ItemStack stackIn) {
		super(90, 18);
		stack = stackIn;
		description = I18n.format(stack.getUnlocalizedName()+".name");
	}

	public void render(int x, int y) {
		this.renderItem(stack, x, y);
		Minecraft.getMinecraft().fontRenderer.drawString(description, x+18, y+4, 0x000000);
	}
}