package iblis.block;

import java.util.Random;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockGrape extends BlockVine {
	
	@Override
	@SuppressWarnings("deprecation")
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		int meta = state.getBlock().getMetaFromState(state);
		IBlockState toReplace = Blocks.VINE.getStateFromMeta(meta);
		super.updateTick(worldIn, pos, toReplace, rand);
	}
}
