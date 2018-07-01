package iblis.chemistry;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class ChemicalReactionRegistry {

	private final static Int2ObjectMap<List<ChemicalReaction>> reactionsByID = new Int2ObjectOpenHashMap<List<ChemicalReaction>>();
	private final static List<ChemicalReaction> EMPTY_LIST = new ImmutableList.Builder<ChemicalReaction> ().build();
	
	public static List<ChemicalReaction> getReactionByIngridientID(int substanceId) {
		if(reactionsByID.containsKey(substanceId))
			return reactionsByID.get(substanceId);
		else
			return EMPTY_LIST;
	}

}
