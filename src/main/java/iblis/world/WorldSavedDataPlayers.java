package iblis.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class WorldSavedDataPlayers extends WorldSavedData {
	
	public final static String DATA_IDENTIFIER = "iblis_players_data";
	private final static String TAG = "IblisPlayerData";
	private final static String ATTRIBUTES_TAG = "Attributes";
	private final static String UUID_TAG = "UUID";
	public final Map<UUID, NBTTagList> playerData = new HashMap<UUID, NBTTagList>();
	
	public WorldSavedDataPlayers(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList tagList = nbt.getTagList(TAG, 10);
		for(int i=0;i<tagList.tagCount();i++){
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			playerData.put(tag.getUniqueId(UUID_TAG), tag.getTagList(ATTRIBUTES_TAG, 10));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList tagList = new NBTTagList(); 
		for(Entry<UUID,NBTTagList> entry:playerData.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId(UUID_TAG, entry.getKey());
			tag.setTag(ATTRIBUTES_TAG, entry.getValue());
			tagList.appendTag(tag);
		}
		compound.setTag(TAG, tagList);
		return compound;
	}

}
