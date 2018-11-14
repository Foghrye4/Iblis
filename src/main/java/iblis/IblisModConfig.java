package iblis;

import iblis.event.IblisEventHandler;
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
		IblisEventHandler.damageMultiplier = configuration.getFloat("headshot_damage_mutiplier",
				Configuration.CATEGORY_GENERAL, 4.0f, 0.0f, 1e6f, "Multiplier of damage caused by headshot.");
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}
}