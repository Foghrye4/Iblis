package iblis.chemistry;

public class ChemicalReaction {
	private final SubstanceStack[] ingridients;
	private int temperatureStart = 0; // Kelvins
	private float entalpy = 1.0f;
	private SubstanceStack[] result;

	public ChemicalReaction(SubstanceStack... ingridientsIn) {
		ingridients = ingridientsIn;
	}
	
	public void doReaction(Reactor reactor, IReactorOwner owner) {
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
		float reactiveMultiplier = speedMultiplier * stoichiometricCoefficient;
		for (SubstanceStack ingridientStack : ingridients) {
			reactor.reduceSubstance(ingridientStack.substance, ingridientStack.amount() * reactiveMultiplier);
		}
		for(SubstanceStack stack:result){
			reactor.putSubstance(stack.substance, stack.gaseousAmount*reactiveMultiplier*owner.getReactionYield(), stack.liquidAmount*reactiveMultiplier*owner.getReactionYield(), stack.solidAmount*reactiveMultiplier*owner.getReactionYield());
		}
		reactor.addEntalpy(entalpy);
	}

	private int calculateSpeed(Reactor reactor) {
		int dt = (reactor.getTemperature() - temperatureStart) / 10;
		if (dt <= 0)
			return 0;
		if (dt >= 30)
			return Integer.MAX_VALUE;
		return 1 << dt;
	}
}
