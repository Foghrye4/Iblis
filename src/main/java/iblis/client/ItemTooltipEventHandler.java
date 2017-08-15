package iblis.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ItemTooltipEventHandler {
	
	private final static String[] qualityLevels = new String[] {
			"iblis.qualityLevel.worthless",
			"iblis.qualityLevel.trash",
			"iblis.qualityLevel.miserable",
			"iblis.qualityLevel.awful",
			"iblis.qualityLevel.bad",
			"iblis.qualityLevel.normal",
			"iblis.qualityLevel.good",
			"iblis.qualityLevel.excellent",
			"iblis.qualityLevel.marvelous",
			"iblis.qualityLevel.exceptional",
			"iblis.qualityLevel.perfect"
	};

	@SubscribeEvent
	public void onItemTooltipEvent(ItemTooltipEvent event){
		ItemStack is = event.getItemStack();
		if(is.hasTagCompound() && is.getTagCompound().hasKey("quality")) {
			int quality, qualityRaw = is.getTagCompound().getInteger("quality");
			quality = qualityRaw;
			if(quality<-5)
				quality = -5;
			if(quality>5)
				quality = 5;
			String qualityLevel = qualityLevels[quality+5];
			event.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("iblis.quality", I18n.format(qualityLevel), qualityRaw));
		}
	}
}
