package iblis_headshots;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ServerNetworkHandler {

	public enum ClientCommands {
		SPAWN_HEADSHOT_PARTICLE;
	}

	protected static FMLEventChannel channel;

	public void load() {
		if (channel == null) {
			channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(IblisHeadshotsMod.MODID);
			channel.register(this);
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setServer(MinecraftServer serverIn) {
	}

	public void spawnHeadshotParticle(World world, Vec3d pos, Vec3d speed, int maxAge) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SPAWN_HEADSHOT_PARTICLE.ordinal());
		byteBufOutputStream.writeDouble(pos.x);
		byteBufOutputStream.writeDouble(pos.y);
		byteBufOutputStream.writeDouble(pos.z);
		byteBufOutputStream.writeDouble(speed.x);
		byteBufOutputStream.writeDouble(speed.y);
		byteBufOutputStream.writeDouble(speed.z);
		byteBufOutputStream.writeInt(maxAge);
		channel.sendToAllAround(new FMLProxyPacket(byteBufOutputStream, IblisHeadshotsMod.MODID),
				new TargetPoint(world.provider.getDimension(), pos.x, pos.y, pos.z, 64d));
	}

}
