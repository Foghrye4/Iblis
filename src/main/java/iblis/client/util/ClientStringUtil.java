package iblis.client.util;

import iblis.chemistry.ChemicalReaction;
import iblis.chemistry.ReactionIngredient;
import iblis.chemistry.Substance;
import iblis.chemistry.SubstanceStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.crafting.Ingredient;

public class ClientStringUtil {
	
	public static float formatFloat(float value, int decimals){
		value*=decimals;
		return ((int)value)/decimals;
	}

	public static String formatSubstanceAmount(SubstanceStack ss) {
		return I18n.format(ss.substance.unlocalizedName+".name")+I18n.format("iblis.gui.amount",formatFloat(ss.amount(),10));
	}

	public static String formatSubstanceInfo(Substance s) {
		return I18n.format(s.unlocalizedName+".name")+I18n.format("iblis.gui.longDash")+I18n.format(s.unlocalizedName+".formula");
	}

	public static String formatReactionInput(ChemicalReaction reaction) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<reaction.ingredients.length;i++) {
			ReactionIngredient ingredient = reaction.ingredients[i];
			sb.append(ingredient.amount+"x"+I18n.format(ingredient.substance.unlocalizedName+".formula"));
			if(i<reaction.ingredients.length-1) {
				sb.append("+");
			}
		}
		sb.append(" ");
		sb.append(I18n.format("iblis.gui.atTemperatureAbove", reaction.temperatureStart-273.1f));
		if(reaction.isReversive)
			sb.append("iblis.gui.arrowLeftRight");
		else
			sb.append("iblis.gui.arrowRight");
		return sb.toString();
	}
	
	public static String formatReactionOutput(ChemicalReaction reaction) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<reaction.result.length;i++) {
			ReactionIngredient ingredient = reaction.ingredients[i];
			sb.append(ingredient.amount+"x"+I18n.format(ingredient.substance.unlocalizedName+".formula"));
			if(i<reaction.ingredients.length-1) {
				sb.append("+");
			}
		}
		sb.append("+");
		sb.append(I18n.format("iblis.gui.entalpy", reaction.entalpy));
		return sb.toString();
	}

}
