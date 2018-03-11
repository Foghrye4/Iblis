package iblis.init;

import iblis.IblisMod;
import iblis.player.SharedIblisAttributes;
import iblis.potion.IblisPotion;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;

public class IblisPotions {
	public static Potion AWARENESS;
	public static Potion OVERHEATING;
	
	public static void init(){
		AWARENESS = new IblisPotion(false, 0);
		OVERHEATING = new IblisPotion(true, 0);
		AWARENESS.setPotionName("awareness")
			.registerPotionAttributeModifier(SharedMonsterAttributes.FOLLOW_RANGE, "A111A5E-17EE5-AA0D1F1E7-F01-1770B5", 1, 0)
			.setRegistryName(IblisMod.MODID, "awareness");
		OVERHEATING.setPotionName("overheating")
			.registerPotionAttributeModifier(SharedIblisAttributes.FIRE_DAMAGE_REDUCTION, "A111A5E-17EE5-AA0D1F1E7-F01-1770B5", -0.05, 0)
			.setRegistryName(IblisMod.MODID, "overheating");
		registerPotion(AWARENESS);
		registerPotion(OVERHEATING);
	}
	
	private static void registerPotion(Potion item) {
		RegistryEventHandler.potions.add(item);
	}
}
