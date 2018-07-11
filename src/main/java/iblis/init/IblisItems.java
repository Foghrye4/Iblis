package iblis.init;

import iblis.IblisMod;
import iblis.item.IblisItemArmor;
import iblis.item.ItemCrossbow;
import iblis.item.ItemCrossbowReloading;
import iblis.item.ItemGuideBook;
import iblis.item.ItemHeavyShield;
import iblis.item.ItemMedkit;
import iblis.item.ItemShotgun;
import iblis.item.ItemShotgunAmmo;
import iblis.item.ItemShotgunReloading;
import iblis.item.ItemSubstanceContainer;
import iblis.item.ItemThrowingWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IblisItems {
	public static Item GUIDE = new ItemGuideBook();
	public static Item SHOTGUN = new ItemShotgun();
	public static Item SHOTGUN_RELOADING = new ItemShotgunReloading(SHOTGUN);
	public static Item SHOTGUN_BULLET = new ItemShotgunAmmo();
	public static Item CROSSBOW = new ItemCrossbow();
	public static Item CROSSBOW_RELOADING = new ItemCrossbowReloading(CROSSBOW);
	public static Item CROSSBOW_BOLT = new Item();
	public static Item INGOT_STEEL = new Item();
	public static Item NUGGET_STEEL = new Item();
	public static Item TRIGGER_SPRING = new Item();
	public static Item NONSTERILE_MEDKIT = new Item();
	public static ItemMedkit MEDKIT = new ItemMedkit();
	public static ItemSubstanceContainer SUBSTANCE_CONTAINER = new ItemSubstanceContainer();
	public static Item BOULDER = new ItemThrowingWeapon(ItemThrowingWeapon.ThrowableType.BOULDER);
	public static Item IRON_THROWING_KNIFE = new ItemThrowingWeapon(ItemThrowingWeapon.ThrowableType.IRON_KNIFE);
	public static Item HEAVY_SHIELD = new ItemHeavyShield();
	public static Item STEEL_HELMET = new IblisItemArmor(IblisMod.armorMaterialSteel, 0, EntityEquipmentSlot.HEAD);
	public static Item STEEL_CHESTPLATE = new IblisItemArmor(IblisMod.armorMaterialSteel, 0, EntityEquipmentSlot.CHEST);
	public static Item STEEL_LEGGINS = new IblisItemArmor(IblisMod.armorMaterialSteel, 0, EntityEquipmentSlot.LEGS);
	public static Item STEEL_BOOTS = new IblisItemArmor(IblisMod.armorMaterialSteel, 0, EntityEquipmentSlot.FEET);
	public static Item PARA_ARAMID_FABRIC = new Item();
	public static Item EPOXY_GLUE = new Item();
	public static Item PARA_ARAMID_HELMET = new IblisItemArmor(IblisMod.armorMaterialParaAramid, 0, EntityEquipmentSlot.HEAD);
	public static Item PARA_ARAMID_CHESTPLATE = new IblisItemArmor(IblisMod.armorMaterialParaAramid, 0, EntityEquipmentSlot.CHEST);
	public static Item PARA_ARAMID_LEGGINS = new IblisItemArmor(IblisMod.armorMaterialParaAramid, 0, EntityEquipmentSlot.LEGS);
	public static Item PARA_ARAMID_BOOTS = new IblisItemArmor(IblisMod.armorMaterialParaAramid, 0, EntityEquipmentSlot.FEET);
	
	
	public static void init(){
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
		SHOTGUN_RELOADING
			.setUnlocalizedName("six_barrels_shotgun")
			.setRegistryName(IblisMod.MODID, "six_barrels_shotgun_reloading")
			.setHasSubtypes(true)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		SHOTGUN_BULLET.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("shotgun_bullet")
			.setRegistryName(IblisMod.MODID, "shotgun_bullet")
			.setHasSubtypes(true)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		CROSSBOW.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("double_crossbow")
			.setRegistryName(IblisMod.MODID, "double_crossbow")
			.setHasSubtypes(false)
			.setMaxDamage(1561)
			.setMaxStackSize(1);
		CROSSBOW_RELOADING
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
		PARA_ARAMID_FABRIC
			.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("para_aramid_fabric")
			.setRegistryName(IblisMod.MODID, "para_aramid_fabric")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		EPOXY_GLUE
			.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("epoxy_glue")
			.setRegistryName(IblisMod.MODID, "epoxy_glue")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		NUGGET_STEEL.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("nugget_steel")
			.setRegistryName(IblisMod.MODID, "nugget_steel")
			.setHasSubtypes(false)
			.setMaxDamage(0)
			.setMaxStackSize(64);
		TRIGGER_SPRING.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("trigger_spring")
			.setRegistryName(IblisMod.MODID, "trigger_spring")
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
		SUBSTANCE_CONTAINER.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("flask")
			.setRegistryName(IblisMod.MODID, "flask")
			.setHasSubtypes(true)
			.setMaxDamage(0)
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
		STEEL_HELMET.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("steel_helmet")
			.setRegistryName(IblisMod.MODID, "steel_helmet");
		STEEL_CHESTPLATE.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("steel_chestplate")
			.setRegistryName(IblisMod.MODID, "steel_chestplate");
		STEEL_LEGGINS.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("steel_leggins")
			.setRegistryName(IblisMod.MODID, "steel_leggins");
		STEEL_BOOTS.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("steel_boots")
			.setRegistryName(IblisMod.MODID, "steel_boots");
		PARA_ARAMID_HELMET.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("para_aramid_helmet")
			.setRegistryName(IblisMod.MODID, "para_aramid_helmet");
		PARA_ARAMID_CHESTPLATE.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("para_aramid_chestplate")
			.setRegistryName(IblisMod.MODID, "para_aramid_chestplate");
		PARA_ARAMID_LEGGINS.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("para_aramid_leggins")
			.setRegistryName(IblisMod.MODID, "para_aramid_leggins");
		PARA_ARAMID_BOOTS.setCreativeTab(IblisMod.creativeTab)
			.setUnlocalizedName("para_aramid_boots")
			.setRegistryName(IblisMod.MODID, "para_aramid_boots");
		
		registerItem(GUIDE);
		registerItem(SHOTGUN);
		registerItem(SHOTGUN_RELOADING);
		registerItem(SHOTGUN_BULLET);
		registerItem(CROSSBOW);
		registerItem(CROSSBOW_RELOADING);
		registerItem(CROSSBOW_BOLT);
		registerItem(INGOT_STEEL);
		registerItem(NUGGET_STEEL);
		registerItem(TRIGGER_SPRING);
		registerItem(NONSTERILE_MEDKIT);
		registerItem(MEDKIT);
		registerItem(SUBSTANCE_CONTAINER);
		registerItem(BOULDER);
		registerItem(IRON_THROWING_KNIFE);
		registerItem(HEAVY_SHIELD);
		registerItem(STEEL_HELMET);
		registerItem(STEEL_CHESTPLATE);
		registerItem(STEEL_LEGGINS);
		registerItem(STEEL_BOOTS);
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
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SHOTGUN_BULLET, 1,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"shotgun_shot"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INGOT_STEEL, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"ingot_steel"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NUGGET_STEEL, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"nugget_steel"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TRIGGER_SPRING, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"trigger_spring"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NONSTERILE_MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"non-sterile_medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SUBSTANCE_CONTAINER, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"pile"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SUBSTANCE_CONTAINER, 1,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"flask"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SUBSTANCE_CONTAINER, 2,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"reactor"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(BOULDER, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"boulder"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(IRON_THROWING_KNIFE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"iron_throwing_knife"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HEAVY_SHIELD, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"heavy_shield"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_HELMET, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_helmet"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_CHESTPLATE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_chestplate"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_LEGGINS, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_leggins"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_BOOTS, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_boots"), "inventory"));
	}
}
