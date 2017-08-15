package iblis.gui;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSkills extends GuiScreen {

	private EntityPlayerSP player;
	int guiLeft = 0;
	int guiTop = 0;
	int xSize = 176;
	int ySize = 166;
	int leftMargin = 40;
	int topMargin = 9;
	int buttonSize = 20;
	int labelHeight = 40;
	int columnWidth = 140;

	public GuiSkills(EntityPlayerSP playerIn) {
		super();
		player = playerIn;
		mc = Minecraft.getMinecraft();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.labelList.clear();
		int column = 0;
		for (PlayerSkills skill : PlayerSkills.values()) {
			int row = 0;
			IAttribute attribute = skill.getAttribute();
			GuiLabelFormatted parentLabel = this.getAttributeLabel(attribute.getParent());
			GuiLabelFormatted childLabel = null;
			if (parentLabel != null) {
				this.getChildOf(parentLabel).addLine(attribute.getName(),
						Math.round(player.getAttributeMap().getAttributeInstance(attribute).getAttributeValue() * 10)
									/ 10d);
			} else {
				childLabel = this.addAttributeLabel(childLabel, attribute, row, column);
			}
			attribute = attribute.getParent();
			childLabel = this.addAttributeLabel(childLabel, attribute, ++row, column);
			attribute = attribute.getParent();
			this.addAttributeLabel(childLabel, attribute, ++row, column);
			if (parentLabel == null)
				column++;
		}
	}
	
	private GuiLabelFormatted addAttributeLabel(GuiLabelFormatted childLabel, IAttribute attribute, int row, int column){
		GuiLabelFormatted label = this.getAttributeLabel(attribute);
		if (label == null) {
			label = new GuiLabelFormatted(fontRendererObj, row,
				leftMargin + columnWidth * column + 1, topMargin + 5 + (labelHeight + 1) * (2 - row),
				100, 11, 0xFFAA33);
		label.addLine(attribute.getName(),
				Math.round(player.getAttributeMap().getAttributeInstance(attribute).getAttributeValue() * 10)
						/ 10d);
		label.setColoursAndBorder(1, 0x44000000, 0xeeff7e00, 0xeeb36900);
		label.setCentered();
		this.labelList.add(label);
		}
		if(childLabel!=null){
			childLabel.setParent(label);
		}
		return label;
	}

	private GuiLabelFormatted getAttributeLabel(IAttribute iattribute) {
		if (iattribute == null)
			return null;
		for (GuiLabel label : this.labelList) {
			if (label instanceof GuiLabelFormatted) {
				GuiLabelFormatted formattedLabel = (GuiLabelFormatted) label;
				if (formattedLabel.isContainLine(iattribute.getName()))
					return formattedLabel;
			}
		}
		return null;
	}

	private GuiLabelFormatted getChildOf(GuiLabelFormatted parent) {
		if (parent == null)
			return null;
		for (GuiLabel label : this.labelList) {
			if (label instanceof GuiLabelFormatted) {
				GuiLabelFormatted formattedLabel = (GuiLabelFormatted) label;
				if (formattedLabel.getParent() == parent)
					return formattedLabel;
			}
		}
		return null;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
