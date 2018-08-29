package iblis.client.util;

import java.util.List;

import iblis.chemistry.ChemicalReaction;
import iblis.chemistry.ReactionIngredient;
import iblis.chemistry.Substance;
import iblis.chemistry.SubstanceStack;
import iblis.client.gui.GuiElement;
import iblis.client.gui.GuiElementTextLine;
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

	public static void formatReaction(ChemicalReaction reaction, List<GuiElement> allElements,int maxLineLenght) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<reaction.ingredients.length;i++) {
			ReactionIngredient ingredient = reaction.ingredients[i];
			String ingredientFormatted = ingredient.amount+"x"+I18n.format(ingredient.substance.unlocalizedName+".formula");
			if(sb.length()+ingredientFormatted.length()>maxLineLenght) {
				allElements.add(new GuiElementTextLine(sb.toString()));
				sb.setLength(0);
			}
			sb.append(ingredientFormatted);
			if(i<reaction.ingredients.length-1) {
				sb.append("+");
			}
		}
		allElements.add(new GuiElementTextLine(sb.toString()));
		sb.setLength(0);
		sb.append(I18n.format("iblis.gui.atTemperatureAbove", formatFloat(reaction.temperatureStart-273.1f,10)));
		if(reaction.isReversive)
			sb.append(I18n.format("iblis.gui.arrowLeftRight"));
		else
			sb.append(I18n.format("iblis.gui.arrowRight"));
		allElements.add(new GuiElementTextLine(sb.toString()));
		sb.setLength(0);
		for(int i=0;i<reaction.result.length;i++) {
			ReactionIngredient ingredient = reaction.result[i];
			String ingredientFormatted = ingredient.amount+"x"+I18n.format(ingredient.substance.unlocalizedName+".formula");
			if(sb.length()+ingredientFormatted.length()>maxLineLenght) {
				allElements.add(new GuiElementTextLine(sb.toString()));
				sb.setLength(0);
			}
			sb.append(ingredientFormatted);
			if(i<reaction.ingredients.length-1) {
				sb.append("+");
			}
		}
		String entalpy = I18n.format("iblis.gui.entalpy", reaction.entalpy);
		if(sb.length()+entalpy.length()>maxLineLenght) {
			allElements.add(new GuiElementTextLine(sb.toString()));
			sb.setLength(0);
		}
		sb.append("+");
		sb.append(entalpy);
		allElements.add(new GuiElementTextLine(sb.toString()));
	}
}
