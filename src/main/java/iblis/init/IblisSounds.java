package iblis.init;

import iblis.IblisMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class IblisSounds {
	public static SoundEvent book_reading;
	public static SoundEvent book_closing;
	public static SoundEvent shotgun_ammo_loading;
	public static SoundEvent shoot;
	public static SoundEvent shotgun_hammer_click;
	public static SoundEvent shotgun_hammer_cock;
	public static SoundEvent shotgun_charging;
	
	public static void register() {
		book_reading = registerSound("book_reading");
		book_closing = registerSound("book_closing");
		shotgun_ammo_loading = registerSound("shotgun_ammo_loading");
		shoot = registerSound("shoot");
		shotgun_hammer_click = registerSound("shotgun_hammer_click");
		shotgun_hammer_cock = registerSound("shotgun_hammer_cock");
		shotgun_charging = registerSound("shotgun_charging");
	}

	private static SoundEvent registerSound(String soundNameIn) {
		ResourceLocation sound = new ResourceLocation(IblisMod.MODID, soundNameIn);
		SoundEvent soundEvent = new SoundEvent(sound).setRegistryName(sound);
		RegistryEventHandler.sounds.add(soundEvent);
		return soundEvent;
	}
}
