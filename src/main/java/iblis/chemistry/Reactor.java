package iblis.chemistry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Reactor {
	private Int2ObjectMap<SubstanceStack> content = new Int2ObjectOpenHashMap<SubstanceStack>();
	private float temperature = 293;
	
	public SubstanceStack getSubstanceStack(Substance substanceIn){
		return content.get(substanceIn.id);
	}
	
	public void reduceSubstance(Substance substanceIn, float amount) {
		SubstanceStack stack = content.get(substanceIn.id);
		if (amount + 1.0f >= stack.amount()) {
			content.remove(substanceIn.id);
		} else {
			stack.reduce(amount);
		}
	}

	public float getTotalAmount() {
		float amount = 0;
		for(SubstanceStack substanceStack: content.values())
			amount +=substanceStack.amount();
		return amount;
	}

	public float getTemperature() {
		return temperature;
	}
	
	public void setTemperature(float temperatureIn) {
		temperature = temperatureIn;
	}

	public void addEntalpy(float entalpy) {
		float totalAmount = getTotalAmount();
		if (totalAmount < 1f)
			temperature += entalpy;
		else
			temperature += entalpy / getTotalAmount();
	}

	public void tick(IReactorOwner owner) {
		Set<ChemicalReaction> reactions = new HashSet<ChemicalReaction>();
		for(SubstanceStack substanceStack: content.values()) {
			int substanceId = substanceStack.substance.id;
			reactions.addAll(ChemistryRegistry.getReactionByIngridientID(substanceId));
			this.addEntalpy(substanceStack.balanceEntalpy(temperature));
		}
		for(ChemicalReaction reaction:reactions){
			reaction.doReaction(this, owner);
		}
	}

	public void exhaustGasesTo(Reactor coldReactor) {
		Iterator<SubstanceStack> it = content.values().iterator();
		while(it.hasNext()){
			SubstanceStack substanceStack = it.next();
			if(substanceStack.isGaseous()){
				coldReactor.putSubstance(substanceStack, temperature);
				it.remove();
				continue;
			}
			else if(substanceStack.containGaseousPhase()){
				coldReactor.putSubstance(substanceStack.splitGaseousPhase(), temperature);
			}
		}
	}
	
	public void dumpLiquidsTo(Reactor other) {
		Iterator<SubstanceStack> it = content.values().iterator();
		while(it.hasNext()){
			SubstanceStack substanceStack = it.next();
			if(substanceStack.isLiquid()){
				other.putSubstance(substanceStack, temperature);
				it.remove();
				continue;
			}
			else if(substanceStack.containLiquidPhase()){
				other.putSubstance(substanceStack.splitLiquidPhase(), temperature);
			}
		}
	}

	public void dumpHeaviestTo(Reactor other) {
		if(content.isEmpty())
			return;
		Iterator<SubstanceStack> it = content.values().iterator();
		Substance heaviest = null;
		while(it.hasNext()){
			SubstanceStack substanceStack = it.next();
			if(substanceStack.isSolid()){
				other.putSubstance(substanceStack, temperature);
				it.remove();
				continue;
			}
			else if(substanceStack.containSolidPhase()) {
				other.putSubstance(substanceStack.splitSolidPhase(), temperature);
			}
			if (heaviest == null
					|| substanceStack.substance.getDensity(temperature) > heaviest.getDensity(temperature)) {
				heaviest = substanceStack.substance;
			}
		}
		it = content.values().iterator();
		while(it.hasNext()){
			SubstanceStack substanceStack = it.next();
			if(heaviest.dissolve(substanceStack.substance)){
				other.putSubstance(substanceStack, temperature);
				it.remove();
			}
		}
	}
	
	public void addContentOf(Reactor otherReactor) {
		for(SubstanceStack ss:otherReactor.content.values()){
			this.putSubstance(ss, otherReactor.getTemperature());
		}
		otherReactor.content.clear();
	}

	public void putSubstance(SubstanceStack substanceStack, float temperature2) {
		SubstanceStack stack = content.get(substanceStack.substance.id);
		if (stack == null) {
			content.put(substanceStack.substance.id, substanceStack);
		} else {
			stack.gaseousAmount += substanceStack.gaseousAmount;
			stack.liquidAmount += substanceStack.liquidAmount;
			stack.solidAmount += substanceStack.solidAmount;
		}
		this.addEntalpy((temperature2 - temperature) * substanceStack.amount());
	}
	
	public void putSubstance(Substance substance, float amount) {
		SubstanceStack stack = content.get(substance.id);
		if (stack == null) {
			stack = new SubstanceStack(substance, temperature, amount);
			content.put(substance.id, stack);
		} else {
			stack.addAmount(temperature, amount);
		}
	}
	
	public void putSubstance(Substance substance, float solidAmount, float liquidAmount, float gaseousAmount) {
		SubstanceStack stack = content.get(substance.id);
		if (stack == null) {
			stack = new SubstanceStack(substance);
			stack.gaseousAmount+=gaseousAmount;
			stack.liquidAmount+=liquidAmount;
			stack.solidAmount+=solidAmount;
			content.put(substance.id, stack);
		} else {
			stack.gaseousAmount+=gaseousAmount;
			stack.liquidAmount+=liquidAmount;
			stack.solidAmount+=solidAmount;
		}
	}


	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for(SubstanceStack substanceStack: content.values()) {
			NBTTagCompound stackNBT = new NBTTagCompound();
			substanceStack.writeToNBT(stackNBT);
			list.appendTag(stackNBT);
		}
		nbt.setTag("content", list);
		nbt.setFloat("temperature", temperature);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		content.clear();
		NBTTagList list = nbt.getTagList("content", 10);
		for(int i=0;i<list.tagCount();i++){
			NBTTagCompound stackNBT = list.getCompoundTagAt(i);
			SubstanceStack stack = SubstanceStack.createFromNBT(stackNBT);
			content.put(stack.substance.id, stack);
		}
		if(nbt.hasKey("temperature"))
			temperature = nbt.getFloat("temperature");
		else
			temperature = 293.0f;
	}
	
	public Collection<SubstanceStack> content(){
		return content.values();
	}

	public void clear() {
		content.clear();
	}
}
