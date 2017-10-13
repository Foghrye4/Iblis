package iblis.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageWithTooltip extends GuiButtonImage {

	public GuiButtonImageWithTooltip(int buttonId, int x, int y, int widthIn, int heightIn, int xTextureStart,
			int yTextureStart, int yDiff, ResourceLocation texture, String tooltipTextIn) {
		super(buttonId, x, y, widthIn, heightIn, xTextureStart, yTextureStart, yDiff, texture);
		this.displayString = tooltipTextIn;
	}
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
    	super.drawButton(mc, mouseX, mouseY, partialTicks);
    	if(this.hovered)
    		mc.currentScreen.drawHoveringText(displayString, mouseX, mouseY);
    }
}
