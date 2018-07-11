package iblis.chemistry;

import java.util.Collection;
import java.util.Iterator;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Reactor {
	private Int2ObjectMap<SubstanceStack> content = new Int2ObjectOpenHashMap<SubstanceStack>();
	private int temperature = 293;
	
	public Reactor(){
		content.defaultReturnValue(SubstanceStack.EMPTY);
	}
	
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

	public int getTemperature() {
		return temperature;
	}
	
	public void setTemperature(int temperatureIn) {
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
		float totalAmount = this.getTotalAmount();
		for(SubstanceStack substanceStack: content.values()) {
			int substanceId = substanceStack.substance.id;
			for(ChemicalReaction reaction:ChemistryRegistry.getReactionByIngridientID(substanceId)){
				reaction.doReaction(this, owner);
			}
			this.addEntalpy(substanceStack.balanceEntalpy(temperature, totalAmount));
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

	public void putSubstance(SubstanceStack substanceStack, int temperature2) {
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
	
	public void putSubstance(Substance substance, float gaseousAmount, float liquidAmount, float solidAmount) {
		SubstanceStack stack = content.get(substance.id);
		if (stack == null) {
			stack = new SubstanceStack(substance);
			stack.gaseousAmount = gaseousAmount;
			stack.liquidAmount = liquidAmount;
			stack.solidAmount = solidAmount;
			content.put(substance.id, stack);
		} else {
			stack.gaseousAmount += gaseousAmount;
			stack.liquidAmount += liquidAmount;
			stack.solidAmount += solidAmount;
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
		nbt.setInteger("temperature", temperature);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		content.clear();
		NBTTagList list = nbt.getTagList("content", 10);
		for(int i=0;i<list.tagCount();i++){
			NBTTagCompound stackNBT = list.getCompoundTagAt(i);
			SubstanceStack stack = SubstanceStack.createFromNBT(stackNBT);
			content.put(stack.substance.id, stack);
		}
		temperature = nbt.getInteger("temperature");
	}
	
	public Collection<SubstanceStack> content(){
		return content.values();
	}

	public void clear() {
		content.clear();
	}
}
