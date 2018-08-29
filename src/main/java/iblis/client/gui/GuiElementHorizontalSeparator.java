package iblis.client.gui;

import net.minecraft.client.gui.Gui;

public class GuiElementHorizontalSeparator extends GuiElement {
	private final int length;

	public GuiElementHorizontalSeparator(int lengthIn) {
		super(lengthIn, 3);
		length = lengthIn;
	}

	@Override
	public void render(int x, int y) {
		Gui.drawRect(x, y+1, x+length, y+2, 255<<24);
	}
}
