package iblis_headshots;

import javax.annotation.Nonnull;

import iblis_headshots.client.ItemTooltipEventHandler;
import iblis_headshots.client.particle.ParticleHeadshot;
import iblis_headshots.init.IblisParticles;
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
		OBJLoader.INSTANCE.addDomain(IblisHeadshotsMod.MODID);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
	}

	@Override
	public void init() {
	}
	

	public void spawnParticle(@Nonnull IblisParticles particle, double posX, double posY, double posZ, double xSpeedIn,
			double ySpeedIn, double zSpeedIn) {
		if (this.headshotParticleType == 0)
			return;
		@Nonnull
		Particle entityParticle;
		Minecraft mc = Minecraft.getMinecraft();
		Entity renderViewEntity = mc.getRenderViewEntity();
		double distance = renderViewEntity.getDistanceSq(posX, posY, posZ);
		distance = MathHelper.sqrt(distance);
		entityParticle = new ParticleHeadshot(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn,
				ySpeedIn, zSpeedIn, (float) (distance / 1000d + 0.01d)*headshotParticleSize, headshotParticleType);
		mc.effectRenderer.addEffect(entityParticle);
	}
}
