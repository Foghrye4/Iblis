package iblis.init;

import iblis.IblisMod;
import iblis.item.ItemGuideBook;
import iblis.item.ItemShotgun;
import iblis.item.ItemShotgunReloading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class IblisItems {
	public static Item GUIDE;
	public static Item SHOTGUN;
	public static Item SHOTGUN_RELOADING;
	public static Item SHOTGUN_BULLET;
	public static Item INGOT_STEEL;
	
	public static void init(){
		GUIDE = new ItemGuideBook();
		SHOTGUN = new ItemShotgun();
		SHOTGUN_RELOADING = new ItemShotgunReloading(SHOTGUN);
		SHOTGUN_BULLET = new Item();
		INGOT_STEEL = new Item();
		
		GUIDE.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("guide")
			.setRegistryName(IblisMod.MODID, "guide")
			.setHasSubtypes(true)
			.setMaxDamage(0)
			.setMaxStackSize(1);
		SHOTGUN.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("six_barrels_shotgun")
			.setRegistryName(IblisMod.MODID, "six_barrels_shotgun")
			.setHasSubtypes(false)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		SHOTGUN_RELOADING.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("six_barrels_shotgun")
			.setRegistryName(IblisMod.MODID, "six_barrels_shotgun_reloading")
			.setHasSubtypes(true)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		SHOTGUN_BULLET.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("shotgun_bullet")
			.setRegistryName(IblisMod.MODID, "shotgun_bullet")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		INGOT_STEEL.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("ingot_steel")
			.setRegistryName(IblisMod.MODID, "ingot_steel")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		
		registerItem(GUIDE);
		registerItem(SHOTGUN);
		registerItem(SHOTGUN_RELOADING);
		registerItem(SHOTGUN_BULLET);
		registerItem(INGOT_STEEL);
	}
	
	private static void registerItem(Item item) {
		RegistryEventHandler.items.add(item);
	}
	
	@SideOnly(value=Side.CLIENT)
	public static void registerRenders() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"adventurer_diary"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 1,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"guide"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 2,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"guide_opened"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 1,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_1"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 2,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_2"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 3,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_3"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 4,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_4"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 5,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_5"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_RELOADING, 6,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_6"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_BULLET, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"shotgun_bullet"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INGOT_STEEL, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"ingot_steel"), "inventory"));
	}
}
