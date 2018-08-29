package iblis.init;

import static iblis.IblisMod.MODID;
import static iblis.IblisMod.creativeTab;

import iblis.block.BlockGrape;
import iblis.block.BlockLabTable;
import iblis.item.ItemBlockEdible;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

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
}
