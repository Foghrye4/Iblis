package iblis.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLabelFormatted extends GuiLabel {

	/** To detect if label is already added. **/
	private Set<String> unformattedLabels = new HashSet<String>();
	private int borderYOffset = -2;

	/** This value handled externally and have no other uses. **/
	GuiLabelFormatted parent;

	public GuiLabelFormatted(FontRenderer fontRendererObj, int id, int x, int y, int widthIn, int heightIn,
			int textColour) {
		super(fontRendererObj, id, x, y, widthIn, heightIn, textColour);
	}

	public void addLine(String text, Object... parameters) {
		unformattedLabels.add(text);
		this.labels.add(I18n.format(text, parameters));
		if (this.labels.size() >= 2)
			this.height = this.height * this.labels.size() / (this.labels.size() - 1);
	}

	public void setParent(GuiLabelFormatted parentIn) {
		parent = parentIn;
	}

	public void setColoursAndBorder(int borderIn, int backColorIn, int ulColorIn, int brColorIn) {
		this.border = borderIn;
		this.backColor = backColorIn;
		this.ulColor = ulColorIn;
		this.brColor = brColorIn;
		this.labelBgEnabled = true;
	}

	public GuiLabelFormatted getParent() {
		return parent;
	}

	public boolean isContainLine(String text) {
		for (String label : unformattedLabels) {
			if (label.contains(text))
				return true;
		}
		return false;
	}

	@Override
	protected void drawLabelBackground(Minecraft mcIn, int mouseX, int mouseY) {
		if (this.labelBgEnabled) {
			int i = this.width + this.border * 2;
			int j = this.height + this.border * 2;
			int k = this.x - this.border;
			int l = this.y - this.border + this.borderYOffset;
			drawRect(k, l, k + i, l + j, this.backColor);
			this.drawHorizontalLine(k, k + i, l, this.ulColor);
			this.drawHorizontalLine(k, k + i, l + j, this.brColor);
			this.drawVerticalLine(k, l, l + j, this.ulColor);
			this.drawVerticalLine(k + i, l, l + j, this.brColor);
            for (int i1 = 1; i1 < this.labels.size(); ++i1)
            {
    			this.drawHorizontalLine(k, k + i, l + i1*10 - this.borderYOffset, this.brColor);
            }
		}
		if (this.parent != null) {
			int bordersWidth = this.width + this.border * 2;
			int bordersHeight = this.height + this.border * 2;
			int bordersStartX = this.x - this.border;
			int bordersStartY = this.y - this.border + this.borderYOffset;

			int parentBordersWidth = this.parent.width + this.parent.border * 2;
			int parentBordersHeight = this.parent.height + this.parent.border * 2;
			int parentBordersStartX = this.parent.x - this.parent.border;
			int parentBordersStartY = this.parent.y - this.parent.border + this.borderYOffset;

			int x1 = bordersStartX;
			int y1 = bordersStartY + bordersHeight / 2;
			int x2 = bordersStartX - bordersHeight / 2;
			int y2 = (parentBordersStartY + parentBordersHeight + bordersStartY) / 2;
			int x3 = parentBordersStartX + parentBordersWidth / 2;
			int y3 = parentBordersStartY + parentBordersHeight;

			this.drawHorizontalLine(x2, x1, y1, this.ulColor);
			this.drawVerticalLine(x2, y2, y1, this.ulColor);
			this.drawHorizontalLine(x2, x3, y2, this.ulColor);
			this.drawVerticalLine(x3, y3, y2, this.ulColor);
		}
	}
}
