package iblis.chemistry;

import iblis.init.IblisSubstances;
import net.minecraft.nbt.NBTTagCompound;

public class SubstanceStack {
	public static final SubstanceStack EMPTY = new SubstanceStack(IblisSubstances.IMPURITY);

	public Substance substance;
	public float solidAmount = 0.0f;
	public float liquidAmount = 0.0f;
	public float gaseousAmount = 0.0f;

	public SubstanceStack(Substance substanceIn) {
		substance = substanceIn;
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

	public boolean containGaseousPhase() {
		return gaseousAmount > 0.0f;
	}

	public SubstanceStack splitGaseousPhase() {
		SubstanceStack gas = new SubstanceStack(substance).setGaseousAmount(gaseousAmount);
		gaseousAmount = 0.0f;
		return gas;
	}

	private SubstanceStack setGaseousAmount(float gaseousAmountIn) {
		gaseousAmount = gaseousAmountIn;
		return this;
	}
	
	private void setSolidAmount(float amount) {
		this.solidAmount = amount;
	}

	private void setLiquidAmount(float amount) {
		this.liquidAmount = amount;
	}

	public float balanceEntalpy(int temperature, float totalAmount) {
		float entalpy = 0.0f;
		float evaporationEntalpy = substance.getEvaporationEntalpy();
		float meltingEntalpy = substance.getMeltingEntalpy();
		int dbpt = temperature - substance.getBoilingPoint();
		int dmpt = temperature - substance.getMeltingPoint();
		if (dbpt < 0 && gaseousAmount > 0.0f) {
			float dEntalpy = Math.min(evaporationEntalpy * gaseousAmount, dbpt * totalAmount);
			float dGaseousAmount = gaseousAmount * dEntalpy / evaporationEntalpy;
			gaseousAmount-=dGaseousAmount;
			liquidAmount += dGaseousAmount;
			entalpy += dEntalpy;
		} else if (dbpt > 0 && liquidAmount > 0.0f) {
			float dEntalpy = Math.min(evaporationEntalpy * liquidAmount, dbpt * totalAmount);
			float dLiquidAmount = liquidAmount*dEntalpy/evaporationEntalpy;
			gaseousAmount += dLiquidAmount;
			liquidAmount -= dLiquidAmount;
			entalpy -= dEntalpy;
		}
		if (dmpt < 0 && liquidAmount > 0.0f) {
			float dEntalpy = Math.min(meltingEntalpy * liquidAmount, dmpt * totalAmount);
			float dLiquidAmount = liquidAmount*dEntalpy/meltingEntalpy;
			solidAmount += dLiquidAmount;
			liquidAmount -= dLiquidAmount;
			entalpy += dEntalpy;
		} else if (dmpt > 0 && solidAmount > 0.0f) {
			float dEntalpy = Math.min(meltingEntalpy * solidAmount, dmpt * totalAmount);
			float dSolidAmount = solidAmount*dEntalpy/meltingEntalpy;
			solidAmount -= dSolidAmount;
			liquidAmount += dSolidAmount;
			entalpy -= dEntalpy;
		}
		return entalpy;
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
}
