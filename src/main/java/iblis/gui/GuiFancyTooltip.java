package iblis.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class GuiFancyTooltip {

	private int frame = 0;
	private int xPos = 0;
	private int yPos = 0;
	private final GuiScreen screen;
	private final List<String> text = new ArrayList<String>();
	private final FontRenderer fontRenderer;
	private final int width;
	private final int height;
	private final int hotspotX1;
	private final int hotspotY1;
	private final int hotspotX2;
	private final int hotspotY2;

	public GuiFancyTooltip(List<String> textIn, GuiScreen screenIn, int hotspotXIn, int hotspotYIn, int hotspotWidth,
			int hotspotHeight) {
		screen = screenIn;
		fontRenderer = screen.mc.fontRenderer;
		text.addAll(textIn);
		int maxLineWidth = 0;
		for (String line : text) {
			int lineWidth = 0;
			for (char c : line.toCharArray()) {
				lineWidth += fontRenderer.getCharWidth(c);
			}
			if (lineWidth > maxLineWidth)
				maxLineWidth = lineWidth;
		}
		width = maxLineWidth;
		height = fontRenderer.FONT_HEIGHT * text.size();
		hotspotX1 = hotspotXIn;
		hotspotY1 = hotspotYIn;
		hotspotX2 = hotspotXIn + hotspotWidth;
		hotspotY2 = hotspotYIn + hotspotHeight;
	}

	private int width() {
		if (frame < width)
			return frame;
		else
			return width;
	}

	private int height() {
		if (frame < height)
			return frame;
		else
			return height;
	}

	public void tick(int cursorX, int cursorY) {
		boolean show = hotspotX1 < cursorX && hotspotX2 > cursorX && hotspotY1 < cursorY && hotspotY2 > cursorY;
		if (show) {
			if (frame < width || frame < height)
				frame++;
			xPos = cursorX;
			yPos = cursorY;
		} else if (frame > 0) {
			frame--;
		}
	}

	public void draw(int cursorX, int cursorY) {
		if (frame == 0)
			return;
		GuiScreen.drawRect(xPos, yPos, xPos + this.width(), xPos + this.height(), frame << 24);
		for (int lineIndex = 0; lineIndex < Math.min(text.size(), height() / fontRenderer.FONT_HEIGHT); lineIndex++) {
			this.fontRenderer.drawString(fontRenderer.trimStringToWidth(text.get(lineIndex), this.width()), xPos, yPos+this.fontRenderer.FONT_HEIGHT*lineIndex,
					16768125);
		}
	}
}
