package iblis.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.ServerNetworkHandler.ServerCommands;
import iblis.block.BlockLabTable.SubBox;
import iblis.chemistry.SubstanceStack;
import iblis.tileentity.TileEntityLabTable;
import iblis.tileentity.TileEntityLabTable.Actions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLabTable {

	public static GuiLabTable instance = new GuiLabTable();

	private SubBox selectedSubBox = null;
	private int subHit = 0;
	private TileEntityLabTable tile = null;

	private int buttonWidth = 120;
	private int buttonHeight = 18;
	private int u0 = 80;
	private int v0 = 220;
	private int du = 18;
	private GuiButtonImageWithLabel fillReactorButton = new GuiButtonImageWithLabel(0, 40, 40, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.fillReactor"));
	private GuiButtonImageWithLabel takeReactorButton = new GuiButtonImageWithLabel(1, 40, 60, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.takeReactor"));
	private GuiButtonImageWithLabel fillSeparatorButton = new GuiButtonImageWithLabel(2, 40, 40, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.fillSeparator"));
	private GuiButtonImageWithLabel useSeparatorButton = new GuiButtonImageWithLabel(3, 40, 60, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.useSeparator"));
	private GuiButtonImageWithLabel fillFilterButton = new GuiButtonImageWithLabel(4, 40, 40, buttonWidth, buttonHeight,
			u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.fillFilter"));
	private GuiButtonImageWithLabel takeSolidFilterContentButton = new GuiButtonImageWithLabel(5, 40, 60, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.takeSolidFilterContent"));
	private GuiButtonImageWithLabel clearButton = new GuiButtonImageWithLabel(6, 40, 60, buttonWidth, buttonHeight, u0,
			v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.clearFlask"));
	private GuiButtonImageWithLabel toggleBurnerButton = new GuiButtonImageWithLabel(7, 40, 40, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.toggleBurner"));
	private GuiButtonImageWithLabel addFuelButton = new GuiButtonImageWithLabel(8, 40, 60, buttonWidth, buttonHeight,
			u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.addFuel"));

	private GuiButtonImageWithLabel fillFlaskButton = new GuiButtonImageWithLabel(9, 40, 40, buttonWidth, buttonHeight,
			u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.fillFlask"));
	private GuiButtonImageWithLabel takeFlaskButton = new GuiButtonImageWithLabel(10, 40, 60, buttonWidth, buttonHeight,
			u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.takeFlask"));
	private GuiButtonImageWithLabel placeFlaskButton = new GuiButtonImageWithLabel(11, 40, 40, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.placeFlask"));
	private GuiButtonImageWithLabel placeReactorButton = new GuiButtonImageWithLabel(12, 40, 40, buttonWidth,
			buttonHeight, u0, v0, du, GuiEventHandler.IBLIS_ICONS, I18n.format("iblis.gui.placeReactor"));

	private List<String> contentStringList = new ArrayList<String>();
	
	/*
	 * REACTOR(10, 4, 10, 14, 8, 14), REACTOR_OUT(10, 0, 3, 14, 4, 6), FILTER(2,
	 * 5, 2, 5, 9, 5), FILTER_OUT(2, 0, 2, 5, 5, 5), SEPARATOR(6, 5, 9, 9, 14,
	 * 12), SEPARATOR_OUT(6, 0, 9, 9, 5, 12), BRUSH(0, 0, 9, 4, 2, 15), BURNER
	 */
	public void setSelectedSubBox(SubBox subBoxIn, int subHitIn, TileEntityLabTable tileIn) {
		selectedSubBox = subBoxIn;
		subHit = subHitIn;
		tile = tileIn;
	}

	public void render() {
		if (selectedSubBox == null || tile == null)
			return;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Collection<SubstanceStack> content = null;
		switch (selectedSubBox) {
		case BRUSH:
			clearButton.setHovered(true);
			clearButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			break;
		case BURNER:
			toggleBurnerButton.setHovered(subHit == 1);
			addFuelButton.setHovered(subHit != 1);
			toggleBurnerButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			addFuelButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			break;
		case FILTER:
			content = tile.filterIn.content();
			fillFilterButton.setHovered(subHit == 1);
			takeSolidFilterContentButton.setHovered(subHit != 1);
			fillFilterButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			takeSolidFilterContentButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			break;
		case FILTER_OUT:
			if (tile.hasFilterOut()) {
				content = tile.filterOut.content();
				fillFlaskButton.setHovered(subHit == 1);
				takeFlaskButton.setHovered(subHit != 1);
				fillFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
				takeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			} else {
				placeFlaskButton.setHovered(true);
				placeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			}
			break;
		case REACTOR:
			if(tile.hasReactor()) {
				content = tile.hotReactor.content();
				fillReactorButton.setHovered(subHit == 1);
				takeReactorButton.setHovered(subHit != 1);
				fillReactorButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
				takeReactorButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			}
			else{
				placeReactorButton.setHovered(true);
				placeReactorButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			}
			break;
		case REACTOR_OUT:
			if (tile.hasReactorOut()) {
				content = tile.coldReactor.content();
				fillFlaskButton.setHovered(subHit == 1);
				takeFlaskButton.setHovered(subHit != 1);
				fillFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
				takeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			} else {
				placeFlaskButton.setHovered(true);
				placeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			}
			break;
		case SEPARATOR:
			content = tile.separatorIn.content();
			fillSeparatorButton.setHovered(subHit == 1);
			useSeparatorButton.setHovered(subHit != 1);
			fillSeparatorButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			useSeparatorButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			break;
		case SEPARATOR_OUT:
			if (tile.hasSeparatorOut()) {
				content = tile.separatorOut.content();
				fillFlaskButton.setHovered(subHit == 1);
				takeFlaskButton.setHovered(subHit != 1);
				fillFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
				takeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			} else {
				placeFlaskButton.setHovered(true);
				placeFlaskButton.drawButton(Minecraft.getMinecraft(), 0, 0, 0);
			}
			break;
		default:
			break;
		}
		if(content == null)
			return;
		int textX = 180;
		int textY = 40;
		int textColour = 16777120;
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		if (content.isEmpty()) {
			fontrenderer.drawString(I18n.format("iblis.empty"), textX, textY, textColour);
		} else {
			for (SubstanceStack ss : content) {
				fontrenderer.drawString(I18n.format(ss.substance.unlocalizedName + ".amount", ss.amount()), textX, textY, textColour);
			}
		}
	}

	public boolean isActive() {
		return this.selectedSubBox!=null && this.tile!=null;
	}

	public void onRightClick() {
		Actions action = Actions.ADD_FUEL;
		switch(selectedSubBox){
		case BRUSH:
			action = Actions.CLEAR_FLASK;
			break;
		case BURNER:
			if(subHit == 1)
				action = Actions.TOGGLE_BURNER;
			else 
				action = Actions.ADD_FUEL;
			break;
		case FILTER:
			if(subHit == 1)
				action = Actions.FILL_FILTER;
			else
				action = Actions.GRAB_RESIDUUM;
			break;
		case FILTER_OUT:
			if(tile.hasFilterOut() && subHit == 1)
				action = Actions.FILL_FILTER_FLASK;
			else if(tile.hasFilterOut())
				action = Actions.TAKE_FILTER_FLASK;
			else
				action = Actions.PLACE_FILTER_FLASK;
			break;
		case REACTOR:
			if(tile.hasReactor() && subHit == 1)
				action = Actions.FILL_REACTOR;
			else if(tile.hasReactor())
				action = Actions.TAKE_REACTOR;
			else
				action = Actions.PLACE_REACTOR;
			break;
		case REACTOR_OUT:
			if(tile.hasReactorOut() && subHit == 1)
				action = Actions.FILL_REACTOR_OUT;
			else if(tile.hasReactorOut())
				action = Actions.TAKE_REACTOR_OUT;
			else
				action = Actions.PLACE_REACTOR_OUT;
			break;
		case SEPARATOR:
			if(subHit == 1)
				action = Actions.FILL_SEPARATOR;
			else
				action = Actions.USE_SEPARATOR;
			break;
		case SEPARATOR_OUT:
			if(tile.hasSeparatorOut() && subHit == 1)
				action = Actions.FILL_SEPARATOR_OUT;
			else if(tile.hasSeparatorOut())
				action = Actions.TAKE_SEPARATOR_OUT;
			else
				action = Actions.PLACE_SEPARATOR_OUT;
			break;
		default:
			break;}
		((ClientNetworkHandler) IblisMod.network).sendCommandLabTableGuiAction(tile.getPos(), action);
	}
}
