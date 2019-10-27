package iblis.client.gui;

import java.util.ArrayList;
import java.util.List;

import iblis.IblisMod;
import iblis.IblisModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiIblisConfig extends GuiConfig {

	public GuiIblisConfig(GuiScreen parent) {
		super(parent,
				getConfigElements(),
				IblisMod.MODID, false, false,
				GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyCategoryElement("generalConfig", "iblis.generalConfig", GeneralEntry.class));
		list.add(new DummyCategoryElement("characteristicsDisabling", "iblis.characteristicsDisablingConfig", CharacteristicsDisablingEntry.class));
		list.add(new DummyCategoryElement("characteristicsStartLevel", "iblis.characteristicsStartLevelConfig", CharacteristicsStartLevelEntry.class));
		list.add(new DummyCategoryElement("characteristicsPointsPerLevel", "iblis.characteristicsPointsPerLevelConfig", CharacteristicsPointsPerLevelEntry.class));
		list.add(new DummyCategoryElement("characteristicsCapConfig", "iblis.characteristicsCapConfig", CharacteristicsCapEntry.class));
		list.add(new DummyCategoryElement("skillsConfig", "iblis.skillsConfig", SkillsEntry.class));
		list.add(new DummyCategoryElement("skillsCapConfig", "iblis.skillsCapConfig", SkillsCapEntry.class));
		list.add(new DummyCategoryElement("skillsXPConfig", "iblis.skillsXPConfig", SkillsXPEntry.class));
		return list;
	}

	public static class GeneralEntry extends CategoryEntry {
		public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(Configuration.CATEGORY_GENERAL))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}
	
	public static class CharacteristicsDisablingEntry extends CategoryEntry {
		public CharacteristicsDisablingEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_CHARACTERISTICS_DISABLING))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}

	public static class CharacteristicsStartLevelEntry extends CategoryEntry {
		public CharacteristicsStartLevelEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_CHARACTERISTICS_START_LEVEL))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}

	public static class CharacteristicsPointsPerLevelEntry extends CategoryEntry {
		public CharacteristicsPointsPerLevelEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_CHARACTERISTICS_POINTS_PER_LEVEL))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}
	
	public static class CharacteristicsCapEntry extends CategoryEntry {
		public CharacteristicsCapEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_CHARACTERISTICS_CAP))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}

	public static class SkillsEntry extends CategoryEntry {
		public SkillsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_SKILLS))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}

	public static class SkillsCapEntry extends CategoryEntry {
		public SkillsCapEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_SKILLS_CAP))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}

	public static class SkillsXPEntry extends CategoryEntry {
		public SkillsXPEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen,
					new ConfigElement(IblisMod.config.configuration.getCategory(IblisModConfig.CATEGORY_SKILLS_XP))
							.getChildElements(),
					IblisMod.MODID, false, false,
					GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
		}
	}
}
