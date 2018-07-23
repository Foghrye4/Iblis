package iblis.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
