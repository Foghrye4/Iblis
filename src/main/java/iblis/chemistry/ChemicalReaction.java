package iblis.chemistry;

public class ChemicalReaction {
	public final ReactionIngridient[] ingridients;
	private int temperatureStart = 0; // Kelvins
	private int tKRatio = 10;
	private float entalpy = 1.0f;
	private ReactionIngridient[] result;
	private boolean isReversive = false;

	public ChemicalReaction(ReactionIngridient... ingridientsIn) {
		ingridients = ingridientsIn;
	}
	 
	public ChemicalReaction setTKRatio(int tKRatioIn) {
		tKRatio = tKRatioIn;
		return this;
	}

	public ChemicalReaction setResult(ReactionIngridient... resultIn) {
		result = resultIn;
		return this;
	}

	public ChemicalReaction setEntalpy(float entalpyIn) {
		entalpy = entalpyIn;
		return this;
	}
	
	public ChemicalReaction setTemperatureStart(int temperatureStartIn) {
		temperatureStart = temperatureStartIn;
		return this;
	}
	
	public void doReaction(Reactor reactor, IReactorOwner owner) {
		float reactionYield = 1.0f;
		if(!isReversive)
			reactionYield = owner.getReactionYield();
		int speed = calculateSpeed(reactor);
		if (speed == 0)
			return;
		float speedMultiplier = speed;
		float totalAmount = reactor.getTotalAmount();
		for (ReactionIngridient ingridientStack : ingridients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingridientStack.substance);
			if (substanceStack==null || substanceStack.isEmpty()) {
				return;
			}
			speedMultiplier *= substanceStack.amount() / totalAmount;
		}
		float stoichiometricCoefficient = 1.0f;
		for (ReactionIngridient ingridientStack : ingridients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingridientStack.substance);
			float reactiveAmount = ingridientStack.amount * speedMultiplier * stoichiometricCoefficient;
			if (substanceStack.amount() < reactiveAmount)
				stoichiometricCoefficient *= substanceStack.amount() / reactiveAmount;
		}
		float reactiveMultiplier = speedMultiplier * stoichiometricCoefficient;
		if (reactiveMultiplier < 1.0f)
			return;
		for (ReactionIngridient ingridientStack : ingridients) {
			reactor.reduceSubstance(ingridientStack.substance, ingridientStack.amount * reactiveMultiplier);
		}
		for(ReactionIngridient stack:result){
			reactor.putSubstance(stack.substance, stack.amount*reactiveMultiplier*reactionYield);
		}
		reactor.addEntalpy(entalpy * reactiveMultiplier);
	}

	private int calculateSpeed(Reactor reactor) {
		int dt = (int) ((reactor.getTemperature() - temperatureStart) / tKRatio);
		if (dt <= 0)
			return 0;
		if (dt >= 30)
			return Integer.MAX_VALUE;
		return 1 << dt;
	}

	public ChemicalReaction register() {
		ChemistryRegistry.registerChemicalReaction(this);
		if(isReversive)
			ChemistryRegistry.registerChemicalReaction(
new ChemicalReaction(this.result).setResult(ingridients).setEntalpy(entalpy)
	.setTemperatureStart(temperatureStart).setTKRatio(tKRatio).setReversive());
		return this;
	}

	public ChemicalReaction setReversive() {
		isReversive =true;
		return this;
	}
}
