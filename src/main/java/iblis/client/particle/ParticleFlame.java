package iblis.client.particle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFlame extends ParticleSimpleAnimated {
	private static final ResourceLocation TEXTURE = new ResourceLocation("iblis:textures/particle/particles.png");
	private final TextureManager textureManager;
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F)
			.addElement(DefaultVertexFormats.TEX_2F)
			.addElement(DefaultVertexFormats.COLOR_4UB)
			.addElement(DefaultVertexFormats.TEX_2S);
	
	public ParticleFlame(TextureManager textureManagerIn, World worldIn, double x, double y, double z, double mx, double my, double mz, float yAccelIn) {
		super(worldIn, x, y, z, 0, 16, yAccelIn);
		this.textureManager = textureManagerIn;
		this.multipleParticleScaleBy(0.4f);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
        this.particleTextureIndexY = 10;
        this.canCollide = false;
	}
	
	@Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
		GL11.glDepthMask(false);
	    GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		this.textureManager.bindTexture(TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, VERTEX_FORMAT);
    	super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		tessellator.draw();
    	GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }
	
	@Override
    public void setParticleTextureIndex(int particleTextureIndex)
    {
        this.particleTextureIndexX = particleTextureIndex % 16;
    }
	
	public int getFXLayer() {
		return 3;
	}
	
	public int getBrightnessForRender(float p_189214_1_) {
		return 15 << 16 | 15;
	}
}