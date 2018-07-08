package iblis.init;

import static iblis.IblisMod.MODID;
import static iblis.IblisMod.creativeTab;

import iblis.block.BlockLabTable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IblisBlocks {
	public static Block LAB_TABLE;
	
	public static void init(){
		LAB_TABLE = new BlockLabTable(Material.GLASS).setRegistryName(MODID, "chemical_lab_installation").setCreativeTab(creativeTab).setUnlocalizedName("iblis.chemical_lab_installation");
		registerBlock(LAB_TABLE,new ItemBlock(LAB_TABLE));
	}
	
	private static void registerBlock(Block block, Item item) {
		RegistryEventHandler.blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistryEventHandler.items.add(item);
	}

	public static void registerRenders() {
		registerRender(LAB_TABLE, 0, LAB_TABLE.getRegistryName());
	}

	@SideOnly(value=Side.CLIENT)
	private static void registerRender(Block block, int metadata, ResourceLocation modelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), metadata,
				new ModelResourceLocation(modelResourceLocation, "inventory"));
	}
}
