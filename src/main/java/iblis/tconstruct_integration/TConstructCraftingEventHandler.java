package iblis.tconstruct_integration;

import iblis.player.PlayerSkills;
import iblis.util.PlayerUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ranged.ProjectileLauncherCore;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;

public class TConstructCraftingEventHandler {

	@SubscribeEvent
	public void onCommonCraftingEvent(PlayerEvent.ItemCraftedEvent event) {
		if (event.player == null)
			return;
		if (event.player.world.isRemote)
			return;
		ItemStack tool = event.crafting;
		NBTTagCompound tag = tool.getTagCompound();
		if (tag == null || !tag.hasKey(Tags.TOOL_DATA))
			return;
		NBTTagCompound statsBase = (NBTTagCompound) tag.getTag(Tags.TOOL_DATA_ORIG);
		double requiredSkill = 0;
		PlayerSkills sensitiveSkill = PlayerSkills.WEAPONSMITH;
		if (tool.getItem() instanceof ProjectileLauncherCore) {
			sensitiveSkill = PlayerSkills.MECHANICS;
			requiredSkill = this.getRangedToolRequiredSkill(statsBase);
		} else if (tool.getItem() instanceof TinkerToolCore) {
			requiredSkill = this.getMeleeToolRequiredSkill(statsBase);
		} else {
			return;
		}
		sensitiveSkill.raiseSkill(event.player, Math.max(requiredSkill, 0d) + 1.0d);
	}

	@SubscribeEvent
	public void onTinkerCraftingEvent(TinkerCraftingEvent event) {
		if (event.getPlayer() == null)
			return;
		ItemStack tool = event.getItemStack();
		NBTTagCompound tag = tool.getTagCompound();
		if (tag == null || !tag.hasKey(Tags.TOOL_DATA))
			return;
		NBTTagCompound stats = (NBTTagCompound) tag.getTag(Tags.TOOL_DATA);
		NBTTagCompound statsBase = (NBTTagCompound) tag.getTag(Tags.TOOL_DATA_ORIG);
		PlayerSkills sensitiveSkill = PlayerSkills.WEAPONSMITH;
		double skillValue = 0;
		double requiredSkill = 0;
		if (tool.getItem() instanceof ProjectileLauncherCore) {
			sensitiveSkill = PlayerSkills.MECHANICS;
			requiredSkill = this.getRangedToolRequiredSkill(statsBase);
		} else if (tool.getItem() instanceof TinkerToolCore) {
			if (!sensitiveSkill.enabled)
				return;
			requiredSkill = this.getMeleeToolRequiredSkill(statsBase);
		}
		if (!sensitiveSkill.enabled)
			return;
		skillValue = sensitiveSkill.getFullSkillValue(event.getPlayer());
		skillValue -= requiredSkill;
		tag.setInteger("quality", (int) skillValue);
		int durability = statsBase.getInteger(Tags.DURABILITY);
		durability = PlayerUtils.modifyIntValueBySkill(false, durability, skillValue);
		stats.setInteger(Tags.DURABILITY, durability);
		if (sensitiveSkill == PlayerSkills.MECHANICS)
			this.handleProjectileLauncherTags(stats, statsBase, skillValue);
		if (sensitiveSkill == PlayerSkills.WEAPONSMITH)
			this.handleToolsTags(stats, statsBase, skillValue);
	}

	private void handleProjectileLauncherTags(NBTTagCompound tag, NBTTagCompound tagBase, double skillValue) {
		float drawSpeed = tagBase.getFloat(Tags.DRAWSPEED);
		float range = tagBase.getFloat(Tags.RANGE);
		float damage = tagBase.getFloat(Tags.PROJECTILE_BONUS_DAMAGE);
		drawSpeed = (float) PlayerUtils.modifyDoubleValueBySkill(false, drawSpeed, skillValue);
		tag.setFloat(Tags.DRAWSPEED, drawSpeed);
		range = (float) PlayerUtils.modifyDoubleValueBySkill(false, range, skillValue);
		tag.setFloat(Tags.RANGE, range);
		damage = (float) PlayerUtils.modifyDoubleValueBySkill(damage < 0, damage, skillValue);
		tag.setFloat(Tags.PROJECTILE_BONUS_DAMAGE, damage);
	}

	private void handleToolsTags(NBTTagCompound tag, NBTTagCompound tagBase, double skillValue) {
		float attack = tagBase.getFloat(Tags.ATTACK);
		float speed = tagBase.getFloat(Tags.MININGSPEED);
		float attackSpeedMultiplier = tagBase.getFloat(Tags.ATTACKSPEEDMULTIPLIER);
		attack = (float) PlayerUtils.modifyDoubleValueBySkill(false, attack, skillValue);
		tag.setFloat(Tags.ATTACK, attack);
		speed = (float) PlayerUtils.modifyDoubleValueBySkill(false, speed, skillValue);
		tag.setFloat(Tags.MININGSPEED, speed);
		tagBase.setFloat(Tags.MININGSPEED, speed);
		attackSpeedMultiplier = (float) PlayerUtils.modifyDoubleValueBySkill(false, attackSpeedMultiplier, skillValue);
		tag.setFloat(Tags.ATTACKSPEEDMULTIPLIER, attackSpeedMultiplier);
	}

	private double getMeleeToolRequiredSkill(NBTTagCompound tagBase) {
		int durability = tagBase.getInteger(Tags.DURABILITY);
		int harvestLevel = tagBase.getInteger(Tags.HARVESTLEVEL);
		float attack = tagBase.getFloat(Tags.ATTACK);
		float speed = tagBase.getFloat(Tags.MININGSPEED);
		float attackSpeedMultiplier = tagBase.getFloat(Tags.ATTACKSPEEDMULTIPLIER);
		double requiredSkill = durability / 300d - 5.0d;
		requiredSkill += attack;
		requiredSkill += speed;
		requiredSkill += harvestLevel;
		requiredSkill += attackSpeedMultiplier;
		return requiredSkill;
	}

	private double getRangedToolRequiredSkill(NBTTagCompound tagBase) {
		int durability = tagBase.getInteger(Tags.DURABILITY);
		float drawSpeed = tagBase.getFloat(Tags.DRAWSPEED);
		float range = tagBase.getFloat(Tags.RANGE);
		float damage = tagBase.getFloat(Tags.PROJECTILE_BONUS_DAMAGE);
		double requiredSkill = durability / 300d - 5.0d;
		requiredSkill += drawSpeed;
		requiredSkill += range;
		requiredSkill += damage;
		return requiredSkill;
	}
}
