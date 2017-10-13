package iblis.crafting;

import java.util.Map.Entry;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import iblis.player.PlayerUtils;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class PlayerSensitiveShapedRecipeWrapper extends ShapedRecipes {

	PlayerSkills sensitiveSkill = PlayerSkills.WEAPONSMITH;
	private int minimalSkill = 0;
	private IRecipe wrappedRecipe;

	public PlayerSensitiveShapedRecipeWrapper(IRecipe recipeIn) {
		super(recipeIn.getGroup(), 
			(recipeIn instanceof  ShapedRecipes)?((ShapedRecipes)recipeIn).recipeWidth:3,
			(recipeIn instanceof  ShapedRecipes)?((ShapedRecipes)recipeIn).recipeHeight:3, 
			recipeIn.getIngredients(), recipeIn.getRecipeOutput());
		wrappedRecipe = recipeIn;
	}

	public PlayerSensitiveShapedRecipeWrapper setSesitiveTo(PlayerSkills skillIn, int minimalSkillIn) {
		sensitiveSkill = skillIn;
		minimalSkill = minimalSkillIn;
		return this;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		double skillValue = IblisMod.proxy.getPlayerSkillValue(sensitiveSkill, inv);
		skillValue -= minimalSkill;
		return this.getCraftingResult(this.getRecipeOutput().copy(), skillValue, false);
	}
	
	/** Modify item stack and return it instance. Does not create a copy. */
	public ItemStack getCraftingResult(ItemStack output1, double skillValue, boolean additive){
		if (!output1.hasTagCompound())
			output1.setTagCompound(new NBTTagCompound());
		output1.getTagCompound().setInteger("quality", (int) skillValue);
		NBTTagList attributeModifiersNBTList = new NBTTagList();
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			for (Entry<String, AttributeModifier> entry : output1.getAttributeModifiers(slot).entries()) {
				double modifierValue = PlayerUtils.getQualityModifierValue(skillValue, output1, slot,
						entry.getKey(), additive);
				if (modifierValue != 0d) {
					NBTTagCompound modifierNBT = SharedMonsterAttributes
							.writeAttributeModifierToNBT(new AttributeModifier(entry.getValue().getID(),
									entry.getValue().getName(), modifierValue, entry.getValue().getOperation()));
					modifierNBT.setString("Slot", slot.getName());
					modifierNBT.setString("AttributeName", entry.getKey());
					attributeModifiersNBTList.appendTag(modifierNBT);
				}
			}
		}
		output1.getTagCompound().setTag("AttributeModifiers", attributeModifiersNBTList);
		return output1;
	}


	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return this.wrappedRecipe.matches(inv, worldIn);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.wrappedRecipe.getRecipeOutput();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		for (IContainerListener listener : inv.eventHandler.listeners) {
			if (listener instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) listener;
				this.raiseSkill(player, 1);
			}
		}
		return this.wrappedRecipe.getRemainingItems(inv);
	}
	
	public void raiseSkill(EntityPlayer player, int times){
		sensitiveSkill.raiseSkill(player, (minimalSkill + 1) * times);
	}

	@Override
	public boolean canFit(int width, int height) {
		return this.wrappedRecipe.canFit(width, height);
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.wrappedRecipe.getIngredients();
	}
}
