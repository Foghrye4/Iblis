package iblis.chemistry;

import iblis.init.IblisSubstances;

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

	public float balanceEntalpy(int temperature, float totalAmount) {
		float entalpy = 0.0f;
		float dGaseousAmount = 0.0f;
		float dLiquidAmount = 0.0f;
		int dbpt = temperature - substance.getBoilingPoint();
		int dmpt = temperature - substance.getMeltingPoint();
		if(dbpt < 0 && this.containGaseousPhase()) {
			entalpy += substance.getEvaporationEntalpy() * gaseousAmount;
			dGaseousAmount -= gaseousAmount;
			dLiquidAmount += dGaseousAmount;
		}
		else if(dbpt > 0 && liquidAmount > 0.0f) {
			entalpy -= substance.getEvaporationEntalpy() * liquidAmount;
			dLiquidAmount -= liquidAmount;
			dGaseousAmount += liquidAmount;
		}
		return 0;
	}
}
