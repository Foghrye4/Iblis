package iblis.chemistry;

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

	public void addEntalpy(float entalpy) {
		float totalAmount = getTotalAmount();
		if (totalAmount < 1f)
			temperature += entalpy;
		else
			temperature += entalpy / getTotalAmount();
	}

	public void tick() {
		float totalAmount = this.getTotalAmount();
		for(SubstanceStack substanceStack: content.values()) {
			int substanceId = substanceStack.substance.id;
			for(ChemicalReaction reaction:ChemistryRegistry.getReactionByIngridientID(substanceId)){
				reaction.doReaction(this);
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
		NBTTagList list = nbt.getTagList("content", 10);
		for(int i=0;i<list.tagCount();i++){
			NBTTagCompound stackNBT = list.getCompoundTagAt(i);
			SubstanceStack stack = SubstanceStack.createFromNBT(stackNBT);
			content.put(stack.substance.id, stack);
		}
		temperature = nbt.getInteger("temperature");
	}
}
