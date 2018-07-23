package iblis.init;

import static iblis.IblisMod.MODID;
import static iblis.IblisMod.creativeTab;

import javax.annotation.Nullable;

import iblis.block.BlockGrape;
import iblis.block.BlockLabTable;
import iblis.item.ItemBlockEdible;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

public class IblisBlocks {
	public static Block LAB_TABLE = new BlockLabTable(Material.GLASS).setRegistryName(MODID, "chemical_lab_installation").setHardness(0.8F).setCreativeTab(creativeTab).setUnlocalizedName("iblis.chemical_lab_installation");
	public static Block VINE = new BlockGrape().setRegistryName(MODID, "vine").setCreativeTab(creativeTab).setUnlocalizedName("iblis.vine").setHardness(0.2F);
	
	public static void init(){
		registerBlock(LAB_TABLE,new ItemBlock(LAB_TABLE));
		registerBlock(VINE,new ItemBlockEdible(VINE, 4, 0.3f, false));
	}
	
	private static void registerBlock(Block block, Item item) {
		RegistryEventHandler.blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistryEventHandler.items.add(item);
	}

	public static void registerRenders() {
		registerRender(LAB_TABLE, 0, LAB_TABLE.getRegistryName());
		registerRender(VINE, 0, VINE.getRegistryName());
		final BlockColors blockColours = Minecraft.getMinecraft().getBlockColors();
		blockColours.registerBlockColorHandler(new IBlockColor() {
			public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos,
					int tintIndex) {
				return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos)
						: ColorizerFoliage.getFoliageColorBasic();
			}
		}, VINE);
	}

	@SideOnly(value=Side.CLIENT)
	private static void registerRender(Block block, int metadata, ResourceLocation modelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), metadata,
				new ModelResourceLocation(modelResourceLocation, "inventory"));
	}
}
