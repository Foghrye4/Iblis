package iblis.util;

import iblis.item.ItemFirearmsBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;

public class ModIntegrationUtil {
	public static boolean isArrow(Entity entity) {
		if (!(entity instanceof EntityArrow))
			return false;
		if (entity instanceof EntityTippedArrow)
			return true;
		else if (entity instanceof EntitySpectralArrow)
			return true;

		if (Loader.isModLoaded("tconstruct")) {
			if (entity instanceof slimeknights.tconstruct.tools.common.entity.EntityArrow)
				return true;
		}
		return false;
	}
	
	public static boolean isCustomModBolt(Entity entity) {
		if (!(entity instanceof EntityArrow))
			return false;
		if (Loader.isModLoaded("tconstruct")) {
			if (entity instanceof slimeknights.tconstruct.tools.common.entity.EntityBolt)
				return true;
		}
		return false;
	}
	
	public static boolean isCustomModThrowable(Entity entity) {
		if (!(entity instanceof EntityArrow))
			return false;
		if (Loader.isModLoaded("tconstruct")) {
			if (entity instanceof slimeknights.tconstruct.tools.common.entity.EntityShuriken)
				return true;
		}
		return false;
	}
	
	public static boolean shouldShowAimFrame(Item item, EntityPlayer player) {
		if(item instanceof ItemFirearmsBase)
			return true;
		if (Loader.isModLoaded("tconstruct")) {
			if (!player.isHandActive() && item instanceof slimeknights.tconstruct.tools.ranged.item.CrossBow)
				return true;
		}
		return false;
	}

}
