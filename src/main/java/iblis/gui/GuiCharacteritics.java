package iblis.gui;

import java.io.IOException;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.player.PlayerCharacteristics;
import iblis.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCharacteritics extends GuiScreen {

	private EntityPlayerSP player;
	int guiLeft = 0;
	int guiTop = 0;
	int xSize = 176;
	int ySize = 166;
	int leftMargin = 40;
	int topMargin = 9;
	int buttonSize = 20;

	public GuiCharacteritics(EntityPlayerSP playerIn) {
		super();
		player = playerIn;
		mc = Minecraft.getMinecraft();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		ClientNetworkHandler cnh = (ClientNetworkHandler) IblisMod.network;
		PlayerCharacteristics characteristic = PlayerCharacteristics.values()[button.id];
		cnh.sendCharacteristicUpdate(characteristic);
		characteristic.raiseCharacteristic(player);
		initGui();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.labelList.clear();
		int row = 0;
		for (PlayerCharacteristics characteristic : PlayerCharacteristics.values()) {
			if (!characteristic.enabled)
				continue;
			if (PlayerUtils.isCharacteristicCouldBeRaised(characteristic, player)) {
				GuiButtonImage raiseCB = new GuiButtonImage(characteristic.ordinal(), leftMargin,
						topMargin + (buttonSize + 1) * row, 20, 18, 40, 220, 18, GuiEventHandler.IBLIS_ICONS);
				this.buttonList.add(raiseCB);
			}
			GuiLabelFormatted label = new GuiLabelFormatted(fontRenderer, characteristic.ordinal(),
					leftMargin + buttonSize + 1, topMargin + 2 + (buttonSize + 1) * row, buttonSize, buttonSize,
					0xFFAA33);
			label.addLine(characteristic.getNiceName(), Integer.valueOf(characteristic.getCurrentLevel(player)),
					Float.valueOf((float) characteristic.getCurrentValue(player)));
			this.labelList.add(label);
			row++;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
