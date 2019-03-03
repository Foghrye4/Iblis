package iblis.init;

import static iblis.IblisMod.MODID;
import static iblis.IblisMod.creativeTab;

import iblis.block.IronCoalBlock;
import iblis.block.SlagBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class IblisBlocks {
	
	public static final Block IRON_COAL = new IronCoalBlock(Material.CLAY).setCreativeTab(creativeTab).setHardness(3.0F).setResistance(5.0F).setUnlocalizedName("iblis.iron_coal").setRegistryName(MODID, "iron_coal");
	public static final Block IRONORE_COAL = new IronCoalBlock(Material.CLAY).setCreativeTab(creativeTab).setHardness(3.0F).setResistance(5.0F).setUnlocalizedName("iblis.ironore_coal").setRegistryName(MODID, "ironore_coal");
	public static final Block SLAG = new SlagBlock(Material.CLAY).setCreativeTab(creativeTab).setHardness(3.0F).setResistance(5.0F).setUnlocalizedName("iblis.slag").setRegistryName(MODID, "slag");
	
	public static void init(){
		registerBlock(IRON_COAL, new ItemBlock(IRON_COAL));
		registerBlock(IRONORE_COAL, new ItemBlock(IRONORE_COAL));
		registerBlock(SLAG, new ItemBlock(SLAG));
	}
	
	private static void registerBlock(Block block, Item item) {
		RegistryEventHandler.blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistryEventHandler.items.add(item);
	}
}
