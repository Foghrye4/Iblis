package iblis.event;

import iblis.IblisMod;
import iblis.init.IblisParticles;
import iblis.util.HeadShotHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisEventHandler {

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		EntityLivingBase living = event.getEntityLiving();
		if (living.world.isRemote)
			return;
		Entity projectile = event.getSource().getImmediateSource();
		if (projectile != null) {
			Vec3d start = new Vec3d(projectile.posX, projectile.posY, projectile.posZ);
			Vec3d end = new Vec3d(projectile.posX + projectile.motionX, projectile.posY + projectile.motionY,
					projectile.posZ + projectile.motionZ);
			if (HeadShotHandler.traceHeadShot(living, start, end) != null) {
				if (living.getHealth() < damage && living instanceof EntitySlime
						&& ((EntitySlime) living).getSlimeSize() > 1) {
					((EntitySlime) living).setSlimeSize(0, false);
				}
				IblisMod.network.spawnCustomParticle(living.world, start, new Vec3d(0d, 0.2d, 0d),
						IblisParticles.HEADSHOT);
				damage *= 4;
				event.setAmount(damage);
			}
		}
	}
}
