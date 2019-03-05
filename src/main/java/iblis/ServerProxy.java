package iblis;

import java.io.InputStream;

import com.google.gson.stream.JsonReader;

import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class ServerProxy {
	private MinecraftServer server;

	void load() {
	}
	
	public void init() {
	}

	public double getPlayerSkillValue(PlayerSkills sensitiveSkill, InventoryCrafting inv) {
		double skillValue = 0d;
		for (IContainerListener listener : inv.eventHandler.listeners) {
			if (listener instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) listener;
				double playerSkillValue = sensitiveSkill.getFullSkillValue(player);
				skillValue += playerSkillValue;
			}
		}
		return skillValue;
	}

	public EntityPlayer getPlayer(InventoryCrafting inv) {
		for (IContainerListener listener : inv.eventHandler.listeners) {
			if (listener instanceof EntityPlayerMP) {
				return (EntityPlayer) listener;
			}
		}
		return null;
	}

	public void registerRenders() {
	}
	
	public void setServer(MinecraftServer serverIn) {
		this.server = serverIn;
	}

	public MinecraftServer getServer() {
		return server;
	}
	
	public InputStream getResourceInputStream(ResourceLocation resource) {
		String resourceURLPath = "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath();
		return IblisMod.class.getResourceAsStream(resourceURLPath);
	}

	public void setToggleSprintByKeyBindSprint(boolean value) {}

	public void setHPRender(boolean boolean1) {}
}
