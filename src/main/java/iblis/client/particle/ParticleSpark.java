package iblis.client.particle;

import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSpark extends ParticleSimpleAnimated {
	public ParticleSpark(World worldIn, double x, double y, double z, double mx, double my, double mz, float yAccelIn) {
		super(worldIn, x, y, z, 160, 1, yAccelIn);
		this.setAlphaF(0.9f);
		this.setColorFade(0xac0c00);
		this.setColor(0xfff2b8);
		this.multipleParticleScaleBy(0.4f);
		this.setMaxAge(particleMaxAge/2);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
	}
}