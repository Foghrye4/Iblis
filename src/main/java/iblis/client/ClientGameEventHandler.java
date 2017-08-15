package iblis.client;

import org.lwjgl.input.Keyboard;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@SideOnly(Side.CLIENT)
public class ClientGameEventHandler {
	KeyBinding[] keyBindings = new KeyBinding[] {
			new KeyBinding("key.iblis.reload", Keyboard.KEY_R, "key.categories.gameplay") };
	boolean sprintingState = false;
	int sprintCounter = 0;
	int lastSprintCounter = 0;
	
	public ClientGameEventHandler() {
		ClientRegistry.registerKeyBinding(keyBindings[0]);
	}

	@SubscribeEvent
	public void onEvent(KeyInputEvent event) {
		if (Keyboard.getEventKey() == keyBindings[0].getKeyCode()) {
			if (Minecraft.getMinecraft().currentScreen == null)
				((ClientNetworkHandler) IblisMod.network).sendCommandReloadWeapon();
		}
	}

	@SubscribeEvent
	public void onEvent(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			if (!event.player.isSprinting()) {
				sprintCounter = 0;
			} else if (sprintCounter < PlayerUtils.MAX_SPRINT_SPEED) {
				if (sprintCounter == 0)
					sprintCounter = 1;
				else if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
					sprintCounter++;
			}

			if (sprintCounter != lastSprintCounter) {
				lastSprintCounter = sprintCounter;
				PlayerUtils.applySprintingSpeedModifier(event.player, sprintCounter);
				((ClientNetworkHandler) IblisMod.network).sendCommandApplySprintingSpeedModifier(sprintCounter);
			}
		}
	}
}
