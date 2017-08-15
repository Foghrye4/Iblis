package iblis;

import org.apache.logging.log4j.Logger;

import iblis.command.CommandSetAttribute;
import iblis.crafting.CraftingHandler;
import iblis.crafting.PlayerSensitiveRecipeWrapper;
import iblis.crafting.PlayerSensitiveShapedRecipe;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.item.IblisCreativeTab;
import iblis.loot.LootTableParsingEventHandler;
import iblis.player.EntityLivingEventHandler;
import iblis.player.EntityPlayerZombie;
import iblis.player.PlayerDeathEventHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid = IblisMod.MODID, version = IblisMod.VERSION)
public class IblisMod
{
	@SidedProxy(clientSide = "iblis.ClientProxy", serverSide = "iblis.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "iblis.ClientNetworkHandler", serverSide = "iblis.ServerNetworkHandler")
	public static ServerNetworkHandler network;
	public static Logger log;
	public static CreativeTabs creativeTab;
	
    public static final String MODID = "iblis";
    public static final String VERSION = "0.2.3";
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	log = event.getModLog();
    	creativeTab = new IblisCreativeTab("iblis.tab");
    	IblisItems.init();
    	IblisSounds.register();
    	RecipeSorter.register(MODID+":shaped_player_sensitive_wrapper", PlayerSensitiveRecipeWrapper.class, Category.SHAPED, "after:minecraft:shaped");
    	RecipeSorter.register(MODID+":shaped_player_sensitive", PlayerSensitiveShapedRecipe.class, Category.SHAPED, "after:"+MODID+":shaped_player_sensitive_wrapper");
    	CraftingHandler craftingHandler = new CraftingHandler();
    	craftingHandler.replaceRecipes();
    	craftingHandler.addRecipes();
    	RangedAttribute toughness = (RangedAttribute) SharedMonsterAttributes.ARMOR_TOUGHNESS;
    	/**Max armor toughness upper limit is removed**/
    	toughness.maximumValue = Double.MAX_VALUE;
    	EntityRegistry.registerModEntity(new ResourceLocation(MODID, "zombie"), EntityPlayerZombie.class, "zombie", 0, this, 80, 3, true);
    	proxy.load();
    	network.load();
    	MinecraftForge.EVENT_BUS.register(craftingHandler);
    	MinecraftForge.EVENT_BUS.register(new PlayerDeathEventHandler());
    	MinecraftForge.EVENT_BUS.register(new EntityLivingEventHandler());
    	MinecraftForge.EVENT_BUS.register(new LootTableParsingEventHandler());
    }
    
	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		proxy.init();
	}
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	network.setServer(event.getServer());
   		event.registerServerCommand(new CommandSetAttribute());
    }
    
}
