package iblis.chemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import iblis.init.IblisSubstances;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ChemistryRegistry {

	private final static Int2ObjectMap<List<ChemicalReaction>> reactionsBySubstanceId = new Int2ObjectOpenHashMap<List<ChemicalReaction>>();
	private final static Int2ObjectMap<Substance> substancesByID = new Int2ObjectOpenHashMap<Substance>();
	private final static Int2ObjectMap<Set<Substance>> solutionGroupsBySubstanceId = new Int2ObjectOpenHashMap<Set<Substance>>();
	private final static Map<Fluid, Substance> fluidToSubstanceMap = new HashMap<Fluid, Substance>();
	private final static Map<PotionType, Substance> potionToSubstanceMap = new HashMap<PotionType, Substance>();
	private final static Map<String, List<SubstanceStack>> oreDictionaryToSubstanceMap = new HashMap<String, List<SubstanceStack>>();
	private final static Map<Item, List<SubstanceStack>> itemToSubstanceMap = new HashMap<Item, List<SubstanceStack>>();
	private final static List<ChemicalReaction> EMPTY_LIST = new ImmutableList.Builder<ChemicalReaction>().build();
	private final static Set<Substance> EMPTY_SET = new ImmutableSet.Builder<Substance>().build();
	
	static {
		reactionsBySubstanceId.defaultReturnValue(EMPTY_LIST);
		solutionGroupsBySubstanceId.defaultReturnValue(EMPTY_SET);
	}
	
	public static List<ChemicalReaction> getReactionByIngridientID(int substanceId) {
			return reactionsBySubstanceId.get(substanceId);
	}

	public static Substance getSubstanceByID(int id) {
		Substance substance = substancesByID.get(id);
		if(substance==null)
			return IblisSubstances.IMPURITY;
		return substance;
	}
	
	public static void registerSubstance(Substance substance, int id) {
		if(substancesByID.containsKey(id))
			throw new IllegalArgumentException("Error registering substance " + substance.unlocalizedName + ". ID " + id
					+ " already registered for " + substancesByID.get(id).unlocalizedName);
		substancesByID.put(id, substance);
	}

	public static boolean fluidStackToSubstanceStack(Reactor reactor, FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		Substance substance = fluidToSubstanceMap.get(fluid);
		if (substance == null)
			return false;
		reactor.putSubstance(substance, fluidStack.amount);
		reactor.addEntalpy((fluid.getTemperature(fluidStack) - reactor.getTemperature())*fluidStack.amount);
		return true;
	}

	public static boolean itemStackToSubstanceStack(Reactor reactor, ItemStack stack) {
		if(stack.isEmpty())
			return false;
		for (int oreID : OreDictionary.getOreIDs(stack)) {
			String oreName = OreDictionary.getOreName(oreID);
			List<SubstanceStack> ssl = oreDictionaryToSubstanceMap.get(oreName);
			if (ssl == null)
				continue;
			for (SubstanceStack ss : ssl)
				reactor.putSubstance(ss.substance, ss.solidAmount * stack.getCount(),
						ss.liquidAmount * stack.getCount(), ss.gaseousAmount * stack.getCount());
			return true;
		}
		List<SubstanceStack> ssl = itemToSubstanceMap.get(stack.getItem());
		if (ssl != null) {
			for (SubstanceStack ss : ssl)
				reactor.putSubstance(ss.substance, ss.solidAmount * stack.getCount(),
						ss.liquidAmount * stack.getCount(), ss.gaseousAmount * stack.getCount());
			return true;
		}
		return false;
	}

	public static void registerFluidToSubstanceConversion(Fluid key, Substance value) {
		fluidToSubstanceMap.put(key, value);
	}

	public static void registerOreDictionaryToSubstanceStackConversion(String oreDictionaryName, List<SubstanceStack> ss) {
		oreDictionaryToSubstanceMap.put(oreDictionaryName, ss);
	}

	public static void registerItemToSubstanceStackConversion(Item item, List<SubstanceStack> ss) {
		itemToSubstanceMap.put(item, ss);
	}
	

	public static void registerPotionToSubstanceConversion(PotionType potion, Substance substance) {
		potionToSubstanceMap.put(potion, substance);
	}

	public static void registerChemicalReaction(ChemicalReaction reaction) {
		for (ReactionIngridient ss : reaction.ingridients) {
			List<ChemicalReaction> reactionList = reactionsBySubstanceId.get(ss.substance.id);
			if (reactionList == EMPTY_LIST) {
				reactionList = new ArrayList<ChemicalReaction>();
				reactionsBySubstanceId.put(ss.substance.id, reactionList);
			}
			reactionList.add(reaction);
		}
	}

	public static void addToSolutionGroup(Substance... substances) {
		Set<Substance> solutionGroup = EMPTY_SET;
		for (Substance s : substances) {
			solutionGroup = solutionGroupsBySubstanceId.get(s.id);
			if (solutionGroup != EMPTY_SET) {
				break;
			}
		}
		if (solutionGroup == EMPTY_SET) {
			solutionGroup = new HashSet<Substance>();
		}
		for (Substance s : substances) {
			Set<Substance> solutionGroup1 = solutionGroupsBySubstanceId.get(s.id);
			solutionGroup.add(s);
			if (solutionGroup1 != solutionGroup && solutionGroup1 != EMPTY_SET) {
				solutionGroup.addAll(solutionGroup1);
			}
			solutionGroupsBySubstanceId.put(s.id, solutionGroup);
		}
	}

	public static Set<Substance> getSolutionGroup(Substance substance) {
		return solutionGroupsBySubstanceId.get(substance.id);
	}

	public static boolean potionToSubstanceStack(Reactor reactor, PotionType potion) {
		Substance substance = potionToSubstanceMap.get(potion);
		if(substance==null)
			return false;
		reactor.putSubstance(substance, 1000.0f);
		return true;
	}
}
