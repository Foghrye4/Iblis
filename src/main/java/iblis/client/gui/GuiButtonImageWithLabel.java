package iblis.client.gui;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageWithLabel extends GuiButtonImage {

	private int xTextureStart;
	private int yTextureStart;
	private int yDiff;
	private ResourceLocation texture;

	public GuiButtonImageWithLabel(int buttonId, int x, int y, int widthIn, int heightIn, int xTextureStartIn,
			int yTextureStartIn, int yDiffIn, ResourceLocation textureIn, String tooltipTextIn) {
		super(buttonId, x, y, widthIn, heightIn, xTextureStartIn, yTextureStartIn, yDiffIn, textureIn);
		this.displayString = tooltipTextIn;
		this.xTextureStart = xTextureStartIn;
		this.yTextureStart = yTextureStartIn;
		this.yDiff = yDiffIn;
		this.texture = textureIn;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableDepth();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int i = this.xTextureStart;
		int j = this.yTextureStart;

		if (this.hovered) {
			j += this.yDiff;
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
		FontRenderer fontrenderer = mc.fontRenderer;
		j = 14737632;
		if (packedFGColour != 0) {
			j = packedFGColour;
		} else if (!this.enabled) {
			j = 10526880;
		} else if (this.hovered) {
			j = 16777120;
		}
		this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2,
				this.y + (this.height - 8) / 2, j);
		GlStateManager.enableDepth();
	}

	public void setHovered(boolean b) {
		this.hovered = b;
	}
}
