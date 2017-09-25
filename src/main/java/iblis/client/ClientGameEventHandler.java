package iblis.client;

import org.lwjgl.input.Keyboard;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@SideOnly(Side.CLIENT)
public class ClientGameEventHandler {
	
	public static ClientGameEventHandler instance;
	private final KeyBinding[] keyBindings = new KeyBinding[] {
			new KeyBinding("key.iblis.reload", Keyboard.KEY_R, "key.categories.gameplay") };
	public int sprintCounter = 0;
	private int lastSprintCounter = 0;
	private BlockPos sprintingStartPos = null;
	public int sprintButtonCounter = 0;
	private int lastSprintButtonCounter = 0;
	
	public ClientGameEventHandler() {
		instance = this;
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

			if (sprintCounter>>>3 != lastSprintCounter>>>3) { // Make it more crude so packets will not spawned too often
				if(lastSprintCounter == 0) // Start of sprint
					sprintingStartPos = event.player.getPosition();
				else if (lastSprintCounter == PlayerUtils.MAX_SPRINT_SPEED) { // Sprint counter cannot go back unless you stop movement.
					double dsq = event.player.getPosition().distanceSq(sprintingStartPos);
					dsq = Math.sqrt(dsq);
					((ClientNetworkHandler) IblisMod.network).sendPlayerRunnedDistance((float) dsq);
				}
				lastSprintCounter = sprintCounter;
				((ClientNetworkHandler) IblisMod.network).sendCommandApplySprintingSpeedModifier(sprintCounter);
			}
		}
		else {
			if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() && PlayerUtils.canJump(event.player)) {
				if (sprintButtonCounter < PlayerUtils.MAX_SPRINT_SPEED)
					sprintButtonCounter++;
			} else
				sprintButtonCounter = 0;
			if (sprintButtonCounter >>> 3 != lastSprintButtonCounter >>> 3) {
				PlayerUtils.saveSprintButtonCounterState(Minecraft.getMinecraft().player, sprintButtonCounter);
				((ClientNetworkHandler) IblisMod.network).sendSprintButtonCounterState(sprintButtonCounter);
				lastSprintButtonCounter = sprintButtonCounter;
			}

		}
	}
}
