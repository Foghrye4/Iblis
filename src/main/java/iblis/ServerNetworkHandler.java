package iblis;


import iblis.init.IblisParticles;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
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

	public void load() {
		if (channel == null) {
			channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(IblisMod.MODID);
			channel.register(this);
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setServer(MinecraftServer serverIn) {
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
}
