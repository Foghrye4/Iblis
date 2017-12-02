package iblis.init;

import java.util.ArrayList;
import java.util.List;

import iblis.IblisMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

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
