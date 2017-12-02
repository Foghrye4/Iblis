package iblis.gui;

import java.util.HashMap;
import java.util.Map;

import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
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
	private final int leftMargin = 40;
	private final int topMargin = 9;
	private final int labelHeight = 60;
	private final int columnWidth = 140;

	public GuiSkills(EntityPlayerSP playerIn) {
		super();
		player = playerIn;
		mc = Minecraft.getMinecraft();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.labelList.clear();
		
		int id = 0;
		int skillLabelRow = 0;
		GuiLabelFormatted wisdomAttributeLabel = this.addAttributeLabel(null, SharedIblisAttributes.WISDOM, id++, 0, 0);
		GuiLabelFormatted parentLabel = null;
		Map<IAttribute, GuiLabelFormatted> skillLabelsMap = new HashMap<IAttribute, GuiLabelFormatted>();
		for (PlayerSkills skill : PlayerSkills.values()) {
			if(!skill.enabled)
				continue;
			IAttribute skillAttribute = skill.getAttribute();
			IAttribute parentAttribute = skillAttribute.getParent();
			if(parentLabel == null){
				parentLabel = this.addAttributeLabel(wisdomAttributeLabel, parentAttribute, id++, 1, 0);
				wisdomAttributeLabel.addChild(parentLabel);
			}
			else if(!parentLabel.isContainLine(parentAttribute.getName())){
				parentLabel.addLine(parentAttribute.getName(),
						Math.round(player.getAttributeMap().getAttributeInstance(parentAttribute).getAttributeValue() * 10)/ 10d);
			}
			GuiLabelFormatted skillLabel = skillLabelsMap.get(parentAttribute);
			if(skillLabel==null){
				skillLabel = this.addAttributeLabel(parentLabel, skillAttribute, id++, skillLabelRow++, 1);
				parentLabel.addChild(skillLabel);
				skillLabelsMap.put(parentAttribute, skillLabel);
			}
			else{
				skillLabel.addLine(skillAttribute.getName(),
						Math.round(player.getAttributeMap().getAttributeInstance(skillAttribute).getAttributeValue() * 10)/ 10d);
			}
		}
	}
	
	private GuiLabelFormatted addAttributeLabel(GuiLabelFormatted parentLabel, IAttribute attribute, int id, int row, int column){
		GuiLabelFormatted label = this.getAttributeLabel(attribute);
		if (label == null) {
			label = new GuiLabelFormatted(fontRenderer, id,
				leftMargin + columnWidth * column + 1, topMargin + 5 + (labelHeight + 1) * row,
				100, 11, 0xFFAA33);
		label.addLine(attribute.getName(),
				Math.round(player.getAttributeMap().getAttributeInstance(attribute).getAttributeValue() * 10)
						/ 10d);
		label.setColoursAndBorder(1, 0x44000000, 0xeeff7e00, 0xeeb36900);
		label.setCentered();
		this.labelList.add(label);
		}
		if(parentLabel!=null){
			parentLabel.addChild(label);
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

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
