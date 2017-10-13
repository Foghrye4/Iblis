package iblis;

import java.io.IOException;

import iblis.ClientNetworkHandler.ClientCommands;
import iblis.crafting.PlayerSensitiveShapedRecipeWrapper;
import iblis.init.IblisSounds;
import iblis.item.ItemShotgun;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.player.PlayerUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ServerNetworkHandler {

	public enum ServerCommands {
		UPDATE_CHARACTERISTIC, RELOAD_WEAPON, APPLY_SPRINTING_SPEED_MODIFIER, RUNNED_DISTANCE_INFO, SPRINTING_BUTTON_INFO, TRAIN_TO_CRAFT;
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
			World world = server.getWorld(worldDimensionId);
			EntityPlayerMP player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			characteristic.raiseCharacteristic(player);
			break;
		case RELOAD_WEAPON:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
			if(held.getItem() instanceof ItemShotgun) {
				ItemShotgun shotgun = (ItemShotgun)held.getItem();
				player.setHeldItem(EnumHand.MAIN_HAND, shotgun.getReloading(held));
				world.playSound(null, player.posX, player.posY, player.posZ, IblisSounds.shotgun_charging,
						SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.2f + 0.8f);
			}
			break;
		case APPLY_SPRINTING_SPEED_MODIFIER:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			int sprintingState = byteBufInputStream.readInt();
			PlayerUtils.applySprintingSpeedModifier(player, sprintingState);
			break;
		case RUNNED_DISTANCE_INFO:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			world = server.getWorld(worldDimensionId);
			player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			// Please don't cheat. I want to keep it client side.
			PlayerSkills.RUNNING.raiseSkill(player, byteBufInputStream.readFloat());
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
			if(player.openContainer instanceof ContainerWorkbench){
				ContainerWorkbench workBenchContainer = (ContainerWorkbench) player.openContainer;
				IRecipe recipe = CraftingManager.findMatchingRecipe(workBenchContainer.craftMatrix, world);
				if (recipe instanceof PlayerSensitiveShapedRecipeWrapper) {
					Slot slotCrafting = workBenchContainer.getSlotFromInventory(workBenchContainer.craftResult, 0);
					slotCrafting.onTake(player, slotCrafting.getStack());
					((PlayerSensitiveShapedRecipeWrapper) recipe).raiseSkill(player, 2);
				}
			}
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
	public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
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
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), new TargetPoint(playerIn.dimension, targetPos.x, targetPos.y, targetPos.z, 64d));
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
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), new TargetPoint(playerIn.dimension, targetPos.x, targetPos.y, targetPos.z, 64d));
	}

	public void sendRefreshTrainCraftButton(EntityPlayerMP player) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.REFRESH_CRAFTING_BUTTONS.ordinal());
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID), player);
	}
}
