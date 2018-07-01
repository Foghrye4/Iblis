package iblis.chemistry;

public class ChemicalReaction {
	private final SubstanceStack[] ingridients;
	private int temperatureStart = 0; // Kelvins
	private float entalpy = 1.0f;
	private SubstanceStack[] result;

	public ChemicalReaction(SubstanceStack... ingridientsIn) {
		ingridients = ingridientsIn;
	}
	
	public void doReaction(Reactor reactor) {
		int speed = calculateSpeed(reactor);
		if (speed == 0)
			return;
		float speedMultiplier = speed;
		float totalAmount = reactor.getTotalAmount();
		for (SubstanceStack ingridientStack : ingridients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingridientStack.substance);
			if (substanceStack.isEmpty()) {
				return;
			}
			speedMultiplier *= substanceStack.amount() / totalAmount;
		}
		float stoichiometricCoefficient = 1.0f;
		for (SubstanceStack ingridientStack : ingridients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingridientStack.substance);
			float reactiveAmount = ingridientStack.amount() * speedMultiplier * stoichiometricCoefficient;
			if (substanceStack.amount() < reactiveAmount)
				stoichiometricCoefficient *= substanceStack.amount() / reactiveAmount;
		}
		for (SubstanceStack ingridientStack : ingridients) {
			float reactiveAmount = ingridientStack.amount() * speedMultiplier * stoichiometricCoefficient;
			reactor.reduceSubstance(ingridientStack.substance, reactiveAmount);
		}
		reactor.addEntalpy(entalpy);
	}

	private int calculateSpeed(Reactor reactor) {
		int dt = reactor.getTemperature() - temperatureStart;
		if (dt <= 0)
			return 0;
		if (dt < 10)
			return 1;
		int speed = 2;
		int i = 0;
		dt /= 10;
		while (dt > 2) {
			speed *= speed;
			dt -= ++i;
		}
		return speed;
	}

}
