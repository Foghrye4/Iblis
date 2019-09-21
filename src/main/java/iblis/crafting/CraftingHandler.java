package iblis.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.init.IblisBlocks;
import iblis.init.IblisItems;
import iblis.player.PlayerSkills;
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
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
	
	public static boolean disableMedkitRecipe = false;
	public static boolean disableShotgunRecipes = false;
	static List<PlayerSensitiveShapedRecipeWrapper> replacements = new ArrayList<PlayerSensitiveShapedRecipeWrapper>();
	static List<ShapedRecipeRaisingSkillWrapper> replacements2 = new ArrayList<ShapedRecipeRaisingSkillWrapper>();
	
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistryModifiable<IRecipe> recipeRegistry = (IForgeRegistryModifiable<IRecipe>) ForgeRegistries.RECIPES;
		Iterator<IRecipe> irecipes = recipeRegistry.iterator();
		List<ResourceLocation> vanillaRecipesToRemove = new ArrayList<ResourceLocation>();
		while (irecipes.hasNext()) {
			IRecipe recipe = irecipes.next();
			if(recipe instanceof RecipeRepairItem) {
				vanillaRecipesToRemove.add(recipe.getRegistryName());
			}
			ItemStack is = recipe.getRecipeOutput();
			if (is != null) {
				if (isArmor(is)) {
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.ARMORSMITH);
				} else if (isWeapon(is)) {
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.WEAPONSMITH);
				} else if (is.getItem() instanceof ItemBow) {
					this.wrapRecipe(is, vanillaRecipesToRemove, recipe, PlayerSkills.MECHANICS);
				} else if (is.getItem() instanceof ItemShield) {
					ShapedRecipeRaisingSkillWrapper shieldsWrapper = new ShapedRecipeRaisingSkillWrapper(recipe);
					shieldsWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 2);
					shieldsWrapper.setRegistryName(recipe.getRegistryName());
					vanillaRecipesToRemove.add(recipe.getRegistryName());
					replacements2.add(shieldsWrapper);
				} else if (isMechanism(is)) {
					ShapedRecipeRaisingSkillWrapper mechanismWrapper = new ShapedRecipeRaisingSkillWrapper(recipe);
					mechanismWrapper.setSensitiveTo(PlayerSkills.MECHANICS, 1);
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
				skillXP = 0.2d; // Nerf tools XP for easily obtained materials
			if(material.equalsIgnoreCase("stone"))
				skillXP = 0.2d;
		}
		recipeReplacement.setSensitiveTo(sensitiveSkill, requiredskill, skillXP);
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
		Ingredient ironOre = Ingredient.fromStacks(new ItemStack(Blocks.IRON_ORE));
		Ingredient coal = Ingredient.fromStacks(new ItemStack(Items.COAL,1,0),new ItemStack(Items.COAL,1,1));
		Ingredient planks = new OreIngredient("plankWood");
		Ingredient leather = Ingredient.fromItem(Items.LEATHER);
		ItemStack spring = new ItemStack(IblisItems.TRIGGER_SPRING);
		Ingredient paper = Ingredient.fromStacks(new ItemStack(Items.PAPER));
		Ingredient gunpowder = Ingredient.fromStacks(new ItemStack(Items.GUNPOWDER));
		Ingredient copperAlloys = new MultiOreIngredient("ingotBronze","ingotCopper");
		
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
		
		NonNullList<Ingredient> bulletRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				Ingredient.EMPTY, ironIngot, Ingredient.EMPTY,
				paper, gunpowder, paper,
				copperAlloys, Ingredient.fromItem(Items.REDSTONE), copperAlloys);
		
		NonNullList<Ingredient> shotRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				paper, 			ironIngot, 			paper,
				paper, 			gunpowder, 			paper,
				copperAlloys, Ingredient.fromItem(Items.REDSTONE), copperAlloys);
		
		NonNullList<Ingredient> ironCoalRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				ironIngot, coal, ironIngot,
				coal, ironIngot, coal,
				ironIngot, coal, ironIngot);

		NonNullList<Ingredient> ironOreCoalRecipeIngridients = NonNullList.from(Ingredient.EMPTY,
				ironOre, coal, ironOre,
				coal, ironOre, coal,
				ironOre, coal, ironOre);
		
		ShapedRecipes bulletRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, bulletRecipeIngridients, new ItemStack(IblisItems.SHOTGUN_BULLET,32));
		ShapedRecipes shotRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, shotRecipeIngridients, new ItemStack(IblisItems.SHOTGUN_SHOT,32));
		ShapedRecipes ironThrowingKnifeRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, ironThrowingKnifeRecipeIngridients, new ItemStack(IblisItems.IRON_THROWING_KNIFE,8,0));
		ShapedRecipes crossbowBoltRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, crossbowBoltRecipeIngridients, new ItemStack(IblisItems.CROSSBOW_BOLT,8,0));
		
		PlayerSensitiveMetadataShapedRecipeWrapper bulletWrapped = new PlayerSensitiveMetadataShapedRecipeWrapper(bulletRecipe).setSensitiveTo(PlayerSkills.CHEMISTRY, 0.0, 1.0);
		PlayerSensitiveMetadataShapedRecipeWrapper shotWrapped = new PlayerSensitiveMetadataShapedRecipeWrapper(shotRecipe).setSensitiveTo(PlayerSkills.CHEMISTRY, 0.0, 1.0);
		PlayerSensitiveMetadataShapedRecipeWrapper ironThrowingKnifeWrappedRecipe = new PlayerSensitiveMetadataShapedRecipeWrapper(ironThrowingKnifeRecipe).setSensitiveTo(PlayerSkills.WEAPONSMITH, 0.0, 1.0);
		PlayerSensitiveMetadataShapedRecipeWrapper crossbowBoltWrappedRecipe = new PlayerSensitiveMetadataShapedRecipeWrapper(crossbowBoltRecipe).setSensitiveTo(PlayerSkills.WEAPONSMITH, 0.0, 1.0);
		
		PlayerSensitiveShapedRecipe recipe1 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients, new ItemStack(IblisItems.GUIDE,1,0));
		PlayerSensitiveShapedRecipe recipe2 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients2, new ItemStack(IblisItems.GUIDE,1,0));
		ShapedRecipes medkitRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, medkitRecipeIngridients, new ItemStack(IblisItems.NONSTERILE_MEDKIT));
		
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
		
		
		ShapedOreRecipe triggerSpringRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),spring, 
				"N  ",
				" N ",
				"   ", 'N', "nuggetSteel");
		ItemStack steelHelmet = new ItemStack(IblisItems.STEEL_HELMET);
		steelHelmet.setTagCompound(new NBTTagCompound());
		steelHelmet.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, steelHelmet.getMaxDamage());
		ItemStack steelChestplate = new ItemStack(IblisItems.STEEL_CHESTPLATE);
		steelChestplate.setTagCompound(new NBTTagCompound());
		steelChestplate.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, steelChestplate.getMaxDamage());
		ItemStack steelLeggins = new ItemStack(IblisItems.STEEL_LEGGINS);
		steelLeggins.setTagCompound(new NBTTagCompound());
		steelLeggins.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, steelLeggins.getMaxDamage());
		ItemStack steelBoots = new ItemStack(IblisItems.STEEL_BOOTS);
		steelBoots.setTagCompound(new NBTTagCompound());
		steelBoots.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, steelBoots.getMaxDamage());
		ShapedOreRecipe steelHelmetRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),steelHelmet,
				"III",
				"I I",
				"   ", 'I', "ingotSteel");
		ShapedOreRecipe steelChestplateRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),steelChestplate,
				"I I",
				"III",
				"III", 'I', "ingotSteel");
		ShapedOreRecipe steelLegginsRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),steelLeggins,
				"III",
				"I I",
				"I I", 'I', "ingotSteel");
		ShapedOreRecipe steelBootsRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),steelBoots,
				"   ",
				"I I",
				"I I", 'I', "ingotSteel");
		PlayerSensitiveShapedRecipeWrapper steelHelmetRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(steelHelmetRecipe);
		PlayerSensitiveShapedRecipeWrapper steelChestplateRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(steelChestplateRecipe);
		PlayerSensitiveShapedRecipeWrapper steelLegginsRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(steelLegginsRecipe);
		PlayerSensitiveShapedRecipeWrapper steelBootsRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(steelBootsRecipe);
		ShapedRecipes ironCoalRecipe = new ShapedRecipes(IblisMod.MODID + ":shaped", 3, 3, ironCoalRecipeIngridients,
				new ItemStack(IblisBlocks.IRON_COAL, 1, 0));
		ShapedRecipes ironOreCoalRecipe = new ShapedRecipes(IblisMod.MODID + ":shaped", 3, 3, ironOreCoalRecipeIngridients,
				new ItemStack(IblisBlocks.IRONORE_COAL, 1, 0));

		steelHelmetRecipeWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 12d, 12d);
		steelChestplateRecipeWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 12d, 12d);
		steelLegginsRecipeWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 12d, 12d);
		steelBootsRecipeWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 12d, 12d);
		
		PlayerSensitiveRecipeRepairItem rri = new PlayerSensitiveRecipeRepairItem();

		recipe1.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_1"));
		recipe2.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_2"));
		medkitRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"medkit"));
		boulderRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"boulder"));
		cobblestoneRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"cobblestone_from_boulders"));
		ironThrowingKnifeWrappedRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"iron_throwing_knife"));
		ironThrowingKnifeWrappedRecipe.setSensitiveTo(PlayerSkills.WEAPONSMITH, 5);
		shieldRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"heavy_shield"));
		shieldRecipeWrapper.setSensitiveTo(PlayerSkills.ARMORSMITH, 4, 4);
		crossbowRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"double_crossbow"));
		crossbowRecipeWrapper.setSensitiveTo(PlayerSkills.MECHANICS, 4, 4);
		crossbowBoltWrappedRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"crossbow_bolt"));
		crossbowBoltWrappedRecipe.setSensitiveTo(PlayerSkills.WEAPONSMITH, 2);
		triggerSpringRecipe.setRegistryName(IblisMod.MODID, "trigger_spring");
		steelHelmetRecipeWrapper.setRegistryName(IblisMod.MODID, "steel_helmet");
		steelChestplateRecipeWrapper.setRegistryName(IblisMod.MODID, "steel_chestplate");
		steelLegginsRecipeWrapper.setRegistryName(IblisMod.MODID, "steel_leggins");
		steelBootsRecipeWrapper.setRegistryName(IblisMod.MODID, "steel_boots");
		bulletWrapped.setRegistryName(IblisMod.MODID, "bullet");
		shotWrapped.setRegistryName(IblisMod.MODID, "shot");
		rri.setRegistryName(new ResourceLocation(IblisMod.MODID,"recipe_repair_item"));
		ironCoalRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"recipe_iron_coal"));
		ironOreCoalRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"recipe_ironore_coal"));
		ItemStack shotgun = new ItemStack(IblisItems.SHOTGUN);
		shotgun.setTagCompound(new NBTTagCompound());
		shotgun.getTagCompound().setInteger(NBTTagsKeys.DURABILITY, 600);
		ShapedOreRecipe shotgunRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),shotgun, 
				"  W",
				" ST",
				"S  ", 'W', "plankWood", 'S', "ingotSteel", 'T', spring);
		PlayerSensitiveShapedRecipeWrapper shotgunRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(shotgunRecipe);
		shotgunRecipeWrapper.setSensitiveTo(PlayerSkills.MECHANICS, 12, 20);
		shotgunRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"shotgun_recipe"));
		
		event.getRegistry().register(recipe1);
		event.getRegistry().register(recipe2);
		if (!disableMedkitRecipe) {
			event.getRegistry().register(medkitRecipe);
		}
		event.getRegistry().register(boulderRecipe);
		event.getRegistry().register(cobblestoneRecipe);
		event.getRegistry().register(ironThrowingKnifeWrappedRecipe);
		event.getRegistry().register(shieldRecipeWrapper);
		event.getRegistry().register(crossbowRecipeWrapper);
		event.getRegistry().register(crossbowBoltWrappedRecipe);
		event.getRegistry().register(triggerSpringRecipe);
		event.getRegistry().register(steelHelmetRecipeWrapper);
		event.getRegistry().register(steelChestplateRecipeWrapper);
		event.getRegistry().register(steelLegginsRecipeWrapper);
		event.getRegistry().register(steelBootsRecipeWrapper);
		if (!disableShotgunRecipes) {
			event.getRegistry().register(bulletWrapped);
			event.getRegistry().register(shotWrapped);
			event.getRegistry().register(shotgunRecipeWrapper);
		}
		event.getRegistry().register(rri);
		event.getRegistry().register(ironOreCoalRecipe);
		event.getRegistry().register(ironCoalRecipe);
		
		this.addShapelessNuggetsOrShradsRecipe("ingotSteel", "nuggetSteel", "steel_ingot_from_nuggets", "nugget_steel", event.getRegistry());

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
			if(!recipeReplacement.sensitiveSkill.enabled)
				continue;
			if (CraftingHandler.itemMatches(repairableStack, recipeReplacement.getRecipeOutput())) {
				double skillValue = recipeReplacement.sensitiveSkill.getFullSkillValue(player) - recipeReplacement.minimalSkill;
				PlayerSensitiveShapedRecipeWrapper.getCraftingResult(repairableStack, skillValue, true);
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
			this.sendSlotContents(event.getContainer(), 0, ItemStack.EMPTY);
		}
	}

	@SubscribeEvent
	public void onPlayerOpenContainerEvent(PlayerContainerEvent.Close event) {
		if(event.getContainer() instanceof ContainerRepair)
			openedContainers.remove(event.getContainer());
	}

	static boolean isArmor(ItemStack is) {
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
		if(is.getItem() instanceof ItemArmor){
			ItemArmor ia = (ItemArmor)is.getItem();
			switch(ia.getArmorMaterial()){
			case DIAMOND:
				return 10d;
			case IRON:
				return 6d;
			case GOLD:
				return 4d;
			case CHAIN:
				return 2d;
			case LEATHER:
				return 1d;
			default:
				break;
			}
		}
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
	
	static boolean isWeapon(ItemStack is) {
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
		minimalSkill*=2;
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
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
	}
	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
	}

	public static boolean itemMatches(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem().getHasSubtypes() && OreDictionary.itemMatches(stack1, stack2, false)
				|| stack1.getItem().equals(stack2.getItem());
	}
}
