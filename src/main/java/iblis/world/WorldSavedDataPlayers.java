package iblis.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import iblis.constants.NBTTagsKeys;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class WorldSavedDataPlayers extends WorldSavedData {
	
	public final static String DATA_IDENTIFIER = "iblis_players_data";
	public final Set<UUID> playerDataKeys = new HashSet<UUID>();
	public final Map<UUID, NBTTagList> playerDataAttributes = new HashMap<UUID, NBTTagList>();
	public final Map<UUID, NBTTagList> playerDataBooks = new HashMap<UUID, NBTTagList>();
	
	public WorldSavedDataPlayers(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList tagList = nbt.getTagList(NBTTagsKeys.IBLIS_PLAYERS_DATA, 10);
		for(int i=0;i<tagList.tagCount();i++){
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			playerDataAttributes.put(tag.getUniqueId(NBTTagsKeys.UUID), tag.getTagList(NBTTagsKeys.ATTRIBUTES, 10));
			playerDataBooks.put(tag.getUniqueId(NBTTagsKeys.UUID), tag.getTagList(NBTTagsKeys.EXPLORED_BOOKS, 10));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList tagList = new NBTTagList(); 
		for(UUID uid:playerDataKeys){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId(NBTTagsKeys.UUID, uid);
			if(playerDataAttributes.containsKey(uid))
				tag.setTag(NBTTagsKeys.ATTRIBUTES, playerDataAttributes.get(uid));
			if(playerDataBooks.containsKey(uid))
				tag.setTag(NBTTagsKeys.EXPLORED_BOOKS, playerDataBooks.get(uid));
			tagList.appendTag(tag);
		}
		compound.setTag(NBTTagsKeys.IBLIS_PLAYERS_DATA, tagList);
		return compound;
	}

}
