package iblis.crafting;

import java.util.Collection;

import iblis.chemistry.Reactor;
import iblis.chemistry.SubstanceStack;
import iblis.item.ItemSubstanceContainer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;

public class ShapedChemicalIngridientRecipe extends ShapedRecipes {

	public ShapedChemicalIngridientRecipe(String group, int width, int height, NonNullList<Ingredient> ingredients,
			ItemStack result) {
		super(group, width, height, ingredients, result);
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < ret.size(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			Ingredient ing = this.recipeItems.get(i);
			if (ing instanceof IngredientSubstance) {
				IngredientSubstance ings = (IngredientSubstance) ing;
				ItemSubstanceContainer substanceContainer = (ItemSubstanceContainer) stack.getItem();
				Reactor reactor = substanceContainer.readReactor(stack.getTagCompound());
				SubstanceStack ss = reactor.getSubstanceStack(ings.substance);
				float relAmount = ings.amount / ss.amount();
				Collection<SubstanceStack> content = reactor.content();
				for (SubstanceStack ss1 : content) {
					reactor.reduceSubstance(ss1.substance, ss1.amount() * relAmount);
				}
				reactor.writeToNBT(stack.getTagCompound());
				if (reactor.getTotalAmount() < 1.0f) {
					ret.set(i, ForgeHooks.getContainerItem(stack));
				} else {
					ret.set(i, stack.copy());
				}
			} else {
				ret.set(i, ForgeHooks.getContainerItem(stack));
			}
		}
		return ret;
	}
}
