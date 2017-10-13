package iblis.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class RegistryEventHandler {
	
	public static final List<Block> blocks = new ArrayList<Block>();
	public static final List<Item> items = new ArrayList<Item>();
	public static final List<SoundEvent> sounds = new ArrayList<SoundEvent>();
	public static final List<VillagerProfession> professions = new ArrayList<VillagerProfession>();
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		for(Block block:blocks)
			event.getRegistry().register(block);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for(Item item:items)
			event.getRegistry().register(item);
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
}
