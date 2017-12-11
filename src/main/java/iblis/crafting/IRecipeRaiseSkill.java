package iblis.crafting;

import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;

public interface IRecipeRaiseSkill {
	public void raiseSkill(EntityPlayer player, int times);
	public PlayerSkills getSensitiveSkill();
	public double getSkillExp();
}
