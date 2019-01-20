package iblis_headshots;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ClientNetworkHandler extends ServerNetworkHandler {

	@SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException {
		Minecraft mc = Minecraft.getMinecraft();
		ByteBuf data = event.getPacket().payload();
		PacketBuffer byteBufInputStream = new PacketBuffer(data);
		double posX, posY, posZ, xSpeed, ySpeed, zSpeed;
		int maxAge;
		WorldClient world = mc.world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		switch (ClientCommands.values()[byteBufInputStream.readByte()]) {
		case SPAWN_HEADSHOT_PARTICLE:
			posX = byteBufInputStream.readDouble();
			posY = byteBufInputStream.readDouble();
			posZ = byteBufInputStream.readDouble();
			xSpeed = byteBufInputStream.readDouble();
			ySpeed = byteBufInputStream.readDouble();
			zSpeed = byteBufInputStream.readDouble();
			maxAge = byteBufInputStream.readInt();
			mc.addScheduledTask(() -> {
				((ClientProxy)IblisHeadshotsMod.proxy).spawnParticle(posX, posY, posZ, xSpeed, ySpeed, zSpeed, maxAge);
	            world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.25F, 1.0F);
			});
			break;
		default:
			break;
		}
	}
}
