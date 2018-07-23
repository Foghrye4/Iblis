package iblis.event;

import java.util.HashSet;
import java.util.Set;

import iblis.init.IblisBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisBlockEventHandler implements IWorldEventListener{
	
	Set<World> eventProviders = new HashSet<World>();
	
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote)
			return;
		if (event.getEntity() instanceof EntityPlayerMP && !eventProviders.contains(event.getWorld())) {
			event.getWorld().addEventListener(this);
			eventProviders.add(event.getWorld());
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		if(newState.getBlock() != Blocks.VINE || worldIn.rand.nextInt(8)!=0)
			return;
		int meta = newState.getBlock().getMetaFromState(newState);
		IBlockState toReplace = IblisBlocks.VINE.getStateFromMeta(meta);
		worldIn.setBlockState(pos, toReplace, 16);
	}

	@Override
	public void notifyLightSet(BlockPos pos) {
	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
	}

	@Override
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
			double y, double z, float volume, float pitch) {
	}

	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {
	}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
	}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed, int... parameters) {
	}

	@Override
	public void onEntityAdded(Entity entityIn) {
	}

	@Override
	public void onEntityRemoved(Entity entityIn) {
	}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {
	}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
	}
}
