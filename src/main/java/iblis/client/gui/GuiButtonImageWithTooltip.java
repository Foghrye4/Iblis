package iblis.client.gui;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageWithTooltip extends GuiButtonImage {

	public GuiButtonImageWithTooltip(int buttonId, int x, int y, int widthIn, int heightIn, int xTextureStart,
			int yTextureStart, int yDiff, ResourceLocation texture, String displayStringIn) {
		super(buttonId, x, y, widthIn, heightIn, xTextureStart, yTextureStart, yDiff, texture);
		this.displayString = displayStringIn;
	}
}
