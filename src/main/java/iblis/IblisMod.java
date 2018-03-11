package iblis;

import org.apache.logging.log4j.Logger;

import iblis.command.CommandGetAttribute;
import iblis.command.CommandSetAttribute;
import iblis.command.CommandShowNBT;
import iblis.crafting.CraftingHandler;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityCrossbowBolt;
import iblis.entity.EntityPlayerZombie;
import iblis.entity.EntityThrowingKnife;
import iblis.init.IblisItems;
import iblis.init.IblisPotions;
import iblis.init.IblisSounds;
import iblis.init.RegistryEventHandler;
import iblis.item.IblisCreativeTab;
import iblis.loot.LootTableParsingEventHandler;
import iblis.player.IblisEventHandler;
import iblis.tconstruct_integration.TConstructCraftingEventHandler;
import iblis.villager.EmeraldForOreDictionaryItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod(modid = IblisMod.MODID, version = IblisMod.VERSION, guiFactory = IblisMod.GUI_FACTORY, dependencies = IblisMod.DEPENDENCIES)
public class IblisMod
{
    public static final String MODID = "iblis";
    public static final String VERSION = "0.3.31";
    public static final String GUI_FACTORY = "iblis.gui.IblisGuiFactory";
    public static final String DEPENDENCIES = "after:landcore;after:hardcorearmor;after:tconstruct;after:silentgems";
    
	@SidedProxy(clientSide = "iblis.ClientProxy", serverSide = "iblis.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "iblis.ClientNetworkHandler", serverSide = "iblis.ServerNetworkHandler")
	public static ServerNetworkHandler network;
	public static Logger log;
	public static IblisCreativeTab creativeTab;
    public static IblisModConfig config;
    public static IblisEventHandler eventHandler;
	public static ArmorMaterial armorMaterialSteel;
	public static ArmorMaterial armorMaterialParaAramid;
    
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	// Oh, so original and fresh! ^_^
    	armorMaterialSteel = EnumHelper.addArmorMaterial("STEEL", "iblis:steel", 33, new int[]{6, 12, 16, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 4);
    	armorMaterialSteel.setRepairItem(new ItemStack(IblisItems.INGOT_STEEL));
    	armorMaterialParaAramid = EnumHelper.addArmorMaterial("PARA_ARAMID", "iblis:para_aramid", 33, new int[]{6, 12, 16, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8);
    	armorMaterialParaAramid.setRepairItem(new ItemStack(IblisItems.PARA_ARAMID_FABRIC));
    	eventHandler = new IblisEventHandler();
    	log = event.getModLog();
    	creativeTab = new IblisCreativeTab("iblis.tab");
    	IblisItems.init();
    	IblisPotions.init();
    	IblisSounds.register();
		config = new IblisModConfig(new Configuration(event.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(config);
    	RangedAttribute toughness = (RangedAttribute) SharedMonsterAttributes.ARMOR_TOUGHNESS;
    	RangedAttribute armor = (RangedAttribute) SharedMonsterAttributes.ARMOR;
		// Max armor and armor toughness upper limit is removed
    	toughness.maximumValue = Double.MAX_VALUE;
    	armor.maximumValue = Double.MAX_VALUE;
    	Items.SHIELD.setMaxDamage(200);
    	EntityRegistry.registerModEntity(new ResourceLocation(MODID, "zombie"), EntityPlayerZombie.class, "zombie", 0, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "boulder"), EntityBoulder.class, "Boulder", 1, this, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "throwing_knife"), EntityThrowingKnife.class, "ThrowingKnife", 2, this, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "crossbow_bolt"), EntityCrossbowBolt.class, "CrossbowBolt", 3, this, 64, 1, true);
		VillagerProfession mechanic = new VillagerProfession(MODID+":mechanic", MODID + ":textures/entity/villager/mechanic.png",
				MODID + ":textures/entity/villager/zombie_mechanic.png");
		VillagerCareer mechanicCareer = new VillagerCareer(mechanic, "mechanic");
		ITradeList trade1 = new ListItemForEmeralds(IblisItems.SHOTGUN_BULLET,
				new PriceInfo(-24, -8));
		ITradeList trade2 = new EmeraldForOreDictionaryItems("ingotSteel", new PriceInfo(4, 8));
		mechanicCareer.addTrade(1, trade1, trade2);
		RegistryEventHandler.professions.add(mechanic);
    	proxy.load();
    	network.load();
    	
    	MinecraftForge.EVENT_BUS.register(new CraftingHandler());
    	MinecraftForge.EVENT_BUS.register(eventHandler);
    	MinecraftForge.EVENT_BUS.register(proxy);
    	MinecraftForge.EVENT_BUS.register(new LootTableParsingEventHandler());
    	MinecraftForge.EVENT_BUS.register(new RegistryEventHandler());
    	
    	if(Loader.isModLoaded("tconstruct")){
        	MinecraftForge.EVENT_BUS.register(new TConstructCraftingEventHandler());
    	}
    }
    
	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		proxy.init();
	}
    
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		network.setServer(event.getServer());
		event.registerServerCommand(new CommandSetAttribute());
		event.registerServerCommand(new CommandGetAttribute());
		event.registerServerCommand(new CommandShowNBT());
	}
}
