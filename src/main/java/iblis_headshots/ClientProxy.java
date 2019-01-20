package iblis_headshots;

import iblis_headshots.client.ItemTooltipEventHandler;
import iblis_headshots.client.RenderEventHandler;
import iblis_headshots.client.particle.ParticleHeadshot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {

	@Override
	void load() {
		OBJLoader.INSTANCE.addDomain(IblisHeadshotsMod.MODID);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
		MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
	}

	@Override
	public void init() {
	}

	public void spawnParticle(double posX, double posY, double posZ, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, int maxAge) {
		if (this.headshotParticleType == 0)
			return;
		Minecraft mc = Minecraft.getMinecraft();
		Entity renderViewEntity = mc.getRenderViewEntity();
		double distance = renderViewEntity.getDistanceSq(posX, posY, posZ);
		distance = MathHelper.sqrt(distance);
		Particle entityParticle = new ParticleHeadshot(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn,
				ySpeedIn, zSpeedIn, (float) (distance / 1000d + 0.01d)*headshotParticleSize, headshotParticleType, maxAge);
		mc.effectRenderer.addEffect(entityParticle);
	}
}
