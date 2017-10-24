package iblis.init;

import iblis.IblisMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class IblisSounds {
	public static SoundEvent book_reading;
	public static SoundEvent book_closing;
	public static SoundEvent shotgun_ammo_loading;
	public static SoundEvent shoot;
	public static SoundEvent shotgun_hammer_click;
	public static SoundEvent shotgun_hammer_cock;
	public static SoundEvent shotgun_charging;
	public static SoundEvent opening_medkit;
	public static SoundEvent closing_medkit;
	public static SoundEvent full_bottle_shaking;
	public static SoundEvent scissors_clicking;
	public static SoundEvent tearing_bandage;
	public static SoundEvent boulder_impact;
	public static SoundEvent knife_impact;
	public static SoundEvent knife_impact_stone;
	public static SoundEvent knife_fall;
	
	public static void register() {
		book_reading = registerSound("book_reading");
		book_closing = registerSound("book_closing");
		shotgun_ammo_loading = registerSound("shotgun_ammo_loading");
		shoot = registerSound("shoot");
		shotgun_hammer_click = registerSound("shotgun_hammer_click");
		shotgun_hammer_cock = registerSound("shotgun_hammer_cock");
		shotgun_charging = registerSound("shotgun_charging");
		opening_medkit = registerSound("opening_medkit");
		closing_medkit = registerSound("closing_medkit");
		full_bottle_shaking = registerSound("full_bottle_shaking");
		scissors_clicking = registerSound("scissors_clicking");
		tearing_bandage = registerSound("tearing_bandage");
		boulder_impact = registerSound("boulder_impact");
		knife_impact = registerSound("knife_impact");
		knife_impact_stone = registerSound("knife_impact_stone");
		knife_fall = registerSound("knife_fall");
	}

	private static SoundEvent registerSound(String soundNameIn) {
		ResourceLocation sound = new ResourceLocation(IblisMod.MODID, soundNameIn);
		SoundEvent soundEvent = new SoundEvent(sound).setRegistryName(sound);
		RegistryEventHandler.sounds.add(soundEvent);
		return soundEvent;
	}
}
