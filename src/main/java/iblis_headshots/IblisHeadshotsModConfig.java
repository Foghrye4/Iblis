package iblis_headshots;

import iblis_headshots.event.IblisHeadshotsEventHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisHeadshotsModConfig {
	
	public IblisHeadshotsModConfig(Configuration configuration) {
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
		if (eventArgs.getModID().equals(IblisHeadshotsMod.MODID)) {
			IblisHeadshotsMod.config.syncConfig();
		}
	}

	void syncConfig() {
		IblisHeadshotsMod.proxy.headshotParticleSize = configuration.getFloat("headshot_particle_size",
				Configuration.CATEGORY_GENERAL, 10.0f, 0.0f, 1e6f, "Size of headshot particle");
		IblisHeadshotsMod.proxy.headshotParticleType = configuration.getInt("headshot_particle_type",
				Configuration.CATEGORY_GENERAL,1, 0, 3, "0 - no particle, 1 - skull, 2 - aim symbol, 3 - star");
		IblisHeadshotsEventHandler.nonProjectileHeadshotMinDistanceSq = configuration.getFloat("non_projectile_headshot_min_distance",
				Configuration.CATEGORY_GENERAL, 16.0f, 0.0f, 1e6f, "Minimal distance at which non-projectile attack count as headshot. Affect Flans mod weapons as well.");
		IblisHeadshotsEventHandler.nonProjectileHeadshotMinDistanceSq *=IblisHeadshotsEventHandler.nonProjectileHeadshotMinDistanceSq;
		IblisHeadshotsEventHandler.damageMultiplier = configuration.getFloat("headshot_damage_mutiplier",
				Configuration.CATEGORY_GENERAL, 4.0f, 0.0f, 1e6f, "Multiplier of damage caused by headshot.");
		IblisHeadshotsEventHandler.damageToItemMultiplier = configuration.getFloat("headgear_damage_mutiplier",
				Configuration.CATEGORY_GENERAL, 4.0f, 0.0f, 1e6f, "Multiplier of damage to headgear item on headshot.");
		IblisHeadshotsEventHandler.missMultiplier = configuration.getFloat("bodyshot_damage_mutiplier",
				Configuration.CATEGORY_GENERAL, 1.0f, 0.0f, 1e6f, "Multiplier of damage caused by regular shot (in everything except head).");
		IblisHeadshotsEventHandler.playersHaveNoHeads = configuration.getBoolean("players_have_no_heads",
				Configuration.CATEGORY_GENERAL, false, "If true, players will not recieve headshots");
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}