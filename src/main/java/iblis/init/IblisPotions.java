package iblis.init;

import iblis.IblisMod;
import iblis.potion.PotionAwareness;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class IblisPotions {
	public static Potion AWARENESS;
	
	public static void init(){
		AWARENESS = new PotionAwareness(false, 0);
		AWARENESS.setPotionName("awareness")
			.registerPotionAttributeModifier(SharedMonsterAttributes.FOLLOW_RANGE, "A111A5E-17EE5-AA0D1F1E7-F01-1770B5", 1, 0)
			.setRegistryName(IblisMod.MODID, "awareness");
		registerPotion(AWARENESS);
	}
	
	private static void registerPotion(Potion item) {
		RegistryEventHandler.potions.add(item);
	}
}
