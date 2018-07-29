package iblis.chemistry;

public class ReactionIngredient {
	public final Substance substance;
	public final float amount;
	public ReactionIngredient(Substance substanceIn, float amountIn){
		substance=substanceIn;
		amount=amountIn;
	}
}
