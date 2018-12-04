package iblis.event;

import iblis.IblisMod;
import iblis.advacements.criterion.HeadshotTrigger;
import iblis.init.IblisParticles;
import iblis.util.HeadShotHandler;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisEventHandler {

	public static float damageMultiplier = 4.0f;
	public static float missMultiplier = 1.0f;
	
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
				damage *= damageMultiplier;
				event.setAmount(damage);
				
				Entity shooter = event.getSource().getTrueSource();
				if (shooter instanceof EntityPlayerMP && !(living  instanceof EntityPlayerMP)) {
					HeadshotTrigger.instance.trigger((EntityPlayerMP) shooter, living);
				} else if (living instanceof EntityPlayerMP) {
					HeadshotTrigger.instance.trigger((EntityPlayerMP) living, living);
				}
			}
			else {
				damage *= missMultiplier;
				event.setAmount(damage);
			}
		}
	}
}
