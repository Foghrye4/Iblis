package iblis.client.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DecalHelper {
	private static final AxisAlignedBB BED_AABB = new AxisAlignedBB(0.0D, 5/16D, 0.0D, 1.0D, 9/16D, 1.0D);
	private static final AxisAlignedBB CACTUS_BLOCK_AABB = new AxisAlignedBB(0.0625d, 0.0D, 0.0625d, 0.9375d, 1.0D, 0.9375d);
    private static final AxisAlignedBB FENCE_PILLAR_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    private static final AxisAlignedBB WALL_PILLAR_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final AxisAlignedBB WALL_NORTH_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6825D, 0.875D, 0.5D);
    private static final AxisAlignedBB WALL_SOUTH_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.5D, 0.6825D, 0.875D, 1.0D);
    private static final AxisAlignedBB WALL_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 0.5D, 0.875D, 0.6825D);
    private static final AxisAlignedBB WALL_EAST_AABB = new AxisAlignedBB(0.5D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6825D);
    
	public static void addDecalDisplayBoxToList(World world, BlockPos pos, AxisAlignedBB particleBB,
			List<AxisAlignedBB> collidingBoxes, IBlockState bstate) {
		if (bstate.getBlock() == Blocks.LADDER) {
			return;
		}
		if (bstate.getBlock() == Blocks.IRON_BARS) {
			return;
		}
		if (bstate.getBlock() == Blocks.ANVIL) {
			return;
		}
		if (bstate.getBlock() == Blocks.BREWING_STAND) {
			return;
		}
		if (bstate.getBlock() == Blocks.BED) {
			AxisAlignedBB bb = BED_AABB.offset(pos);
			if (particleBB.intersects(bb))
				collidingBoxes.add(bb);
			return;
		}
		if (bstate.getBlock() == Blocks.COBBLESTONE_WALL) {
			Block block = bstate.getBlock();
	        boolean north =  block.canBeConnectedTo(world, pos, EnumFacing.NORTH);
	        boolean east = block.canBeConnectedTo(world, pos, EnumFacing.EAST);
	        boolean south = block.canBeConnectedTo(world, pos, EnumFacing.SOUTH);
	        boolean west = block.canBeConnectedTo(world, pos, EnumFacing.WEST);
	        boolean noPillar = north && south && !west && !east || east && west && !north && !south;
	        if(!noPillar) {
				AxisAlignedBB bb = WALL_PILLAR_AABB.offset(pos);
				if (particleBB.intersects(bb)) {
					collidingBoxes.add(bb);
					return;
				}
	        }
	        if(north) {
				AxisAlignedBB bb = WALL_NORTH_AABB.offset(pos);
				if (particleBB.intersects(bb))
					collidingBoxes.add(bb);
	        }
	        if(south) {
				AxisAlignedBB bb = WALL_SOUTH_AABB.offset(pos);
				if (particleBB.intersects(bb))
					collidingBoxes.add(bb);
	        }
	        if(west) {
				AxisAlignedBB bb = WALL_WEST_AABB.offset(pos);
				if (particleBB.intersects(bb))
					collidingBoxes.add(bb);
	        }
	        if(east) {
				AxisAlignedBB bb = WALL_EAST_AABB.offset(pos);
				if (particleBB.intersects(bb))
					collidingBoxes.add(bb);
	        }
			return;
		}
		if (bstate.getBlock() instanceof BlockFence) {
			AxisAlignedBB bb = FENCE_PILLAR_AABB.offset(pos);
			if (particleBB.intersects(bb))
				collidingBoxes.add(bb);
			return;
		}
		if (bstate.getBlock() instanceof BlockFenceGate) {
			return;
		}
		if (bstate.getBlock() == Blocks.CACTUS) {
			AxisAlignedBB bb = CACTUS_BLOCK_AABB.offset(pos);
			if (particleBB.intersects(bb))
				collidingBoxes.add(bb);
			return;
		}
		bstate.addCollisionBoxToList(world, pos, particleBB, collidingBoxes, null, false);
	}

	public static int getDecalColour(World world, BlockPos pos, IBlockState bstate) {
		if(bstate.getBlock() == Blocks.GRASS)
			return 0x996a45;
		if(bstate.getBlock() == Blocks.BED)
			return 0x999999;
		return bstate.getMapColor(world, pos).colorValue;
	}
}
