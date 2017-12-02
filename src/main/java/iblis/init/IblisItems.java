package iblis.init;

import iblis.IblisMod;
import iblis.item.ItemCrossbow;
import iblis.item.ItemCrossbowReloading;
import iblis.item.ItemGuideBook;
import iblis.item.ItemHeavyShield;
import iblis.item.ItemMedkit;
import iblis.item.ItemShotgun;
import iblis.item.ItemShotgunReloading;
import iblis.item.ItemThrowingWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IblisItems {
	public static Item GUIDE;
	public static Item SHOTGUN;
	public static Item SHOTGUN_RELOADING;
	public static Item SHOTGUN_BULLET;
	public static Item CROSSBOW;
	public static Item CROSSBOW_RELOADING;
	public static Item CROSSBOW_BOLT;
	public static Item INGOT_STEEL;
	public static Item NONSTERILE_MEDKIT;
	public static Item MEDKIT;
	public static Item BOULDER;
	public static Item IRON_THROWING_KNIFE;
	public static Item HEAVY_SHIELD;
	
	public static void init(){
		GUIDE = new ItemGuideBook();
		SHOTGUN = new ItemShotgun();
		SHOTGUN_RELOADING = new ItemShotgunReloading(SHOTGUN);
		SHOTGUN_BULLET = new Item();
		CROSSBOW = new ItemCrossbow();
		CROSSBOW_RELOADING = new ItemCrossbowReloading(CROSSBOW);
		CROSSBOW_BOLT = new Item();
		INGOT_STEEL = new Item();
		NONSTERILE_MEDKIT = new Item();
		MEDKIT = new ItemMedkit();
		BOULDER = new ItemThrowingWeapon(ItemThrowingWeapon.ThrowableType.BOULDER);
		IRON_THROWING_KNIFE = new ItemThrowingWeapon(ItemThrowingWeapon.ThrowableType.IRON_KNIFE);
		HEAVY_SHIELD = new ItemHeavyShield();
		
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
		CROSSBOW.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("double_crossbow")
			.setRegistryName(IblisMod.MODID, "double_crossbow")
			.setHasSubtypes(false)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		CROSSBOW_RELOADING.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("double_crossbow")
			.setRegistryName(IblisMod.MODID, "double_crossbow_reloading")
			.setHasSubtypes(true)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		CROSSBOW_BOLT.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("crossbow_bolt")
			.setRegistryName(IblisMod.MODID, "crossbow_bolt")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		INGOT_STEEL.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("ingot_steel")
			.setRegistryName(IblisMod.MODID, "ingot_steel")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		NONSTERILE_MEDKIT.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("non-sterile_medkit")
			.setRegistryName(IblisMod.MODID, "non-sterile_medkit")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(1);
		MEDKIT.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("medkit")
			.setRegistryName(IblisMod.MODID, "medkit")
			.setHasSubtypes(false)
			.setMaxDamage(10)
			.setMaxStackSize(1);
		BOULDER.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("boulder")
			.setRegistryName(IblisMod.MODID, "boulder")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(16);
		IRON_THROWING_KNIFE.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("iron_throwing_knife")
			.setRegistryName(IblisMod.MODID, "iron_throwing_knife")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(16);
		HEAVY_SHIELD.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("heavy_shield")
			.setRegistryName(IblisMod.MODID, "heavy_shield")
			.setHasSubtypes(false)
			.setMaxDamage(600)
			.setMaxStackSize(1);
		registerItem(GUIDE);
		registerItem(SHOTGUN);
		registerItem(SHOTGUN_RELOADING);
		registerItem(SHOTGUN_BULLET);
		registerItem(CROSSBOW);
		registerItem(CROSSBOW_RELOADING);
		registerItem(CROSSBOW_BOLT);
		registerItem(INGOT_STEEL);
		registerItem(NONSTERILE_MEDKIT);
		registerItem(MEDKIT);
		registerItem(BOULDER);
		registerItem(IRON_THROWING_KNIFE);
		registerItem(HEAVY_SHIELD);
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
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_BULLET, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"shotgun_bullet"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INGOT_STEEL, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"ingot_steel"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NONSTERILE_MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"non-sterile_medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(BOULDER, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"boulder"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(IRON_THROWING_KNIFE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"iron_throwing_knife"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HEAVY_SHIELD, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"heavy_shield"), "inventory"));
	}
}
