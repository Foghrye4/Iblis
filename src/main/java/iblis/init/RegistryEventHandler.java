package iblis.init;

import java.util.ArrayList;
import java.util.List;

import iblis.IblisMod;
import iblis.item.ItemBlockEdible;
import iblis.item.ItemIngot;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;

public class RegistryEventHandler {
	
	public static final List<Block> blocks = new ArrayList<Block>();
	public static final List<Item> items = new ArrayList<Item>();
	public static final List<SoundEvent> sounds = new ArrayList<SoundEvent>();
	public static final List<VillagerProfession> professions = new ArrayList<VillagerProfession>();
	public static final List<Potion> potions = new ArrayList<Potion>();
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		for(Block block:blocks)
			event.getRegistry().register(block);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for(Item item:items)
			event.getRegistry().register(item);
		IblisMod.proxy.registerRenders();
		OreDictionary.registerOre("ingotSteel", new ItemStack(IblisItems.INGOT, 1, ItemIngot.STEEL));
		OreDictionary.registerOre("ingotBronze", new ItemStack(IblisItems.INGOT, 1, ItemIngot.BRONZE));
		OreDictionary.registerOre("nuggetSteel", IblisItems.NUGGET_STEEL);
	}
	
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for(SoundEvent sound:sounds)
			event.getRegistry().register(sound);
	}
	
	@SubscribeEvent
	public void registerVillagerProfession(RegistryEvent.Register<VillagerProfession> event) {
		for(VillagerProfession profession:professions)
			event.getRegistry().register(profession);
	}
	
	@SubscribeEvent
	public void registerPotion(RegistryEvent.Register<Potion> event) {
		for(Potion potion:potions)
			event.getRegistry().register(potion);
	}
}
