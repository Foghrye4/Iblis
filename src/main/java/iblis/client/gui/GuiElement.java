package iblis.client.gui;

public abstract class GuiElement {
	public final int width;
	public final int height;

	public GuiElement(int widthIn, int heightIn) {
		width = widthIn;
		height = heightIn;
	}

	public abstract void render(int x, int y);
}
