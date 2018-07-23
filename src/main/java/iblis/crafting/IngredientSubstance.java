package iblis.crafting;

import javax.annotation.Nullable;

import iblis.chemistry.Reactor;
import iblis.chemistry.Substance;
import iblis.init.IblisItems;
import iblis.item.ItemSubstanceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class IngredientSubstance extends Ingredient {
	public final Substance substance;
	public final float amount;
	public final float purityMin;

	public IngredientSubstance(Substance substanceIn, float amountIn, float purityMinIn) {
		super(IblisItems.SUBSTANCE_CONTAINER.build(substanceIn, amountIn));
		substance = substanceIn;
		amount = amountIn;
		purityMin = purityMinIn;
	}
	
	@Override
	public boolean apply(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		} else {
			if (stack.getItem() instanceof ItemSubstanceContainer) {
				ItemSubstanceContainer substanceContainer = (ItemSubstanceContainer) stack.getItem();
				Reactor reactor = substanceContainer.readReactor(stack.getTagCompound());
				return reactor.getSubstanceStack(substance).amount() >= amount;
			}
			return false;
		}
	}
}
