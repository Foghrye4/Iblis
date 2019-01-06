package iblis_headshots.client.gui;

import iblis_headshots.IblisHeadshotsMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiIblisConfig  extends GuiConfig {

    public GuiIblisConfig(GuiScreen parent) {
        super(parent, 
        		new ConfigElement(IblisHeadshotsMod.config.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), 
        		IblisHeadshotsMod.MODID, 
        		false,
                false, 
                GuiConfig.getAbridgedConfigPath(IblisHeadshotsMod.config.configuration.toString()));
    }
}