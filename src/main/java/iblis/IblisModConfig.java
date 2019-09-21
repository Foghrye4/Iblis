package iblis;

import iblis.crafting.CraftingHandler;
import iblis.init.IblisItems;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisModConfig {
	
	public static final String CATEGORY_SKILLS = "skills";
	public static final String CATEGORY_SKILLS_CAP = "skills_cap";
	public static final String CATEGORY_CHARACTERISTICS_DISABLING = "characteristics_disabling";
	public static final String CATEGORY_CHARACTERISTICS_START_LEVEL = "characteristics_start_level";
	public static final String CATEGORY_CHARACTERISTICS_POINTS_PER_LEVEL = "characteristics_points_per_level";
	public static final String CATEGORY_CHARACTERISTICS_CAP = "characteristics_cap";
	
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
			skill.enabled = configuration.getBoolean(skill.name().toLowerCase(),
					CATEGORY_SKILLS, true, "Turn off to disable skill. Disabled skills always counts as equal to zero.");
			skill.cap = configuration.getFloat(skill.name().toLowerCase(),
					CATEGORY_SKILLS_CAP, 1000.0f, 0f, 1000.0f, "Max skill level.");
		}
		for(PlayerCharacteristics ch:PlayerCharacteristics.values()){
			ch.enabled = configuration.getBoolean(ch.name().toLowerCase(),
					CATEGORY_CHARACTERISTICS_DISABLING, true, "Turn off to disable characteristic. Disabled characteristics cannot be raised.");
			ch.startLevel = configuration.getFloat(ch.name().toLowerCase(),
					CATEGORY_CHARACTERISTICS_START_LEVEL, (float)ch.defaultStartLevel, 0f, 100f, "Start level of characteristic.");
			ch.pointsPerLevel = configuration.getFloat(ch.name().toLowerCase(),
					CATEGORY_CHARACTERISTICS_POINTS_PER_LEVEL, (float)ch.defaultPointsPerLevel, 0f, 100f, "Amount of points added to characteristic every level.");
			ch.cap = configuration.getFloat(ch.name().toLowerCase(),
					CATEGORY_CHARACTERISTICS_CAP, 1000.0f, 0f, 1000.0f, "Max value of characteristic.");
		}
		IblisMod.deathPenaltyHandler.spawnPlayerZombie = configuration.getBoolean("spawn_player_zombie",
				Configuration.CATEGORY_GENERAL, false, "Spawn player zombie on players death with all inventory.");
		IblisMod.deathPenaltyHandler.noDeathPenalty = configuration.getBoolean("no_death_penalty",
				Configuration.CATEGORY_GENERAL, true, "No death penalty to all skills and characteristics.");
		IblisMod.eventHandler.noIncreasedMobSeekRange = configuration.getBoolean("no_increased_mob_seek_range",
				Configuration.CATEGORY_GENERAL, false, "If true mobs will use regular AI. If false - mobs will have a chance to spot player at any distance.");
		IblisMod.eventHandler.mobReactOnlyOnShooting = configuration.getBoolean("mob_react_only_on_shooting",
				Configuration.CATEGORY_GENERAL, false, "If 'no_increased_mob_seek_range' is false and this option is true only shooting with shotgun will trigger increased mob range mechanics.");
		IblisItems.MEDKIT.instantHealing = configuration.getBoolean("medkit_instant_healing",
				Configuration.CATEGORY_GENERAL, false, "Medkit heal instantly on use.");
		IblisMod.proxy.setToggleSprintByKeyBindSprint(configuration.getBoolean("toggle_sprint_by_sprint_button",
				Configuration.CATEGORY_GENERAL, false, "If set to true sprint button will toggle sprint instead of setting it."));
		IblisMod.proxy.setHPRender(configuration.getBoolean("render_hp_bar",
				Configuration.CATEGORY_GENERAL, false, "If true HP bar will be rendered by Iblis."));
		CraftingHandler.disableMedkitRecipe = configuration.getBoolean("disable_medkit_recipe",
				Configuration.CATEGORY_GENERAL, false, "If true medkit will not be craftable. Effective after game reload.");
		CraftingHandler.disableShotgunRecipes = configuration.getBoolean("disable_shotgun_recipe",
				Configuration.CATEGORY_GENERAL, false, "If true shotgun and bullets will not be craftable. Effective after game reload.");
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}
