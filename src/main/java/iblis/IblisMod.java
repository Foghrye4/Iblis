package iblis;

import org.apache.logging.log4j.Logger;

import iblis.event.IblisEventHandler;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = IblisMod.MODID, version = IblisMod.VERSION, dependencies = IblisMod.DEPENDENCIES)
public class IblisMod
{
    public static final String MODID = "iblis";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "after:landcore;after:hardcorearmor;after:tconstruct;after:silentgems";
    
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
	 
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	eventHandler = new IblisEventHandler();
    	log = event.getModLog();
    	proxy.load();
    	network.load();
    	
    	MinecraftForge.EVENT_BUS.register(eventHandler);
    	MinecraftForge.EVENT_BUS.register(proxy);
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
