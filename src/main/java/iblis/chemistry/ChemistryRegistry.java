package iblis.chemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ChemistryRegistry {

	private final static Int2ObjectMap<List<ChemicalReaction>> reactionsByID = new Int2ObjectOpenHashMap<List<ChemicalReaction>>();
	private final static Int2ObjectMap<Substance> substancesByID = new Int2ObjectOpenHashMap<Substance>();
	private final static Map<Fluid,Substance> fluidToSubstanceMap = new HashMap<Fluid,Substance>();
	private final static Map<String,Substance> oreDictionaryToSubstanceMap = new HashMap<String,Substance>();
	private final static Map<Item,Substance> itemToSubstanceMap = new HashMap<Item,Substance>();
	private final static List<ChemicalReaction> EMPTY_LIST = new ImmutableList.Builder<ChemicalReaction> ().build();
	
	public static List<ChemicalReaction> getReactionByIngridientID(int substanceId) {
		if(reactionsByID.containsKey(substanceId))
			return reactionsByID.get(substanceId);
		else
			return EMPTY_LIST;
	}

	public static Substance getSubstanceByID(int id) {
		return substancesByID.get(id);
	}

	public static SubstanceStack fluidStackToSubstanceStack(FluidStack fluidStack) {
		Substance substance = fluidToSubstanceMap.get(fluidStack.getFluid());
		if(substance==null)
			return null;
		SubstanceStack ss = new SubstanceStack(substance);
		ss.liquidAmount=fluidStack.amount;
		return ss;
	}

	public static SubstanceStack itemStackToSubstanceStack(ItemStack stack) {
		for (int oreID : OreDictionary.getOreIDs(stack)) {
			String oreName = OreDictionary.getOreName(oreID);
			Substance substance = oreDictionaryToSubstanceMap.get(oreName);
			SubstanceStack ss = new SubstanceStack(substance);
			ss.solidAmount = getAmountOf(oreName);
			return ss;
		}
		Substance substance = itemToSubstanceMap.get(stack.getItem());
		if (substance != null) {
			SubstanceStack ss = new SubstanceStack(substance);
			ss.solidAmount = getAmountOf(stack.getItem());
			return ss;
		}
		return null;
	}

	private static float getAmountOf(Item item) {
		if(item == Items.SNOWBALL)
			return 16;
		return 144;
	}

	private static float getAmountOf(String oreName) {
		if (oreName.contains("nugget") || oreName.contains("dustTiny"))
			return 16;
		if (oreName.contains("block"))
			return 1296;
		return 144;
	}
}
