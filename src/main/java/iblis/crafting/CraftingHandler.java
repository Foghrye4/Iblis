package iblis.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.init.IblisItems;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class CraftingHandler  implements IContainerListener{
	
	List<PlayerSensitiveShapedRecipeWrapper> replacements = new ArrayList<PlayerSensitiveShapedRecipeWrapper>();
	List<ShapedRecipeRaisingSkillWrapper> replacements2 = new ArrayList<ShapedRecipeRaisingSkillWrapper>();
	
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistryModifiable<IRecipe> recipeRegistry = (IForgeRegistryModifiable<IRecipe>) ForgeRegistries.RECIPES;
		Iterator<IRecipe> irecipes = recipeRegistry.iterator();
		List<ResourceLocation> vanillaRecipesToRemove = new ArrayList<ResourceLocation>();
		while (irecipes.hasNext()) {
			IRecipe recipe = irecipes.next();
			ItemStack is = recipe.getRecipeOutput();
			if (is != null) {
				if (isArmor(is)) {
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.ARMORSMITH);
				} else if (isWeapon(is)) {
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.WEAPONSMITH);
				} else if (is.getItem() instanceof ItemBow) {
					if (!is.hasTagCompound())
						is.setTagCompound(new NBTTagCompound());
					NBTTagList attributeModifiersNBTList = new NBTTagList();
					NBTTagCompound modifierNBT = SharedMonsterAttributes.writeAttributeModifierToNBT(
							new AttributeModifier(SharedIblisAttributes.ARROW_DAMAGE_MODIFIER, "Arrow damage", 2d, 0));
					modifierNBT.setString("Slot", EntityEquipmentSlot.MAINHAND.getName());
					modifierNBT.setString("AttributeName", SharedIblisAttributes.PROJECTILE_DAMAGE.getName());
					attributeModifiersNBTList.appendTag(modifierNBT);
					is.getTagCompound().setTag("AttributeModifiers", attributeModifiersNBTList);
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.MECHANICS);
				} else if (is.getItem() instanceof ItemShield) {
					ShapedRecipeRaisingSkillWrapper shieldsWrapper = new ShapedRecipeRaisingSkillWrapper(recipe);
					shieldsWrapper.setSesitiveTo(PlayerSkills.ARMORSMITH, 2);
					shieldsWrapper.setRegistryName(recipe.getRegistryName());
					vanillaRecipesToRemove.add(recipe.getRegistryName());
					replacements2.add(shieldsWrapper);
				} else if (isMechanism(is)) {
					ShapedRecipeRaisingSkillWrapper mechanismWrapper = new ShapedRecipeRaisingSkillWrapper(recipe);
					mechanismWrapper.setSesitiveTo(PlayerSkills.MECHANICS, 1);
					mechanismWrapper.setRegistryName(recipe.getRegistryName());
					vanillaRecipesToRemove.add(recipe.getRegistryName());
					replacements2.add(mechanismWrapper);
					
				}
			}
		}
		for (ResourceLocation key : vanillaRecipesToRemove)
			recipeRegistry.remove(key);
		this.addRecipes(event);
	}
	
	private boolean isMechanism(ItemStack is) {
		Item item = is.getItem();
		return item == Item.getItemFromBlock(Blocks.PISTON) 
				|| item == Items.CLOCK
				|| item == Item.getItemFromBlock(Blocks.NOTEBLOCK)
				|| item == Item.getItemFromBlock(Blocks.DISPENSER)
				|| item == Item.getItemFromBlock(Blocks.JUKEBOX);
	}

	private void wrapRecipe(ItemStack is, List<ResourceLocation> vanillaRecipesToRemove, IRecipe recipe, PlayerSkills sensitiveSkill){
		PlayerSensitiveShapedRecipeWrapper recipeReplacement = new PlayerSensitiveShapedRecipeWrapper(recipe);
		double requiredskill = getWeaponCraftingRequiredSkill(is) + getArmorCraftingRequiredSkill(is);
		double skillXP = requiredskill + 1.0d;
		if(is.getItem() instanceof ItemTool){
			ItemTool isit = (ItemTool) is.getItem();
			String material = isit.getToolMaterialName();
			if(material.equalsIgnoreCase("wood"))
				skillXP = 1.0d; // Nerf tools XP for easily obtained materials
			if(material.equalsIgnoreCase("stone"))
				skillXP = 1.0d;
		}
		recipeReplacement.setSesitiveTo(sensitiveSkill, requiredskill, skillXP);
		recipeReplacement.setRegistryName(recipe.getRegistryName());
		vanillaRecipesToRemove.add(recipe.getRegistryName());
		replacements.add(recipeReplacement);

	}

	private void addRecipes(RegistryEvent.Register<IRecipe> event) {
		NonNullList<Ingredient> guideRecipeIngridients = NonNullList.from(
				Ingredient.EMPTY, 
				Ingredient.fromStacks(new ItemStack(Items.WRITABLE_BOOK,1,0)), 
				Ingredient.fromStacks(new ItemStack(Items.PAPER,1,0)),
				Ingredient.EMPTY,
				Ingredient.EMPTY);
		
		NonNullList<Ingredient> guideRecipeIngridients2 = NonNullList.from(
				Ingredient.EMPTY, 
				Ingredient.fromStacks(new ItemStack(IblisItems.GUIDE,1,0)), 
				Ingredient.fromStacks(new ItemStack(Items.PAPER,1,0)),
				Ingredient.EMPTY, 
				Ingredient.EMPTY
				);
		ItemStack[] waterBottleArray = new ItemStack[] {
				PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER) };
		Ingredient water_bottle = new Ingredient(waterBottleArray) {
			@Override
			public boolean apply(@Nullable ItemStack stack) {
				if (stack.getItem() != Items.POTIONITEM)
					return false;
				return PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER;
			}
		};
	    
		NonNullList<Ingredient> medkitRecipeIngridients = NonNullList.from(
				Ingredient.EMPTY, 
				Ingredient.fromStacks(new ItemStack(Items.STRING,1,0)),
				Ingredient.EMPTY, 
				Ingredient.EMPTY,
				Ingredient.fromStacks(new ItemStack(Items.STRING,1,0)), 
				Ingredient.fromStacks(new ItemStack(Items.LEATHER,1,0)), 
				water_bottle,
				Ingredient.fromStacks(new ItemStack(Items.STRING,1,0)), 
				Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT,1,0)), 
				Ingredient.EMPTY
				);
		
		NonNullList<Ingredient> boulderRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				Ingredient.fromStacks(new ItemStack(Blocks.COBBLESTONE, 1, 0)));
		NonNullList<Ingredient> cobblestoneRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)),
				Ingredient.fromStacks(new ItemStack(IblisItems.BOULDER, 1, 0)));
		
		Ingredient ironIngot = Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT));
		Ingredient planks = new OreIngredient("plankWood");
		Ingredient leather = Ingredient.fromItem(Items.LEATHER);
		ItemStack spring = new ItemStack(IblisItems.TRIGGER_SPRING);
		
		NonNullList<Ingredient> crossbowRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				Ingredient.fromStacks(new ItemStack(Items.STICK, 1, 0)),
				Ingredient.fromStacks(new ItemStack(Items.STRING, 1, 0)),
				Ingredient.EMPTY,
				ironIngot,
				planks,
				planks,
				Ingredient.fromStacks(new ItemStack(Items.STICK, 1, 0)),
				Ingredient.fromStacks(new ItemStack(Items.STRING, 1, 0)),
				Ingredient.fromStacks(spring));
		
		NonNullList<Ingredient> crossbowBoltRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(Items.IRON_NUGGET, 1, 0)), Ingredient.EMPTY,
				Ingredient.EMPTY, planks, Ingredient.EMPTY,
				Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(Items.STICK, 1, 0)),Ingredient.EMPTY);

		NonNullList<Ingredient> ironThrowingKnifeRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
			Ingredient.EMPTY,ironIngot,Ingredient.EMPTY,
			Ingredient.EMPTY,Ingredient.fromStacks(new ItemStack(Items.STICK, 1, 0)),Ingredient.EMPTY,
			Ingredient.EMPTY,Ingredient.EMPTY,Ingredient.EMPTY);
		
		NonNullList<Ingredient> shieldRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				planks, Ingredient.EMPTY, planks,
				leather, ironIngot, leather,
				Ingredient.EMPTY, planks, Ingredient.EMPTY);
		
		PlayerSensitiveShapedRecipe recipe1 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients, new ItemStack(IblisItems.GUIDE,1,0));
		PlayerSensitiveShapedRecipe recipe2 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients2, new ItemStack(IblisItems.GUIDE,1,0));
		ShapedRecipes medkitRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, medkitRecipeIngridients, new ItemStack(IblisItems.NONSTERILE_MEDKIT));
		ShapedRecipes ironThrowingKnifeRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, ironThrowingKnifeRecipeIngridients, new ItemStack(IblisItems.IRON_THROWING_KNIFE,8,0));
		ShapedRecipeRaisingSkillWrapper ironThrowingKnifeWrappedRecipe = new ShapedRecipeRaisingSkillWrapper(ironThrowingKnifeRecipe);
		ShapelessRecipes boulderRecipe = new ShapelessRecipes(IblisMod.MODID+":shapeless", new ItemStack(IblisItems.BOULDER, 9, 0), boulderRecipeIngridients);
		ShapelessRecipes cobblestoneRecipe = new ShapelessRecipes(IblisMod.MODID+":shapeless", new ItemStack(Blocks.COBBLESTONE, 1, 0), cobblestoneRecipeIngridients);
		ItemStack shield = new ItemStack(IblisItems.HEAVY_SHIELD);
		shield.setTagCompound(new NBTTagCompound());
		shield.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, 600);
		ShapedRecipes shieldRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, shieldRecipeIngridients, shield);
		PlayerSensitiveShapedRecipeWrapper shieldRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(shieldRecipe);

		ItemStack crossbow = new ItemStack(IblisItems.CROSSBOW);
		crossbow.setTagCompound(new NBTTagCompound());
		crossbow.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, 600);

		ShapedRecipes crossbowRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, crossbowRecipeIngridients, crossbow);
		PlayerSensitiveShapedRecipeWrapper crossbowRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(crossbowRecipe);
		
		ShapedRecipes crossbowBoltRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, crossbowBoltRecipeIngridients, new ItemStack(IblisItems.CROSSBOW_BOLT,8,0));
		ShapedRecipeRaisingSkillWrapper crossbowBoltWrappedRecipe = new ShapedRecipeRaisingSkillWrapper(crossbowBoltRecipe);
		
		ShapedOreRecipe triggerSpringRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),spring, 
				"N  ",
				" N ",
				"   ", 'N', "nuggetSteel");

		recipe1.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_1"));
		recipe2.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_2"));
		medkitRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"medkit"));
		boulderRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"boulder"));
		cobblestoneRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"cobblestone_from_boulders"));
		ironThrowingKnifeWrappedRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"iron_throwing_knife"));
		ironThrowingKnifeWrappedRecipe.setSesitiveTo(PlayerSkills.WEAPONSMITH, 5);
		shieldRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"heavy_shield"));
		shieldRecipeWrapper.setSesitiveTo(PlayerSkills.ARMORSMITH, 4, 4);
		crossbowRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"double_crossbow"));
		crossbowRecipeWrapper.setSesitiveTo(PlayerSkills.MECHANICS, 4, 4);
		crossbowBoltWrappedRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"crossbow_bolt"));
		crossbowBoltWrappedRecipe.setSesitiveTo(PlayerSkills.WEAPONSMITH, 2);
		triggerSpringRecipe.setRegistryName(IblisMod.MODID, "trigger_spring");
		
		event.getRegistry().register(recipe1);
		event.getRegistry().register(recipe2);
		event.getRegistry().register(medkitRecipe);
		event.getRegistry().register(boulderRecipe);
		event.getRegistry().register(cobblestoneRecipe);
		event.getRegistry().register(ironThrowingKnifeWrappedRecipe);
		event.getRegistry().register(shieldRecipeWrapper);
		event.getRegistry().register(crossbowRecipeWrapper);
		event.getRegistry().register(crossbowBoltWrappedRecipe);
		event.getRegistry().register(triggerSpringRecipe);
		this.addShapelessNuggetsOrShradsRecipe("ingotSteel", "nuggetSteel", "steel_ingot_from_nuggets", "nugget_steel", event.getRegistry());

		ItemStack shotgun = new ItemStack(IblisItems.SHOTGUN);
		shotgun.setTagCompound(new NBTTagCompound());
		shotgun.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, 600);
		ShapedOreRecipe shotgunRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),shotgun, 
				"  W",
				" ST",
				"S  ", 'W', "plankWood", 'S', "ingotSteel", 'T', spring);
		PlayerSensitiveShapedRecipeWrapper shotgunRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(shotgunRecipe);
		shotgunRecipeWrapper.setSesitiveTo(PlayerSkills.MECHANICS, 12, 20);
		shotgunRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"shotgun_recipe"));
		replacements.add(shotgunRecipeWrapper);
		for(PlayerSensitiveShapedRecipeWrapper recipeReplacement: replacements)
			event.getRegistry().register(recipeReplacement);
		for(ShapedRecipeRaisingSkillWrapper recipeReplacement: replacements2)
			event.getRegistry().register(recipeReplacement);
		FurnaceRecipes.instance().addSmelting(IblisItems.NONSTERILE_MEDKIT, new ItemStack(IblisItems.MEDKIT), 1.0f);
	}
	
	private void addShapelessNuggetsOrShradsRecipe(String ingot, String shard, String accembleName, String breakName,IForgeRegistry<IRecipe> registry){
		NonNullList<Ingredient> nuggetRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				new OreIngredient(ingot));
		NonNullList<Ingredient> ingotRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard),
				new OreIngredient(shard));
		ItemStack shards = OreDictionary.getOres(shard).get(0);
		shards.setCount(9);
		ShapelessRecipes nuggetRecipe = new ShapelessRecipes(IblisMod.MODID+":shapeless", shards, nuggetRecipeIngridients);
		ShapelessRecipes ingotRecipe = new ShapelessRecipes(IblisMod.MODID+":shapeless", OreDictionary.getOres(ingot).get(0), ingotRecipeIngridients);
		nuggetRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,breakName));
		ingotRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,accembleName));
		registry.register(nuggetRecipe);
		registry.register(ingotRecipe);
	}
	
	private final List<ContainerRepair> openedContainers = new ArrayList<ContainerRepair>();
	private boolean skipNextUpdate = false;
	
	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent event) {
		// Hackish way to retrieve output after event.
		if(skipNextUpdate)
			return;
		// First find container responsible for event
		ContainerRepair container = null;
		assert !openedContainers.isEmpty();
		for(ContainerRepair containerIn: openedContainers)
			if (containerIn.inventorySlots.get(0).getStack() == event.getLeft()) {
				event.setCanceled(true);
				skipNextUpdate = true;
				containerIn.updateRepairOutput();
				container = containerIn;
				break;
			}
		if(container == null)
			return;
		// Second - find player
		EntityPlayer player = null; 
		for (IContainerListener listener : container.listeners) {
			if (listener instanceof EntityPlayerMP) {
				player = (EntityPlayer) listener;
				break;
			}
		}
		// Third - find a recipe output
		ItemStack repairableStack = container.getSlot(2).getStack();
		for (PlayerSensitiveShapedRecipeWrapper recipeReplacement : replacements) {
			if (OreDictionary.itemMatches(repairableStack, recipeReplacement.getRecipeOutput(), false)) {
				double skillValue = recipeReplacement.sensitiveSkill.getFullSkillValue(player);
				recipeReplacement.getCraftingResult(repairableStack, skillValue, true);
				break;
			}
		}
		container.detectAndSendChanges();
		skipNextUpdate = false;		
	}
	
	@SubscribeEvent
	public void onPlayerOpenContainerEvent(PlayerContainerEvent.Open event) {
		if(event.getContainer() instanceof ContainerRepair)
			openedContainers.add((ContainerRepair) event.getContainer());
		if(event.getContainer() instanceof ContainerWorkbench) {
			event.getContainer().listeners.add(this);
		}
	}

	@SubscribeEvent
	public void onPlayerOpenContainerEvent(PlayerContainerEvent.Close event) {
		if(event.getContainer() instanceof ContainerRepair)
			openedContainers.remove(event.getContainer());
	}

	private static boolean isArmor(ItemStack is) {
		if(is.getMaxStackSize()!=1)
			return false;
		String armorkey = SharedMonsterAttributes.ARMOR.getName();
		if(is.getAttributeModifiers(EntityEquipmentSlot.CHEST).keySet().contains(armorkey))
			return true;
		if(is.getAttributeModifiers(EntityEquipmentSlot.FEET).keySet().contains(armorkey))
			return true;
		if(is.getAttributeModifiers(EntityEquipmentSlot.HEAD).keySet().contains(armorkey))
			return true;
		if(is.getAttributeModifiers(EntityEquipmentSlot.LEGS).keySet().contains(armorkey))
			return true;
		return false;
	}
	
	private static double getArmorCraftingRequiredSkill(ItemStack is) {
		double minimalSkill = 0;
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.CHEST).get(SharedMonsterAttributes.ARMOR.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.FEET).get(SharedMonsterAttributes.ARMOR.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.HEAD).get(SharedMonsterAttributes.ARMOR.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.LEGS).get(SharedMonsterAttributes.ARMOR.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.CHEST).get(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.FEET).get(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.HEAD).get(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()))
			minimalSkill+=am.getAmount();
		for(AttributeModifier am :is.getAttributeModifiers(EntityEquipmentSlot.LEGS).get(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()))
			minimalSkill+=am.getAmount();
		return minimalSkill;
	}
	
	private static boolean isWeapon(ItemStack is) {
		Set<String> am = is.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).keySet();
		return am.contains(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	}
	
	private static double getWeaponCraftingRequiredSkill(ItemStack is) {
		double minimalSkill = 0;
		if(is.getItem() instanceof ItemBow) {
			minimalSkill = 1;
		}
		Collection<AttributeModifier> am = is.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		Iterator<AttributeModifier> ami = am.iterator();
		while(ami.hasNext()) {
			minimalSkill+=ami.next().getAmount();
		}
		return minimalSkill;
	}
	
	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
		if (!(containerToSend instanceof ContainerWorkbench))
			return;
		EntityPlayerMP player = (EntityPlayerMP) IblisMod.proxy.getPlayer(((ContainerWorkbench)containerToSend).craftMatrix);
		if(player!=null)
			IblisMod.network.sendRefreshTrainCraftButton(player);
	}

	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {}
	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {}
	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {}

}
