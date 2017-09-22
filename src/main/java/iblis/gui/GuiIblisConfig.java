package iblis.gui;

import iblis.IblisMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class GuiIblisConfig  extends GuiConfig {

    public GuiIblisConfig(GuiScreen parent) {
        super(parent, 
        		new ConfigElement(IblisMod.config.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), 
        		IblisMod.MODID, 
        		false,
                false, 
                GuiConfig.getAbridgedConfigPath(IblisMod.config.configuration.toString()));
    }
}
