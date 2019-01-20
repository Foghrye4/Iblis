package iblis.init;

import static iblis.IblisMod.MODID;
import static iblis.IblisMod.creativeTab;

import iblis.item.ItemBlockEdible;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class IblisBlocks {
	
	public static void init(){
	}
	
	private static void registerBlock(Block block, Item item) {
		RegistryEventHandler.blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistryEventHandler.items.add(item);
	}
}
