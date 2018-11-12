package iblis;

import javax.annotation.Nonnull;

import iblis.client.ItemTooltipEventHandler;
import iblis.client.particle.ParticleHeadshot;
import iblis.init.IblisParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ClientProxy extends ServerProxy {


	@Override
	void load() {
		OBJLoader.INSTANCE.addDomain(IblisMod.MODID);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
	}

	@Override
	public void init() {
	}
	

	public void spawnParticle(@Nonnull IblisParticles particle, double posX, double posY, double posZ, double xSpeedIn,
			double ySpeedIn, double zSpeedIn) {
		@Nonnull
		Particle entityParticle;
		Minecraft mc = Minecraft.getMinecraft();
		Entity renderViewEntity = mc.getRenderViewEntity();
		double distance = renderViewEntity.getDistanceSq(posX, posY, posZ);
		distance = MathHelper.sqrt(distance);
		entityParticle = new ParticleHeadshot(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn,
				ySpeedIn, zSpeedIn, (float) (distance / 100d + 0.1d));
		mc.effectRenderer.addEffect(entityParticle);
	}
}
