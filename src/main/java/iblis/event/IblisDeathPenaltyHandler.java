package iblis.event;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.entity.EntityPlayerZombie;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.util.PlayerUtils;
import iblis.world.WorldSavedDataPlayers;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisDeathPenaltyHandler {
	public boolean spawnPlayerZombie = true;
	public boolean noDeathPenalty = false;

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote)
			return;
		if (event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			WorldSavedDataPlayers playersData = (WorldSavedDataPlayers) IblisMod.proxy.getServer().worlds[0].getPerWorldStorage()
					.getOrLoadData(WorldSavedDataPlayers.class, WorldSavedDataPlayers.DATA_IDENTIFIER);
			NBTTagList attributesNBTList = null;
			NBTTagList books = null;
			boolean signOfRessurection = player.getEntityData().getBoolean(NBTTagsKeys.SIGN_OF_RESSURECTION);
			if (playersData != null && !signOfRessurection) {
				attributesNBTList = playersData.playerDataAttributes.get(player.getUniqueID());
				books = playersData.playerDataBooks.get(player.getUniqueID());
			}
			if (attributesNBTList != null && noDeathPenalty) {
				for(int i=0;i<attributesNBTList.tagCount();i++) {
					NBTTagCompound cNBT = attributesNBTList.getCompoundTagAt(i);
					String key = cNBT.getString("key");
					double value = cNBT.getDouble("value");
					IAttributeInstance attribute = player.getAttributeMap().getAttributeInstanceByName(key);
					if (attribute != null)
						attribute.setBaseValue(value);
				}
			}
			if (books != null && noDeathPenalty)
				player.getEntityData().setTag(NBTTagsKeys.EXPLORED_BOOKS, books);
			player.getEntityData().setBoolean(NBTTagsKeys.SIGN_OF_RESSURECTION, true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerChangeDimension(EntityTravelToDimensionEvent event){
		if (!(event.getEntity() instanceof EntityPlayerMP))
			return;
		this.savePlayerData((EntityPlayerMP) event.getEntity());
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
		this.savePlayerData(player);
		if (spawnPlayerZombie) {
			EntityPlayerZombie playerZombie = new EntityPlayerZombie(player, noDeathPenalty);
			player.world.spawnEntity(playerZombie);
		}
	}
	
	private void savePlayerData(EntityPlayerMP player){
		if (noDeathPenalty) {
			WorldSavedDataPlayers playersData = PlayerUtils.getOrCreateWorldSavedData(IblisMod.proxy.getServer().worlds[0]);
			playersData.playerDataKeys.add(player.getUniqueID());
			NBTTagList attributes = new NBTTagList();
			for(PlayerCharacteristics characteristic:PlayerCharacteristics.values()) {
				NBTTagCompound cNBT = new NBTTagCompound();
				cNBT.setDouble("value", characteristic.getAttributeInstance(player).getBaseValue());
				cNBT.setString("key", characteristic.getAttribute().getName());
				attributes.appendTag(cNBT);
			}
			for(PlayerSkills skill:PlayerSkills.values()) {
				NBTTagCompound cNBT = new NBTTagCompound();
				cNBT.setDouble("value", skill.getAttributeInstance(player).getBaseValue());
				cNBT.setString("key", skill.getAttribute().getName());
				attributes.appendTag(cNBT);
			}
			playersData.playerDataAttributes.put(player.getUniqueID(),attributes);
			playersData.markDirty();
			player.getEntityData().setBoolean(NBTTagsKeys.SIGN_OF_RESSURECTION, true);
		}
		
	}
	
	
}
