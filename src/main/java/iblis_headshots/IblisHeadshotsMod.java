package iblis_headshots;

import org.apache.logging.log4j.Logger;

import iblis_headshots.event.IblisEventHandler;
import iblis_headshots.init.IblisHeadshotsAdvancements;
import iblis_headshots.item.HelmetsConfig;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = IblisHeadshotsMod.MODID, version = IblisHeadshotsMod.VERSION, guiFactory = IblisHeadshotsMod.GUI_FACTORY, dependencies = IblisHeadshotsMod.DEPENDENCIES)
public class IblisHeadshotsMod
{
    public static final String MODID = "iblis_headshots";
    public static final String VERSION = "1.1.2";
    public static final String DEPENDENCIES = "after:landcore;after:hardcorearmor;after:tconstruct;after:silentgems";
    public static final String GUI_FACTORY = "iblis.client.gui.IblisGuiFactory";
    
	@SidedProxy(clientSide = "iblis.ClientProxy", serverSide = "iblis.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "iblis.ClientNetworkHandler", serverSide = "iblis.ServerNetworkHandler")
	public static ServerNetworkHandler network;
	public static Logger log;
    public static IblisEventHandler eventHandler;
	public static ArmorMaterial armorMaterialSteel;
	public static ArmorMaterial armorMaterialParaAramid;
	public static boolean isRPGHUDLoaded = false;
	public static boolean isAppleCoreLoaded = false;
	public static boolean isAppleskinLoaded = false;
    public static IblisHeadshotsModConfig config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	eventHandler = new IblisEventHandler();
    	log = event.getModLog();
    	proxy.load();
    	network.load();
		config = new IblisHeadshotsModConfig(new Configuration(event.getSuggestedConfigurationFile()));
		IblisHeadshotsAdvancements.register();
		MinecraftForge.EVENT_BUS.register(config);
    	MinecraftForge.EVENT_BUS.register(eventHandler);
    	MinecraftForge.EVENT_BUS.register(proxy);
    	HelmetsConfig.load();
    }
    
	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		proxy.init();
		isRPGHUDLoaded  = Loader.isModLoaded("rpghud");
	}
    
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		network.setServer(event.getServer());
	}
}
