package iblis.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiSingleLineLabelFormatted extends GuiLabel {

	public GuiSingleLineLabelFormatted(FontRenderer fontRendererObj, int id, int x, int y, int widthIn, int heightIn,
			int textColourIn) {
		super(fontRendererObj, id, x, y, widthIn, heightIn, textColourIn);
	}

	public void setColoursAndBorder(int borderIn, int backColorIn, int ulColorIn, int brColorIn) {
		this.border = borderIn;
		this.backColor = backColorIn;
		this.ulColor = ulColorIn;
		this.brColor = brColorIn;
		this.labelBgEnabled = true;
	}

	@Override
	public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			if(this.labelBgEnabled)
				this.drawLabelBackground(mc, mouseX, mouseY);
			super.drawLabel(mc, mouseX, mouseY);
		}
	}

	@Override
	protected void drawLabelBackground(Minecraft mcIn, int mouseX, int mouseY) {
		if (this.labelBgEnabled) {
			int i = this.width + this.border * 2;
			int j = this.height + this.border * 2;
			int k = this.x - this.border;
			int l = this.y - this.border * 2;
			drawRect(k, l, k + i, l + j, this.backColor);
			this.drawHorizontalLine(k, k + i, l, this.ulColor);
			this.drawHorizontalLine(k, k + i, l + j, this.brColor);
			this.drawVerticalLine(k, l, l + j, this.ulColor);
			this.drawVerticalLine(k + i, l, l + j, this.brColor);
		}
	}
}
