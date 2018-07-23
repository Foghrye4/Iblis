package iblis.chemistry;

import iblis.init.IblisSubstances;
import net.minecraft.nbt.NBTTagCompound;

public class SubstanceStack {
	public static final SubstanceStack EMPTY = new SubstanceStack(IblisSubstances.IMPURITY);
	public static final float HYSTERESIS = 1.0f;

	public final Substance substance;
	public float solidAmount = 0.0f;
	public float liquidAmount = 0.0f;
	public float gaseousAmount = 0.0f;

	public SubstanceStack(Substance substanceIn) {
		substance = substanceIn;
	}

	public SubstanceStack(Substance substance, float temperature, float amount) {
		this(substance);
		this.addAmount(temperature, amount);
	}
	
	public void addAmount(float temperature, float amount){
		if (temperature > substance.getBoilingPoint())
			gaseousAmount += amount;
		else if (temperature < substance.getMeltingPoint())
			solidAmount += amount;
		else
			liquidAmount += amount;
	}

	public boolean isEmpty() {
		return solidAmount <= 0.0f && liquidAmount <= 0.0f && gaseousAmount <= 0.0f;
	}

	public float amount() {
		return solidAmount + liquidAmount + gaseousAmount;
	}

	public void reduce(float amount) {
		if (gaseousAmount < amount) {
			amount -= gaseousAmount;
			gaseousAmount = 0.0f;
		} else {
			gaseousAmount -= amount;
			return;
		}
		if (liquidAmount < amount) {
			amount -= liquidAmount;
			liquidAmount = 0.0f;
		} else {
			liquidAmount -= amount;
			return;
		}
		if (solidAmount < amount) {
			solidAmount = 0.0f;
		} else {
			solidAmount -= amount;
		}
	}

	public boolean isGaseous() {
		return liquidAmount == 0.0f && solidAmount == 0.0f;
	}
	
	public boolean isSolid() {
		return liquidAmount == 0.0f && gaseousAmount == 0.0f;
	}
	
	public boolean isLiquid() {
		return solidAmount == 0.0f && gaseousAmount == 0.0f;
	}

	public boolean containGaseousPhase() {
		return gaseousAmount > 0.0f;
	}
	
	public boolean containSolidPhase() {
		return solidAmount > 0.0f;
	}

	public boolean containLiquidPhase() {
		return liquidAmount > 0.0f;
	}

	public SubstanceStack splitGaseousPhase() {
		SubstanceStack gas = new SubstanceStack(substance).setGaseousAmount(gaseousAmount);
		gaseousAmount = 0.0f;
		return gas;
	}
	
	public SubstanceStack splitSolidPhase() {
		SubstanceStack solid = new SubstanceStack(substance).setSolidAmount(solidAmount);
		solidAmount = 0.0f;
		return solid;
	}
	
	public SubstanceStack splitLiquidPhase() {
		SubstanceStack liquid = new SubstanceStack(substance).setLiquidAmount(liquidAmount);
		liquidAmount = 0.0f;
		return liquid;
	}

	public SubstanceStack setGaseousAmount(float gaseousAmountIn) {
		gaseousAmount = gaseousAmountIn;
		return this;
	}
	
	public SubstanceStack setSolidAmount(float amount) {
		this.solidAmount = amount;
		return this;
	}

	public SubstanceStack setLiquidAmount(float amount) {
		this.liquidAmount = amount;
		return this;
	}

	public float balanceEntalpy(float temperature) {
		float evaporationEntalpy = substance.getEvaporationEntalpy();
		float meltingEntalpy = substance.getMeltingEntalpy();
		if (temperature < substance.getBoilingPoint() - HYSTERESIS && gaseousAmount > 1.0f) {
			float amount = Math.min(substance.getBoilingPoint() - temperature, gaseousAmount);
			gaseousAmount -= amount;
			liquidAmount += amount;
			return evaporationEntalpy * amount;
		}
		if (temperature > substance.getBoilingPoint() + HYSTERESIS && liquidAmount > 1.0f) {
			float amount = Math.min(temperature - substance.getBoilingPoint(), liquidAmount);
			gaseousAmount += amount;
			liquidAmount -= amount;
			return -evaporationEntalpy * amount;
		}
		if (temperature < substance.getMeltingPoint() - HYSTERESIS && liquidAmount > 1.0f) {
			float amount = Math.min(substance.getMeltingPoint() - temperature, liquidAmount);
			liquidAmount -= amount;
			solidAmount += amount;
			return meltingEntalpy * amount;
		}
		if (temperature > substance.getMeltingPoint() + HYSTERESIS && solidAmount > 1.0f) {
			float amount = Math.min(temperature - substance.getMeltingPoint(), solidAmount);
			liquidAmount += amount;
			solidAmount -= amount;
			return -meltingEntalpy * amount;
		}
		return 0.0f;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("id", substance.id);
		nbt.setFloat("solid", this.solidAmount);
		nbt.setFloat("liquid", this.liquidAmount);
		nbt.setFloat("gaseous", this.gaseousAmount);
	}

	public static SubstanceStack createFromNBT(NBTTagCompound nbt) {
		SubstanceStack stack = new SubstanceStack(ChemistryRegistry.getSubstanceByID(nbt.getInteger("id")));
		stack.setSolidAmount(nbt.getFloat("solid"));
		stack.setLiquidAmount(nbt.getFloat("liquid"));
		stack.setGaseousAmount(nbt.getFloat("gaseous"));
		return stack;
	}

	public SubstanceStack copy() {
		SubstanceStack ss = new SubstanceStack(this.substance);
		ss.solidAmount=this.solidAmount;
		ss.liquidAmount=this.liquidAmount;
		ss.gaseousAmount=this.gaseousAmount;
		return ss;
	}
}
