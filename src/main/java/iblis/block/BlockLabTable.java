package iblis.block;

import java.util.List;

import javax.annotation.Nullable;

import iblis.tileentity.TileEntityLabTable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLabTable extends Block implements ITileEntityProvider {

	public static final AxisAlignedBB BRUSH_AABB = new AxisAlignedBB(0D / 16D, 0D / 16D, 9D / 16D, 4D / 16D, 2D / 16D,
			15D / 16D);
	public static final AxisAlignedBB SEPARATOR_OUT_AABB = new AxisAlignedBB(7D / 16D, 0D / 16D, 9D / 16D, 9D / 16D,
			4D / 16D, 12D / 16D);
	public static final AxisAlignedBB SEPARATOR_IN_AABB = new AxisAlignedBB(7D / 16D, 4D / 16D, 9D / 16D, 9D / 16D,
			10D / 16D, 12D / 16D);
	public static final AxisAlignedBB FILTER_IN_AABB = new AxisAlignedBB(2D / 16D, 4D / 16D, 2D / 16D, 5D / 16D,
			7D / 16D, 5D / 16D);
	public static final AxisAlignedBB FILTER_OUT_AABB = new AxisAlignedBB(2D / 16D, 0D / 16D, 2D / 16D, 5D / 16D,
			4D / 16D, 5D / 16D);
	public static final AxisAlignedBB REACTOR_OUT_AABB = new AxisAlignedBB(10D / 16D, 0D / 16D, 3D / 16D, 14D / 16D,
			4D / 16D, 6D / 16D);
	public static final AxisAlignedBB BURNER_AABB = new AxisAlignedBB(10D / 16D, 0D / 16D, 10D / 16D, 14D / 16D,
			4D / 16D, 14D / 16D);
	public static final AxisAlignedBB REACTOR_AABB = new AxisAlignedBB(10D / 16D, 4D / 16D, 10D / 16D, 14D / 16D,
			8D / 16D, 14D / 16D);
	public static final AxisAlignedBB SELECTION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	public static final float BLOCK_REACH_DISTANCE = 1.0f;

	public BlockLabTable(Material materialIn) {
		super(materialIn);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLabTable();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	public enum SubBox {
		REACTOR(10, 4, 10, 14, 8, 14), 
		REACTOR_OUT(10, 0, 3, 14, 4, 6), 
		FILTER(2, 5, 2, 5, 9, 5), 
		FILTER_OUT(2, 0, 2, 5, 5, 5),
		SEPARATOR(6, 5, 9, 9, 14, 12), 
		SEPARATOR_OUT(6, 0, 9, 9, 5, 12), 
		BRUSH(0, 0, 9, 4, 2, 15), 
		BURNER(10, 0, 10, 14, 4, 14);
		public final AxisAlignedBB bb;
		private final static int SEGMENTS = 2;

		SubBox(int x1, int y1, int z1, int x2, int y2, int z2) {
			bb = new AxisAlignedBB(x1 / 16d, y1 / 16d, z1 / 16d, x2 / 16d, y2 / 16d, z2 / 16d);
		}

		public RayTraceResult intersectsAtPos(Vec3d min, Vec3d max, BlockPos pos) {
			AxisAlignedBB bb1 = getBBAt(pos);
			RayTraceResult traceResult = bb1.calculateIntercept(min, max);
			if (traceResult == null)
				return null;
			float segmentPosY = (float) MathHelper.clamp((traceResult.hitVec.y - bb1.minY) / (bb1.maxY - bb1.minY), 0.1,
					0.9);
			traceResult.subHit = (int) (SEGMENTS * segmentPosY);
			return traceResult;
		}

		public AxisAlignedBB getBBAt(BlockPos pos) {
			AxisAlignedBB bb1 = bb.offset(pos);
			return bb1;
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		return state.isFullCube();
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (this.canPlaceBlockAt(worldIn, pos))
			return;
		if (worldIn.getBlockState(pos).getBlock() == this) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		a: addCollisionBoxToList(pos, entityBox, collidingBoxes, SELECTION_AABB);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SELECTION_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		return true;
	}
}
