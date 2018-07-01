package iblis;

import java.io.IOException;

import iblis.client.ClientRenderEventHandler;
import iblis.gui.GuiEventHandler;
import iblis.init.IblisParticles;
import iblis.player.PlayerCharacteristics;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ClientNetworkHandler extends ServerNetworkHandler {

	@SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException {
		Minecraft mc = Minecraft.getMinecraft();
		ByteBuf data = event.getPacket().payload();
		PacketBuffer byteBufInputStream = new PacketBuffer(data);
		double posX, posY, posZ, xSpeed, ySpeed, zSpeed;
		WorldClient world = mc.world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		int playerId = 0;
		Entity entity;
		switch (ClientCommands.values()[byteBufInputStream.readByte()]) {
		case REFRESH_GUI:
			if (mc.currentScreen != null)
				mc.currentScreen.initGui();
			break;
		case SEND_PLAYER_BOOK_LIST_INFO:
			NBTTagList books = new NBTTagList();
			int tagCount = byteBufInputStream.readInt();
			for (int i = 0; i < tagCount; i++) {
				books.appendTag(byteBufInputStream.readCompoundTag());
			}
			player.getEntityData().setTag("exploredBooks", books);
			break;
		case SPAWN_BLOCK_PARTICLES:
			double targetX = byteBufInputStream.readDouble();
			double targetY = byteBufInputStream.readDouble();
			double targetZ = byteBufInputStream.readDouble();
			double impactVectorX = byteBufInputStream.readDouble();
			double impactVectorY = byteBufInputStream.readDouble();
			double impactVectorZ = byteBufInputStream.readDouble();
			int blockStateId = byteBufInputStream.readInt();
			for (int i = 0; i < world.rand.nextInt(8) + 2; i++)
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, targetX, targetY, targetZ,
						-impactVectorX * 0.5 + world.rand.nextFloat() - 0.5f,
						-impactVectorY * 0.5 + world.rand.nextFloat() - 0.8f,
						-impactVectorZ * 0.5 + world.rand.nextFloat() - 0.5f, blockStateId);
			break;
		case SPAWN_PARTICLE:
			targetX = byteBufInputStream.readDouble();
			targetY = byteBufInputStream.readDouble();
			targetZ = byteBufInputStream.readDouble();
			double speedX = byteBufInputStream.readDouble();
			double speedY = byteBufInputStream.readDouble();
			double speedZ = byteBufInputStream.readDouble();
			int particleId = byteBufInputStream.readInt();
			world.spawnParticle(EnumParticleTypes.values()[particleId], targetX, targetY, targetZ, speedX, speedY,
					speedZ, 0);
			break;
		case SPAWN_PARTICLES:
			targetX = byteBufInputStream.readDouble();
			targetY = byteBufInputStream.readDouble();
			targetZ = byteBufInputStream.readDouble();
			impactVectorX = byteBufInputStream.readDouble();
			impactVectorY = byteBufInputStream.readDouble();
			impactVectorZ = byteBufInputStream.readDouble();
			particleId = byteBufInputStream.readInt();
			for (int i = 0; i < world.rand.nextInt(8) + 2; i++)
				world.spawnParticle(EnumParticleTypes.values()[particleId], targetX, targetY, targetZ,
						-impactVectorX * 0.5 + world.rand.nextFloat() - 0.5f,
						-impactVectorY * 0.5 + world.rand.nextFloat() - 0.8f,
						-impactVectorZ * 0.5 + world.rand.nextFloat() - 0.5f, 0);
			break;
		case SPAWN_CUSTOM_PARTICLE:
			posX = byteBufInputStream.readDouble();
			posY = byteBufInputStream.readDouble();
			posZ = byteBufInputStream.readDouble();
			xSpeed = byteBufInputStream.readDouble();
			ySpeed = byteBufInputStream.readDouble();
			zSpeed = byteBufInputStream.readDouble();
			particleId  = byteBufInputStream.readInt();
			((ClientProxy)IblisMod.proxy).spawnParticle(IblisParticles.values()[particleId], posX, posY, posZ, xSpeed, ySpeed, zSpeed);
			break;
		case SPAWN_CUSTOM_PARTICLES:
			posX = byteBufInputStream.readDouble();
			posY = byteBufInputStream.readDouble();
			posZ = byteBufInputStream.readDouble();
			xSpeed = byteBufInputStream.readDouble();
			ySpeed = byteBufInputStream.readDouble();
			zSpeed = byteBufInputStream.readDouble();
			particleId  = byteBufInputStream.readInt();
			for (int i = 0; i < world.rand.nextInt(8) + 2; i++) {
				xSpeed+=world.rand.nextFloat()*0.2f-0.1f;
				ySpeed+=world.rand.nextFloat()*0.2f-0.1f;
				zSpeed+=world.rand.nextFloat()*0.2f-0.1f;
				((ClientProxy)IblisMod.proxy).spawnParticle(IblisParticles.values()[particleId], posX, posY, posZ, xSpeed, ySpeed, zSpeed);
			}
			break;
			
		case ADD_DECAL:
			posX = byteBufInputStream.readDouble();
			posY = byteBufInputStream.readDouble();
			posZ = byteBufInputStream.readDouble();
			IblisParticles decal = IblisParticles.values()[byteBufInputStream.readInt()];
			EnumFacing facing  = EnumFacing.VALUES[byteBufInputStream.readInt()];
			((ClientProxy)IblisMod.proxy).addDecal(decal, posX, posY, posZ, facing, byteBufInputStream.readInt(), byteBufInputStream.readFloat());
			break;
		case REFRESH_CRAFTING_BUTTONS:
			mc.addScheduledTask(new Runnable(){
				@Override
				public void run() {
					GuiEventHandler.instance.refreshTrainCraftingButton();
				}});
			break;
		case LAUNCH_KICK_ANIMATION:
			playerId = byteBufInputStream.readInt();
			float power = byteBufInputStream.readFloat();
			ClientRenderEventHandler.playerKickAnimationState.put(playerId,
					ClientRenderEventHandler.PLAYER_KICK_ANIMATION_LENGTH);
			entity = world.getEntityByID(playerId);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				living.limbSwingAmount = Math.min(1.5f, living.limbSwingAmount + power);
				if(entity == Minecraft.getMinecraft().player)
					ClientRenderEventHandler.renderFirstPersonPlayerkickAnimation = 1.7f;

			}
			break;
		case LAUNCH_SWING_ANIMATION:
			playerId = byteBufInputStream.readInt();
			entity = world.getEntityByID(playerId);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				living.swingArm(living.getActiveHand());
			}
			break;
		case RESET_COOLDOWN_AND_ACTIVE_HAND:
			if (player.isHandActive()) {
				player.resetActiveHand();
				player.setActiveHand(EnumHand.MAIN_HAND);
			}
			player.resetCooldown();
			break;
		case PLAY_EVENT:
			int eventNumber = byteBufInputStream.readInt();
			BlockPos pos = byteBufInputStream .readBlockPos();
			int eventData = byteBufInputStream.readInt();
			world.playEvent(player, eventNumber, pos, eventData);
			break;
		case SHOW_HINT:
			String hint = byteBufInputStream.readString(4096);
			GuiEventHandler.instance.showHint(hint);
			break;
		default:
			break;
		}
	}

	public void sendCharacteristicUpdate(PlayerCharacteristics characterictic) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ServerCommands.UPDATE_CHARACTERISTIC.ordinal());
		byteBufOutputStream.writeByte(characterictic.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID));
	}

	@Override
	public void sendPlayerBookListInfo(EntityPlayerMP playerIn) {
	}

	public void sendCommandApplySprintingSpeedModifier(int sprintCounter) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ServerCommands.APPLY_SPRINTING_SPEED_MODIFIER.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		byteBufOutputStream.writeInt(sprintCounter);
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID));
	}

	public void sendPlayerRunnedDistance(float distance) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ServerCommands.RUNNED_DISTANCE_INFO.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		byteBufOutputStream.writeFloat(distance);
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID));
	}

	public void sendSprintButtonCounterState(int sprintButtonCounter) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ServerCommands.SPRINTING_BUTTON_INFO.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		byteBufOutputStream.writeInt(sprintButtonCounter);
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID));
	}

	public void sendCommand(ServerCommands command) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(command.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, IblisMod.MODID));
	}
}
