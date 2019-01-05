package iblis;

import iblis.init.IblisItems;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisModConfig {
	
	public IblisModConfig(Configuration configuration) {
		loadConfig(configuration);
		syncConfig();
	}

	public static String getNicelyFormattedName(String name) {
		StringBuffer out = new StringBuffer();
		char char_ = '_';
		char prevchar = 0;
		for (char c : name.toCharArray()) {
			if (c != char_ && prevchar != char_) {
				out.append(String.valueOf(c).toLowerCase());
			} else if (c != char_) {
				out.append(String.valueOf(c));
			}
			prevchar = c;
		}
		return out.toString();
	}

	public Configuration configuration;

	void loadConfig(Configuration configuration) {
		this.configuration = configuration;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(IblisMod.MODID)) {
			IblisMod.config.syncConfig();
		}
	}

	void syncConfig() {
		for(PlayerSkills skill:PlayerSkills.values()){
			skill.enabled = configuration.getBoolean("enable_"+skill.name().toLowerCase()+"_skill",
					Configuration.CATEGORY_GENERAL, true, "Turn off to disable skill. Disabled skills always counts as equal to zero.");
		}
		for(PlayerCharacteristics ch:PlayerCharacteristics.values()){
			ch.enabled = configuration.getBoolean("enable_"+ch.name().toLowerCase()+"_characteristic",
					Configuration.CATEGORY_GENERAL, true, "Turn off to disable characteristic. Disabled characteristics cannot be raised.");
			ch.startLevel = configuration.getFloat(ch.name().toLowerCase()+"_start_level",
					Configuration.CATEGORY_GENERAL, (float)ch.defaultStartLevel, 0f, 100f, "Start level of characteristic.");
			ch.pointsPerLevel = configuration.getFloat(ch.name().toLowerCase()+"_point_per_level",
					Configuration.CATEGORY_GENERAL, (float)ch.defaultPointsPerLevel, 0f, 100f, "Amount of points added to characteristic every level.");
		}
		IblisMod.eventHandler.spawnPlayerZombie = configuration.getBoolean("spawn_player_zombie",
				Configuration.CATEGORY_GENERAL, true, "Spawn player zombie on players death with all inventory.");
		IblisMod.eventHandler.noDeathPenalty = configuration.getBoolean("no_death_penalty",
				Configuration.CATEGORY_GENERAL, false, "No death penalty to all skills and characteristics.");
		IblisMod.eventHandler.noIncreasedMobSeekRange = configuration.getBoolean("no_increased_mob_seek_range",
				Configuration.CATEGORY_GENERAL, false, "If true mobs will use regular AI. If false - mobs will have a chance to spot player at any distance.");
		IblisItems.MEDKIT.instantHealing = configuration.getBoolean("medkit_instant_healing",
				Configuration.CATEGORY_GENERAL, false, "Medkit heal instantly on use.");
		IblisMod.proxy.setToggleSprintByKeyBindSprint(configuration.getBoolean("toggle_sprint_by_sprint_button",
				Configuration.CATEGORY_GENERAL, false, "If set to true sprint button will toggle sprint instead of setting it."));
		IblisMod.proxy.setHPRender(configuration.getBoolean("render_hp_bar",
				Configuration.CATEGORY_GENERAL, false, "If true HP bar will be rendered by Iblis."));
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}
