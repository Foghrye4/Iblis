package iblis.client;

import org.lwjgl.input.Keyboard;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.ServerNetworkHandler.ServerCommands;
import iblis.init.IblisItems;
import iblis.item.ICustomLeftClickItem;
import iblis.player.PlayerSkills;
import iblis.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientGameEventHandler {

	public static ClientGameEventHandler instance;
	private final KeyBinding[] keyBindings = new KeyBinding[] {
			new KeyBinding("key.iblis.reload", Keyboard.KEY_R, "key.categories.gameplay") };
	private boolean shieldPunch = false;
	private boolean kick = false;
	public int sprintCounter = 0;
	private int lastSprintCounter = 0;
	private BlockPos sprintingStartPos = null;
	public int sprintButtonCounter = 0;
	private int lastSprintButtonCounter = 0;
	private final int NETWORK_SENSIBILITY_BIT = 2;

	public ClientGameEventHandler() {
		instance = this;
		ClientRegistry.registerKeyBinding(keyBindings[0]);
	}

	@SubscribeEvent
	public void onEvent(KeyInputEvent event) {
		handleInput();
	}

	@SubscribeEvent
	public void onEvent(MouseInputEvent event) {
		handleInput();
	}

	private void handleInput() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (mc.currentScreen != null || player == null)
			return;
		if (keyBindings[0].isPressed()) {
			((ClientNetworkHandler) IblisMod.network).sendCommand(ServerCommands.RELOAD_WEAPON);
			return;
		}
		if (mc.player.getHeldItemMainhand().getItem() instanceof ICustomLeftClickItem
				&& mc.gameSettings.keyBindAttack.isPressed()) {
			((ClientNetworkHandler) IblisMod.network).sendCommand(ServerCommands.LEFT_CLICK);
		}
		if (!shieldPunch 
				&& player.isActiveItemStackBlocking() 
				&& player.onGround
				&& mc.gameSettings.keyBindAttack.isPressed()) {
			shieldPunch = true;
		}
		if (!kick 
				&& !player.isHandActive()
				&& player.onGround
				&& mc.gameSettings.keyBindUseItem.isKeyDown() 
				&& mc.gameSettings.keyBindAttack.isPressed()) {
			kick = true;
		}
	}

	@SubscribeEvent
	public void onCheckingPlayerIsInBlock(PlayerSPPushOutOfBlocksEvent event) {
		EntityPlayerSP player = (EntityPlayerSP) event.getEntityPlayer();
		if(player.isRiding())
			return;
		if (!player.isHandActive())
			return;
		// Ugly way to compensate movement speed decreasing on using item
		if (player.getActiveItemStack().getItem() instanceof ItemBow) {
			double archery = PlayerSkills.ARCHERY.getFullSkillValue(player);
			if (archery < 5.1d)
				return;
			float multiliper = 5.0f - 20.0f / (float) archery;
			player.movementInput.moveStrafe *= multiliper;
			player.movementInput.moveForward *= multiliper;
		}
		if (player.getActiveItemStack().getItem() == IblisItems.CROSSBOW_RELOADING) {
			player.movementInput.moveStrafe = 0;
			player.movementInput.moveForward = 0;
		}
		if (player.isActiveItemStackBlocking()) {
			if (shieldPunch) {
				float f = player.rotationYaw * 0.017453292F;
				player.motionX -= (double) (MathHelper.sin(f) * 0.2F) * player.getCooledAttackStrength(0);
				player.motionZ += (double) (MathHelper.cos(f) * 0.2F) * player.getCooledAttackStrength(0);
				player.swingArm(player.getActiveHand());
				shieldPunch = false;
				((ClientNetworkHandler) IblisMod.network).sendCommand(ServerCommands.SHIELD_PUNCH);
			}
			double parry = PlayerSkills.PARRY.getFullSkillValue(player);
			if (parry < 5.1d)
				return;
			float multiliper = 5.0f - 20.0f / (float) parry;
			player.movementInput.moveStrafe *= multiliper;
			player.movementInput.moveForward *= multiliper;
		}
	}

	@SubscribeEvent
	public void onEvent(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (player != Minecraft.getMinecraft().player)
			return;
		if (event.side != Side.CLIENT)
			return;
		if (event.phase == Phase.END) {
			if (!event.player.isSprinting()) {
				sprintCounter = 0;
			} else if (sprintCounter < PlayerUtils.MAX_SPRINT_SPEED) {
				if (sprintCounter == 0)
					sprintCounter = 1;
				else if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
					sprintCounter++;
			}
			// Make it more crude so packets will not spawned too often
			if (sprintCounter >>> NETWORK_SENSIBILITY_BIT != lastSprintCounter >>> NETWORK_SENSIBILITY_BIT) {
				if (lastSprintCounter == 0) // Start of sprint
					sprintingStartPos = event.player.getPosition();
				// Sprint counter cannot go back unless you stop movement.
				else if (lastSprintCounter == PlayerUtils.MAX_SPRINT_SPEED) {
					double dsq = event.player.getPosition().distanceSq(sprintingStartPos);
					dsq = Math.sqrt(dsq);
					((ClientNetworkHandler) IblisMod.network).sendPlayerRunnedDistance((float) dsq);
				}
				lastSprintCounter = sprintCounter;
				((ClientNetworkHandler) IblisMod.network).sendCommandApplySprintingSpeedModifier(sprintCounter);
			}
		} else {
			if (kick) {
				kick = false;
				((ClientNetworkHandler) IblisMod.network).sendCommand(ServerCommands.KICK);
			}
			if (shieldPunch) {
				shieldPunch = false;
				if (player.isActiveItemStackBlocking()) {
					float f = player.rotationYaw * 0.017453292F;
					player.motionX -= (double) (MathHelper.sin(f) * 0.2F);
					player.motionZ += (double) (MathHelper.cos(f) * 0.2F);
//					player.swingArm(player.getActiveHand());
					((ClientNetworkHandler) IblisMod.network).sendCommand(ServerCommands.SHIELD_PUNCH);
				}
			}
			if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() && PlayerUtils.canJump(event.player)) {
				if (sprintButtonCounter < PlayerUtils.MAX_SPRINT_SPEED)
					sprintButtonCounter++;
			} else
				sprintButtonCounter = 0;
			if (sprintButtonCounter >>> NETWORK_SENSIBILITY_BIT != lastSprintButtonCounter >>> NETWORK_SENSIBILITY_BIT) {
				PlayerUtils.saveSprintButtonCounterState(Minecraft.getMinecraft().player, sprintButtonCounter);
				((ClientNetworkHandler) IblisMod.network).sendSprintButtonCounterState(sprintButtonCounter);
				lastSprintButtonCounter = sprintButtonCounter;
			}

		}
	}
}
