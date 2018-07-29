package iblis.client.gui;

import net.minecraft.client.Minecraft;

public class GuiElementTextLine extends GuiElement {
	private final String text;

	public GuiElementTextLine(String textIn) {
		super(textIn.length() * 6, 12);
		text = textIn;
	}

	@Override
	public void render(int x, int y) {
		Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, 0x000000);
	}
}
