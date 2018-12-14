package iblis.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSkills extends GuiScreen {

	private EntityPlayerSP player;
	private final int leftMargin = 2;
	private final int topMargin = 8;
	private final int labelHeight = 12;
	private final int labelRowHeight = 14;
	private final int labelWidth = 100;
	
	private GuiLabel wisdomLabel;
	
	private final Map<IAttribute, GuiLabel> level2Skills = new HashMap<IAttribute, GuiLabel>();
	
	private final Map<IAttribute, GuiLabel> martialSkills = new HashMap<IAttribute, GuiLabel>();
	private final Map<IAttribute, GuiLabel> craftSkills = new HashMap<IAttribute, GuiLabel>();
	private final Map<IAttribute, GuiLabel> acrobaticsSkills = new HashMap<IAttribute, GuiLabel>();
	private final Map<IAttribute, GuiLabel> magicSkills = new HashMap<IAttribute, GuiLabel>();
	
	private int wisdomLineX1;
	private int wisdomLineY1;
	private int wisdomLineX2;
	private int wisdomLineY2;
	
	private int martialLineX1;
	private int martialLineY1;
	private int martialLineX2;
	private int martialLineY2;
	
	private int craftLineX1;
	private int craftLineY1;
	private int craftLineX2;
	private int craftLineY2;
	
	private int acrobaticsLineX1;
	private int acrobaticsLineY1;
	private int acrobaticsLineX2;
	private int acrobaticsLineY2;

	private int magicLineX1;
	private int magicLineY1;
	private int magicLineX2;
	private int magicLineY2;


	public GuiSkills(EntityPlayerSP playerIn) {
		super();
		player = playerIn;
		mc = Minecraft.getMinecraft();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.labelList.clear();
		this.wisdomLabel = null;
		this.level2Skills.clear();
		this.martialSkills.clear();
		this.craftSkills.clear();
		this.acrobaticsSkills.clear();
		this.magicSkills.clear();
		
		int id = 0;
		int level1Y = this.labelHeight*3;
		int martialX = this.width - this.labelWidth - this.leftMargin;
		int martialY = this.topMargin + labelHeight * 2;
		
		int craftX = martialX;
		int craftY = this.height/2;
		
		int acrobaticsX = leftMargin + this.labelWidth + 8;
		int acrobaticsY = this.height/2;
		
		for (PlayerSkills skill : PlayerSkills.values()) {
			if(!skill.enabled)
				continue;
			IAttribute skillAttribute = skill.getAttribute();
			if(skillAttribute.getParent() == SharedIblisAttributes.MARTIAL_ARTS)
				martialSkills.put(skillAttribute, this.addAttributeLabel(skillAttribute, id++, martialX, martialY + labelRowHeight * martialSkills.size()));
			else if(skillAttribute.getParent() == SharedIblisAttributes.CRAFTMANSHIP)
				craftSkills.put(skillAttribute, this.addAttributeLabel(skillAttribute, id++, craftX, craftY + labelRowHeight * craftSkills.size()));
			else if(skillAttribute.getParent() == SharedIblisAttributes.ACROBATICS)
				acrobaticsSkills.put(skillAttribute, this.addAttributeLabel(skillAttribute, id++, acrobaticsX, acrobaticsY + labelRowHeight * acrobaticsSkills.size()));
			else
				magicSkills.put(skillAttribute, this.addAttributeLabel(skillAttribute, id++, leftMargin, acrobaticsY + labelRowHeight * magicSkills.size()));

			IAttribute parent = skillAttribute.getParent();
			if(!level2Skills.containsKey(parent))
				level2Skills.put(parent, this.addAttributeLabel(parent, id++, leftMargin, level1Y + labelRowHeight * level2Skills.size()));
			IAttribute wisdom = parent.getParent();
			if(wisdomLabel == null)
				wisdomLabel = this.addAttributeLabel(wisdom, id++, leftMargin, topMargin);
		}
		this.wisdomLineX1 = leftMargin + labelWidth+1;
		this.wisdomLineX2 = leftMargin + labelWidth/2;
		this.wisdomLineY1 = topMargin + labelHeight/2;
		this.wisdomLineY2 = level1Y - 1;
		
		this.martialLineX1 = leftMargin + labelWidth + 1;
		this.martialLineX2 = martialX + labelWidth/2;
		this.martialLineY1 = level1Y + labelRowHeight/2;
		this.martialLineY2 = martialY - 1;
		
		this.craftLineX1 = leftMargin + labelWidth + 1;
		this.craftLineX2 = craftX + labelWidth/2;
		this.craftLineY1 = level1Y + labelRowHeight + labelRowHeight/2;
		this.craftLineY2 = craftY - 1;

		this.acrobaticsLineX1 = leftMargin + labelWidth + 1;
		this.acrobaticsLineX2 = acrobaticsX + labelWidth/2;
		this.acrobaticsLineY1 = level1Y + labelRowHeight*2 + labelRowHeight/2;
		this.acrobaticsLineY2 = acrobaticsY - 1;
		
		this.magicLineX1 = leftMargin + labelWidth + 1;
		this.magicLineX2 = leftMargin + labelWidth/2;
		this.magicLineY1 = level1Y + labelRowHeight*3 + labelRowHeight/2;
		this.magicLineY2 = acrobaticsY - 1;
	}
	
	private GuiLabel addAttributeLabel(IAttribute attribute, int id, int x, int y) {
		GuiSingleLineLabelFormatted label = new GuiSingleLineLabelFormatted(fontRenderer, id,
				x, y,
				labelWidth, labelHeight, 0xFFAA33);
		label.addLine(I18n.format(attribute.getName(),
				Math.round(player.getAttributeMap().getAttributeInstance(attribute).getAttributeValue() * 10)
						/ 10d));
		label.setColoursAndBorder(1, 0xFF000000, 0xeeff7e00, 0xeeb36900);
		label.setCentered();
		this.labelList.add(label);
		return label;
	}
	
	private void drawLinkFromTopToBottom(int x1, int x2, int y1, int y2, int linkOffsetX){
		this.drawHorizontalLine(x1, x1 + linkOffsetX, y1, 0xeeb36900);
		this.drawVerticalLine(x1 + linkOffsetX, y1, y1 / 2 + y2 / 2, 0xeeb36900);
		this.drawHorizontalLine(x2, x1 + linkOffsetX, y1 / 2 + y2 / 2, 0xeeb36900);
		this.drawVerticalLine(x2, y1 / 2 + y2 / 2, y2, 0xeeb36900);
	}
	
	private void drawLinkFromLeftToRight(int x1, int x2, int y1, int y2, int linkOffsetX) {
		int linkOffsetY = -10;
		this.drawHorizontalLine(x1, x1 + linkOffsetX, y1, 0xeeb36900);
		this.drawVerticalLine(x1 + linkOffsetX, y1, y2 + linkOffsetY, 0xeeb36900);
		this.drawHorizontalLine(x1 + linkOffsetX, x2, y2 + linkOffsetY, 0xeeb36900);
		this.drawVerticalLine(x2, y2 + linkOffsetY, y2, 0xeeb36900);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.drawLinkFromTopToBottom(wisdomLineX1, wisdomLineX2, wisdomLineY1, wisdomLineY2, 10);
		if (!martialSkills.isEmpty())
			this.drawLinkFromLeftToRight(martialLineX1, martialLineX2, martialLineY1, martialLineY2,20);
		if (!craftSkills.isEmpty())
			this.drawLinkFromLeftToRight(craftLineX1, craftLineX2, craftLineY1, craftLineY2,10+labelWidth);
		if (!acrobaticsSkills.isEmpty())
			this.drawLinkFromLeftToRight(acrobaticsLineX1, acrobaticsLineX2, acrobaticsLineY1, acrobaticsLineY2,20);
		if (!magicSkills.isEmpty())
			this.drawLinkFromTopToBottom(magicLineX1, magicLineX2, magicLineY1, magicLineY2,10);
	}
}
