package iblis.applecore_integration;

import iblis.player.PlayerCharacteristics;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.hunger.HungerEvent;

public class AplleCoreHungerEventHandler {
	
	@SubscribeEvent
	public void onGetMaxHunger(HungerEvent.GetMaxHunger event) {
		if (event.player == null)
			return;
		event.maxHunger = MathHelper.floor(PlayerCharacteristics.GLUTTONY.getCurrentValue(event.player));
	}
}
