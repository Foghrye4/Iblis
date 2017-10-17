package iblis;

import org.apache.logging.log4j.Logger;

import iblis.command.CommandGetAttribute;
import iblis.command.CommandSetAttribute;
import iblis.command.CommandShowNBT;
import iblis.crafting.CraftingHandler;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityPlayerZombie;
import iblis.entity.EntityThrowingKnife;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.init.RegistryEventHandler;
import iblis.item.IblisCreativeTab;
import iblis.loot.LootTableParsingEventHandler;
import iblis.player.IblisEventHandler;
import iblis.villager.EmeraldForOreDictionaryItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = IblisMod.MODID, version = IblisMod.VERSION, guiFactory = IblisMod.GUI_FACTORY, dependencies = IblisMod.DEPENDENCIES)
public class IblisMod
{
    public static final String MODID = "iblis";
    public static final String VERSION = "0.3.21";
    public static final String GUI_FACTORY = "iblis.gui.IblisGuiFactory";
    public static final String DEPENDENCIES = "after:landcore;after:tconstruct";
    
	@SidedProxy(clientSide = "iblis.ClientProxy", serverSide = "iblis.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "iblis.ClientNetworkHandler", serverSide = "iblis.ServerNetworkHandler")
	public static ServerNetworkHandler network;
	public static Logger log;
	public static IblisCreativeTab creativeTab;
    public static IblisModConfig config;
    public static IblisEventHandler eventHandler;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	eventHandler = new IblisEventHandler();
		config = new IblisModConfig(new Configuration(event.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(config);
    	log = event.getModLog();
    	creativeTab = new IblisCreativeTab("iblis.tab");
    	IblisItems.init();
    	IblisSounds.register();
    	RangedAttribute toughness = (RangedAttribute) SharedMonsterAttributes.ARMOR_TOUGHNESS;
		// Max armor toughness upper limit is removed
    	toughness.maximumValue = Double.MAX_VALUE;
    	EntityRegistry.registerModEntity(new ResourceLocation(MODID, "zombie"), EntityPlayerZombie.class, "zombie", 0, this, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "boulder"), EntityBoulder.class, "Boulder", 1, this, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "throwing_knife"), EntityThrowingKnife.class, "ThrowingKnife", 2, this, 64, 1, true);
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
    	MinecraftForge.EVENT_BUS.register(new LootTableParsingEventHandler());
    	MinecraftForge.EVENT_BUS.register(new RegistryEventHandler());
    }
    
	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		proxy.init();
	}
    
    @EventHandler
	public void init(FMLInitializationEvent event) {
		OreDictionary.registerOre("ingotSteel", IblisItems.INGOT_STEEL);
		for (int meta = 0; meta < 16; meta++)
			OreDictionary.registerOre("plankWood", new ItemStack(Blocks.PLANKS, 1, meta));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		network.setServer(event.getServer());
		event.registerServerCommand(new CommandSetAttribute());
		event.registerServerCommand(new CommandGetAttribute());
		event.registerServerCommand(new CommandShowNBT());
	}
}
