package iblis.chemistry;

public class ChemicalReaction {
	public final ReactionIngredient[] ingredients;
	public int temperatureStart = 183; // Kelvins
	private int tKRatio = 10;
	public float entalpy = 1.0f;
	public ReactionIngredient[] result;
	public boolean isReversive = false;

	public ChemicalReaction(ReactionIngredient... ingredientsIn) {
		ingredients = ingredientsIn;
	}
	 
	public ChemicalReaction setTKRatio(int tKRatioIn) {
		tKRatio = tKRatioIn;
		return this;
	}

	public ChemicalReaction setResult(ReactionIngredient... resultIn) {
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
		for (ReactionIngredient ingredientStack : ingredients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingredientStack.substance);
			if (substanceStack==null || substanceStack.isEmpty()) {
				return;
			}
			speedMultiplier *= substanceStack.amount() / totalAmount;
		}
		float stoichiometricCoefficient = 1.0f;
		for (ReactionIngredient ingredientStack : ingredients) {
			SubstanceStack substanceStack = reactor.getSubstanceStack(ingredientStack.substance);
			float reactiveAmount = ingredientStack.amount * speedMultiplier * stoichiometricCoefficient;
			if (substanceStack.amount() < reactiveAmount)
				stoichiometricCoefficient *= substanceStack.amount() / reactiveAmount;
		}
		float reactiveMultiplier = speedMultiplier * stoichiometricCoefficient;
		if (reactiveMultiplier < 1.0f)
			return;
		for (ReactionIngredient ingredientStack : ingredients) {
			reactor.reduceSubstance(ingredientStack.substance, ingredientStack.amount * reactiveMultiplier);
		}
		for(ReactionIngredient stack:result){
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
		ChemistryRegistry.allReactions.add(this);
		ChemistryRegistry.registerChemicalReaction(this);
		if(isReversive)
			ChemistryRegistry.registerChemicalReaction(
new ChemicalReaction(this.result).setResult(ingredients).setEntalpy(entalpy)
	.setTemperatureStart(temperatureStart).setTKRatio(tKRatio).setReversive());
		return this;
	}

	public ChemicalReaction setReversive() {
		isReversive =true;
		return this;
	}
}
