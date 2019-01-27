package iblis.block;

import java.util.Random;

import iblis.init.IblisBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IronCoalBlock extends Block {

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

	public IronCoalBlock(Material materialIn) {
		super(materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
	}

	public int tickRate(World worldIn) {
		return 30;
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
	}

	private boolean isIgnited(World worldIn, BlockPos pos) {
		int[] xyz = { 0, 0, -1, 0, 0, 1, 0, 0 };
		MutableBlockPos mbpos = new MutableBlockPos(pos);
		for (int i = 2; i < xyz.length; i++) {
			mbpos.setPos(pos.getX() + xyz[i - 2], pos.getY() + xyz[i - 1], pos.getZ() + xyz[i]);
			IBlockState neighborState = worldIn.getBlockState(mbpos);
			if (neighborState.getMaterial() == Material.FIRE) {
				return true;
			}
		}
		return false;
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		boolean isIgnited = isIgnited(worldIn, pos);
		if (!isIgnited)
			return;
		int i = ((Integer) state.getValue(AGE)).intValue();
		if (i < 15) {
			state = state.withProperty(AGE, Integer.valueOf(i + rand.nextInt(3) / 2));
			worldIn.setBlockState(pos, state, 11);
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
			this.setNeighborsInFire(worldIn, pos);
		} else {
			worldIn.setBlockState(pos, IblisBlocks.SLAG.getDefaultState(), 11);
		}
	}

	private void setNeighborsInFire(World worldIn, BlockPos pos) {
		MutableBlockPos mbpos = new MutableBlockPos(pos);
		for (EnumFacing facing : EnumFacing.values()) {
			mbpos.setPos(pos.getX() + facing.getFrontOffsetX(), pos.getY() + facing.getFrontOffsetY(),
					pos.getZ() + facing.getFrontOffsetZ());
			IBlockState neighborState = worldIn.getBlockState(mbpos);
			if (neighborState == Blocks.AIR.getDefaultState() || worldIn.rand.nextInt(300) < neighborState.getBlock()
					.getFlammability(worldIn, pos, facing.getOpposite())) {
				worldIn.setBlockState(mbpos, Blocks.FIRE.getDefaultState(), 11);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(isIgnited(worldIn, pos))
			Blocks.FIRE.randomDisplayTick(stateIn, worldIn, pos, rand);
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}

	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(AGE)).intValue();
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AGE });
	}

}
