package iblis.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.init.RegistryEventHandler;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class CraftingHandler {
	
	List<PlayerSensitiveRecipeWrapper> replacements = new ArrayList<PlayerSensitiveRecipeWrapper>();
	
	public void replaceRecipes() {
		IForgeRegistryModifiable<IRecipe> recipeRegistry = (IForgeRegistryModifiable<IRecipe>) ForgeRegistries.RECIPES;
		Iterator<IRecipe> irecipes = recipeRegistry.iterator();
		List<ResourceLocation> vanillaRecipesToRemove = new ArrayList<ResourceLocation>();
		while (irecipes.hasNext()) {
			IRecipe recipe = irecipes.next();
			ItemStack is = recipe.getRecipeOutput();
			if (is != null) {
				if(isArmor(is)){
					PlayerSensitiveRecipeWrapper recipeReplacement = new PlayerSensitiveRecipeWrapper(recipe);
					recipeReplacement.setSesitiveTo(PlayerSkills.ARMORSMITH, getArmorCraftingRequiredSkill(is));
					recipeReplacement.setRegistryName(new ResourceLocation(IblisMod.MODID,recipe.getRegistryName().getResourcePath()));
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
				}
				else if(isWeapon(is)){
					PlayerSensitiveRecipeWrapper recipeReplacement = new PlayerSensitiveRecipeWrapper(recipe);
					recipeReplacement.setSesitiveTo(PlayerSkills.WEAPONSMITH, getWeaponCraftingRequiredSkill(is));
					recipeReplacement.setRegistryName(new ResourceLocation(IblisMod.MODID,recipe.getRegistryName().getResourcePath()));
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
				}
				else if(is.getItem() instanceof ItemBow) {
					if (!is.hasTagCompound())
						is.setTagCompound(new NBTTagCompound());
					NBTTagList attributeModifiersNBTList = new NBTTagList();
					NBTTagCompound modifierNBT = SharedMonsterAttributes.writeAttributeModifierToNBT(new AttributeModifier(
							SharedIblisAttributes.ARROW_DAMAGE_MODIFIER, "Arrow damage", 2d, 0));
					modifierNBT.setString("Slot", EntityEquipmentSlot.MAINHAND.getName());
					modifierNBT.setString("AttributeName", SharedIblisAttributes.ARROW_DAMAGE.getName());
					attributeModifiersNBTList.appendTag(modifierNBT);
					is.getTagCompound().setTag("AttributeModifiers", attributeModifiersNBTList);
					PlayerSensitiveRecipeWrapper recipeReplacement = new PlayerSensitiveRecipeWrapper(recipe);
					recipeReplacement.setRegistryName(new ResourceLocation(IblisMod.MODID,recipe.getRegistryName().getResourcePath()));
					recipeReplacement.setSesitiveTo(PlayerSkills.WEAPONSMITH, getWeaponCraftingRequiredSkill(is));
					replacements.add(recipeReplacement);
					vanillaRecipesToRemove.add(recipe.getRegistryName());
				}
			}
		}
		for(ResourceLocation key:vanillaRecipesToRemove)
			recipeRegistry.remove(key);
	}

	public void addRecipes() {
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
		
		PlayerSensitiveShapedRecipe recipe1 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients, new ItemStack(IblisItems.GUIDE,1,0));
		PlayerSensitiveShapedRecipe recipe2 = new PlayerSensitiveShapedRecipe(IblisMod.MODID+":shaped_player_sensitive", 2, 2, guideRecipeIngridients2, new ItemStack(IblisItems.GUIDE,1,0));
		recipe1.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_1"));
		recipe2.setRegistryName(new ResourceLocation(IblisMod.MODID,"guide_book_2"));
		RegistryEventHandler.recipes.add(recipe1);
		RegistryEventHandler.recipes.add(recipe2);

		ShapedOreRecipe shotgunRecipe = new ShapedOreRecipe(new ResourceLocation(IblisMod.MODID,"shaped"),IblisItems.SHOTGUN, 
				"  W",
				" S ",
				"S  ", 'W', "plankWood", 'S', "ingotSteel");
		PlayerSensitiveRecipeWrapper shotgunRecipeWrapper = new PlayerSensitiveRecipeWrapper(shotgunRecipe);
		shotgunRecipeWrapper.setSesitiveTo(PlayerSkills.WEAPONSMITH, 20);
		shotgunRecipeWrapper.setRegistryName(new ResourceLocation(IblisMod.MODID,"shotgun_recipe"));
		replacements.add(shotgunRecipeWrapper);
		for(PlayerSensitiveRecipeWrapper recipeReplacement: replacements)
			RegistryEventHandler.recipes.add(recipeReplacement);
	}
	
	@SubscribeEvent
	public void onAnvilRepair(AnvilRepairEvent event) {
		ItemStack repairableStack = event.getItemResult();
			for(PlayerSensitiveRecipeWrapper recipeReplacement: replacements){
				if(OreDictionary.itemMatches(repairableStack, recipeReplacement.getRecipeOutput(), false)) {
					double skillValue = recipeReplacement.sensitiveSkill.getFullSkillValue(event.getEntityPlayer());
					recipeReplacement.getCraftingResult(event.getItemResult(), skillValue, true);
					recipeReplacement.raiseSkill(event.getEntityPlayer());
					return;
				}
			}
				
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


}
