package iblis_headshots.init;

import java.lang.reflect.Method;

import iblis_headshots.advacements.criterion.HeadshotTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class IblisHeadshotsAdvancements {

	public static void register() {
		Method method;
		try {
			method = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a",
					ICriterionTrigger.class);
			method.setAccessible(true);
			method.invoke(null, new HeadshotTrigger());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
