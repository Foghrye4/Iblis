package iblis.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import iblis.IblisMod;
import iblis.gui.GuiButtonImageWithTooltip;
import iblis.init.IblisItems;
import iblis.init.RegistryEventHandler;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class CraftingHandler  implements IContainerListener{
	
	List<PlayerSensitiveShapedRecipeWrapper> replacements = new ArrayList<PlayerSensitiveShapedRecipeWrapper>();
	
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
					PlayerSensitiveShapedRecipeWrapper recipeReplacement = new PlayerSensitiveShapedRecipeWrapper(recipe);
					recipeReplacement.setSesitiveTo(PlayerSkills.ARMORSMITH, getArmorCraftingRequiredSkill(is));
					recipeReplacement.setRegistryName(recipe.getRegistryName());
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
				} else if (isWeapon(is)) {
					PlayerSensitiveShapedRecipeWrapper recipeReplacement = new PlayerSensitiveShapedRecipeWrapper(recipe);
					recipeReplacement.setSesitiveTo(PlayerSkills.WEAPONSMITH, getWeaponCraftingRequiredSkill(is));
					recipeReplacement.setRegistryName(recipe.getRegistryName());
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
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
					PlayerSensitiveShapedRecipeWrapper recipeReplacement = new PlayerSensitiveShapedRecipeWrapper(recipe);
					recipeReplacement.setRegistryName(recipe.getRegistryName());
					recipeReplacement.setSesitiveTo(PlayerSkills.WEAPONSMITH, getWeaponCraftingRequiredSkill(is));
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
				}
			}
		}
		for (ResourceLocation key : vanillaRecipesToRemove)
			recipeRegistry.remove(key);
		this.addRecipes(event);
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
		
		
		PlayerSensitiveShapedRecipe recipe1 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients, new ItemStack(IblisItems.GUIDE,1,0));
		PlayerSensitiveShapedRecipe recipe2 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients2, new ItemStack(IblisItems.GUIDE,1,0));
		ShapedRecipes medkitRecipe = new ShapedRecipes(IblisMod.MODID+":shaped", 3, 3, medkitRecipeIngridients, new ItemStack(IblisItems.NONSTERILE_MEDKIT));
		recipe1.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_1"));
		recipe2.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_2"));
		medkitRecipe.setRegistryName(new ResourceLocation(IblisMod.MODID,"medkit"));
		event.getRegistry().register(recipe1);
		event.getRegistry().register(recipe2);
		event.getRegistry().register(medkitRecipe);

		ShapedOreRecipe shotgunRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),IblisItems.SHOTGUN, 
				"  W",
				" S ",
				"S  ", 'W', "plankWood", 'S', "ingotSteel");
		PlayerSensitiveShapedRecipeWrapper shotgunRecipeWrapper = new PlayerSensitiveShapedRecipeWrapper(shotgunRecipe);
		shotgunRecipeWrapper.setSesitiveTo(PlayerSkills.WEAPONSMITH, 20);
		shotgunRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"shotgun_recipe"));
		replacements.add(shotgunRecipeWrapper);
		for(PlayerSensitiveShapedRecipeWrapper recipeReplacement: replacements)
			event.getRegistry().register(recipeReplacement);
		
		FurnaceRecipes.instance().addSmelting(IblisItems.NONSTERILE_MEDKIT, new ItemStack(IblisItems.MEDKIT), 1.0f);

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
	
	private static int getArmorCraftingRequiredSkill(ItemStack is) {
		int minimalSkill = 0;
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
	
	private static int getWeaponCraftingRequiredSkill(ItemStack is) {
		int minimalSkill = 0;
		if(is.getItem() instanceof ItemBow) {
			minimalSkill = 5;
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
