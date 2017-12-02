package iblis;

import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.InventoryCrafting;

public class ServerProxy {
	void load() {
	}

	public boolean isClient() {
		return false;
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
}
