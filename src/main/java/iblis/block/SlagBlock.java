package iblis.block;

import java.util.Random;

import iblis.init.IblisItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SlagBlock extends Block {

	public SlagBlock(Material materialIn) {
		super(materialIn);
	}

	public int quantityDroppedWithBonus(int fortune, Random random) {
		return MathHelper.clamp(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 3);
	}

	public int quantityDropped(Random random) {
		return 1 + random.nextInt(1);
	}

	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return IblisItems.INGOT;
	}

	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MapColor.CLAY;
	}
}
