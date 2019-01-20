package iblis;

import java.io.IOException;

import iblis.crafting.IRecipeRaiseSkill;
import iblis.init.IblisParticles;
import iblis.item.ICustomLeftClickItem;
import iblis.item.ItemFirearmsBase;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.util.PlayerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ServerNetworkHandler {

	public enum ClientCommands {
		REFRESH_GUI, SEND_PLAYER_BOOK_LIST_INFO, SPAWN_BLOCK_PARTICLES, SPAWN_PARTICLES, REFRESH_CRAFTING_BUTTONS, SPAWN_CUSTOM_PARTICLE, SPAWN_CUSTOM_PARTICLES, LAUNCH_KICK_ANIMATION, LAUNCH_SWING_ANIMATION, ADD_DECAL, PLAY_EVENT, SPAWN_PARTICLE, RESET_COOLDOWN_AND_ACTIVE_HAND, SHOW_HINT;
	}

	public enum ServerCommands {
		UPDATE_CHARACTERISTIC, RELOAD_WEAPON, APPLY_SPRINTING_SPEED_MODIFIER, RUNNED_DISTANCE_INFO, SPRINTING_BUTTON_INFO, TRAIN_TO_CRAFT, LEFT_CLICK, SHIELD_PUNCH, KICK, LAB_TABLE_GUI_ACTION;
	}

	protected static FMLEventChannel channel;
	private MinecraftServer server;

	public void load() {
		if (channel == null) {
			channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(IblisMod.MODID);
			channel.register(this);
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPacketFromClientToServer(FMLNetworkEvent.ServerCustomPacketEvent event) throws IOException {
		ByteBuf data = event.getPacket().payload();
		ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
		int playerEntityId;
		int worldDimensionId;
		switch (ServerCommands.values()[byteBufInputStream.read()]) {
		case UPDATE_CHARACTERISTIC:
			PlayerCharacteristics characteristic = PlayerCharacteristics.values()[byteBufInputStream.read()];
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			WorldServer world = server.getWorld(worldDimensionId);
			EntityPlayerMP player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			world.addScheduledTask(new TaskRaiseCharacteristic(characteristic, player));
			break;
		case RELOAD_WEAPON:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
			if (held.getItem() instanceof ItemFirearmsBase) {
				ItemFirearmsBase gun = (ItemFirearmsBase) held.getItem();
				player.setHeldItem(EnumHand.MAIN_HAND, gun.getReloading(held));
				gun.playReloadingSoundEffect(player);
			}
			break;
		case APPLY_SPRINTING_SPEED_MODIFIER:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			int sprintingState = byteBufInputStream.readInt();
			world.addScheduledTask(new TaskApplySprintingSpeedModifier(player, sprintingState));
			break;
		case RUNNED_DISTANCE_INFO:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			// Please don't cheat. I want to keep it client side.
			world.addScheduledTask(new TaskRaiseSkill(PlayerSkills.RUNNING, player, byteBufInputStream.readFloat()));
			break;
		case SPRINTING_BUTTON_INFO:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			int sprintButtonCounter = byteBufInputStream.readInt();
			PlayerUtils.saveSprintButtonCounterState(player, sprintButtonCounter);
			break;
		case TRAIN_TO_CRAFT:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			world.addScheduledTask(new TaskTrainCraftButton(player));
			break;
		case LEFT_CLICK:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
			if (itemstack.getItem() instanceof ICustomLeftClickItem) {
				ICustomLeftClickItem firearm = (ICustomLeftClickItem) itemstack.getItem();
				world.addScheduledTask(new TaskLaunchLeftClick(world, player, firearm));
			}
			break;
		case SHIELD_PUNCH:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			PlayerUtils.saveKnockState(player, PlayerUtils.KNOCK_BY_SHIELD);
			break;
		case KICK:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			PlayerUtils.saveKnockState(player, PlayerUtils.KNOCK_BY_KICK);
			break;
		default:
			break;
		}
		byteBufInputStream.close();
	}

	public void setServer(MinecraftServer serverIn) {
		this.server = serverIn;
	}

	@SubscribeEvent
	public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		this.sendPlayerBookListInfo((EntityPlayerMP) event.player);
	}

	public void sendPlayerBookListInfo(EntityPlayerMP playerIn) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SEND_PLAYER_BOOK_LIST_INFO.ordinal());
		NBTTagList books = playerIn.getEntityData().getTagList("exploredBooks", 10);
		byteBufOutputStream.writeInt(books.tagCount());
		for (int i = 0; i < books.tagCount(); i++) {
			NBTTagCompound book = books.getCompoundTagAt(i);
			byteBufOutputStream.writeCompoundTag(book);
		}
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), playerIn);
	}

	public void spawnBlockParticles(EntityPlayerMP playerIn, Vec3d targetPos, Vec3d impactVector, int blockStateID) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_BLOCK_PARTICLES.ordinal());
		byteBufOutputStream.writeDouble(targetPos.x);
		byteBufOutputStream.writeDouble(targetPos.y);
		byteBufOutputStream.writeDouble(targetPos.z);
		byteBufOutputStream.writeDouble(impactVector.x);
		byteBufOutputStream.writeDouble(impactVector.y);
		byteBufOutputStream.writeDouble(impactVector.z);
		byteBufOutputStream.writeInt(blockStateID);
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(playerIn.dimension, targetPos.x, targetPos.y, targetPos.z, 64d));
	}

	public void spawnParticle(EntityPlayer playerIn, double x, double y, double z, double speedX, double speedY,
			double speedZ, EnumParticleTypes particleType) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_PARTICLE.ordinal());
		byteBufOutputStream.writeDouble(x);
		byteBufOutputStream.writeDouble(y);
		byteBufOutputStream.writeDouble(z);
		byteBufOutputStream.writeDouble(speedX);
		byteBufOutputStream.writeDouble(speedY);
		byteBufOutputStream.writeDouble(speedZ);
		byteBufOutputStream.writeInt(particleType.ordinal());
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(playerIn.dimension, x, y, z, 64d));
	}

	public void spawnParticles(EntityPlayerMP playerIn, Vec3d targetPos, Vec3d impactVector, EnumParticleTypes crit) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_PARTICLES.ordinal());
		byteBufOutputStream.writeDouble(targetPos.x);
		byteBufOutputStream.writeDouble(targetPos.y);
		byteBufOutputStream.writeDouble(targetPos.z);
		byteBufOutputStream.writeDouble(impactVector.x);
		byteBufOutputStream.writeDouble(impactVector.y);
		byteBufOutputStream.writeDouble(impactVector.z);
		byteBufOutputStream.writeInt(crit.ordinal());
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(playerIn.dimension, targetPos.x, targetPos.y, targetPos.z, 64d));
	}

	public void sendRefreshTrainCraftButton(EntityPlayerMP player) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.REFRESH_CRAFTING_BUTTONS.ordinal());
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), player);
	}

	public void spawnCustomParticle(World world, Vec3d pos, Vec3d speed, IblisParticles particle) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_CUSTOM_PARTICLE.ordinal());
		byteBufOutputStream.writeDouble(pos.x);
		byteBufOutputStream.writeDouble(pos.y);
		byteBufOutputStream.writeDouble(pos.z);
		byteBufOutputStream.writeDouble(speed.x);
		byteBufOutputStream.writeDouble(speed.y);
		byteBufOutputStream.writeDouble(speed.z);
		byteBufOutputStream.writeInt(particle.ordinal());
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(world.provider.getDimension(), pos.x, pos.y, pos.z, 64d));
	}

	public void spawnCustomParticles(World world, Vec3d pos, Vec3d speed, IblisParticles particle) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_CUSTOM_PARTICLES.ordinal());
		byteBufOutputStream.writeDouble(pos.x);
		byteBufOutputStream.writeDouble(pos.y);
		byteBufOutputStream.writeDouble(pos.z);
		byteBufOutputStream.writeDouble(speed.x);
		byteBufOutputStream.writeDouble(speed.y);
		byteBufOutputStream.writeDouble(speed.z);
		byteBufOutputStream.writeInt(particle.ordinal());
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(world.provider.getDimension(), pos.x, pos.y, pos.z, 64d));
	}

	public void addDecal(World world, Vec3d pos, IblisParticles decal, EnumFacing facing, int bloodColour, float size) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.ADD_DECAL.ordinal());
		byteBufOutputStream.writeDouble(pos.x);
		byteBufOutputStream.writeDouble(pos.y);
		byteBufOutputStream.writeDouble(pos.z);
		byteBufOutputStream.writeInt(decal.ordinal());
		byteBufOutputStream.writeInt(facing.ordinal());
		byteBufOutputStream.writeInt(bloodColour);
		byteBufOutputStream.writeFloat(size);
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(world.provider.getDimension(), pos.x, pos.y, pos.z, 64d));
	}

	public void launchKickAnimation(EntityPlayer player, float power) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.LAUNCH_KICK_ANIMATION.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeFloat(power);
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64d));
	}

	public void launchSwingAnimation(EntityPlayer player) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.LAUNCH_SWING_ANIMATION.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID),
				new TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64d));
	}

	public void resetCooldownAndActiveHand(EntityPlayer playerIn) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.RESET_COOLDOWN_AND_ACTIVE_HAND.ordinal());
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), (EntityPlayerMP) playerIn);
	}

	public void playEvent(EntityPlayer playerIn, int eventNumber, BlockPos pos, int data) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.PLAY_EVENT.ordinal());
		byteBufOutputStream.writeInt(eventNumber);
		byteBufOutputStream.writeBlockPos(pos);
		byteBufOutputStream.writeInt(data);
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), new TargetPoint(
				playerIn.world.provider.getDimension(), playerIn.posX, playerIn.posY, playerIn.posZ, 64d));
	}
	
	public void showHintToPlayer(EntityPlayer playerIn, String unlocalisedHint){
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SHOW_HINT.ordinal());
		byteBufOutputStream.writeString(unlocalisedHint);
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), (EntityPlayerMP) playerIn);
	}

	private static class TaskLaunchLeftClick implements Runnable {
		final WorldServer world;
		final EntityPlayerMP player;
		final ICustomLeftClickItem firearm;

		public TaskLaunchLeftClick(WorldServer worldIn, EntityPlayerMP playerIn, ICustomLeftClickItem firearmIn) {
			world = worldIn;
			player = playerIn;
			firearm = firearmIn;
		}

		@Override
		public void run() {
			firearm.onLeftClick(world, player, EnumHand.MAIN_HAND);
		}
	}

	private static class TaskRaiseCharacteristic implements Runnable {
		final PlayerCharacteristics characteristic;
		final EntityPlayerMP player;

		public TaskRaiseCharacteristic(PlayerCharacteristics characteristicIn, EntityPlayerMP playerIn) {
			characteristic = characteristicIn;
			player = playerIn;
		}

		@Override
		public void run() {
			characteristic.raiseCharacteristic(player);
		}
	}

	private static class TaskRaiseSkill implements Runnable {
		final PlayerSkills skill;
		final double amount;
		final EntityPlayerMP player;

		public TaskRaiseSkill(PlayerSkills skillIn, EntityPlayerMP playerIn, double amountIn) {
			skill = skillIn;
			player = playerIn;
			amount = amountIn;
		}

		@Override
		public void run() {
			skill.raiseSkill(player, amount);
		}
	}

	private static class TaskApplySprintingSpeedModifier implements Runnable {
		final int sprintingState;
		final EntityPlayerMP player;

		public TaskApplySprintingSpeedModifier(EntityPlayerMP playerIn, int sprintingStateIn) {
			player = playerIn;
			sprintingState = sprintingStateIn;
		}

		@Override
		public void run() {
			PlayerUtils.applySprintingSpeedModifier(player, sprintingState);
		}
	}

	private static class TaskTrainCraftButton implements Runnable {
		final EntityPlayerMP player;

		public TaskTrainCraftButton(EntityPlayerMP playerIn) {
			player = playerIn;
		}

		@Override
		public void run() {
			if (player.openContainer instanceof ContainerWorkbench) {
				ContainerWorkbench workBenchContainer = (ContainerWorkbench) player.openContainer;
				IRecipe recipe = CraftingManager.findMatchingRecipe(workBenchContainer.craftMatrix, player.world);
				if (recipe instanceof IRecipeRaiseSkill) {
					Slot slotCrafting = workBenchContainer.getSlotFromInventory(workBenchContainer.craftResult, 0);
					slotCrafting.onTake(player, slotCrafting.getStack());
					((IRecipeRaiseSkill) recipe).getSensitiveSkill().raiseSkill(player, 2.0);
					IblisMod.network.sendRefreshTrainCraftButton(player);
				}
			}
		}
	}
}
