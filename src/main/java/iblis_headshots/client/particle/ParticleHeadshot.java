package iblis_headshots.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
public class ParticleHeadshot extends Particle {
	private static final ResourceLocation TEXTURE = new ResourceLocation("iblis_headshots:textures/particle/particles.png");
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F)
			.addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB)
			.addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B)
			.addElement(DefaultVertexFormats.PADDING_1B);
	/** The Rendering Engine. */
	private final TextureManager textureManager;
	private final float size;
	private int headshotParticleType;

	public ParticleHeadshot(TextureManager textureManagerIn, World worldIn, double xCoordIn, double yCoordIn,
			double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float sizeIn, int headshotParticleTypeIn, int particleMaxAgeIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.textureManager = textureManagerIn;
		this.size = sizeIn;
		this.particleMaxAge = particleMaxAgeIn;
		this.headshotParticleType = headshotParticleTypeIn;
        this.motionX = xSpeedIn*(Math.random() * 1.5D - 0.5D);
        this.motionY = ySpeedIn*(Math.random() * 1.5D - 0.5D);
        this.motionZ = zSpeedIn*(Math.random() * 1.5D - 0.5D);
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		this.textureManager.bindTexture(TEXTURE);
		float u1 = (headshotParticleType - 1) * 16f / 256f;
		float u2 = u1 + 16f / 256f;
		float v1 = 177f / 256f;
		float v2 = v1 + 16f / 256f;
		float f4 = 2.0F * this.size;
		float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		buffer.begin(7, VERTEX_FORMAT);
		buffer.pos((double) (f5 - rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4),
				(double) (f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double) u2, (double) v2)
				.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240)
				.normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos((double) (f5 - rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4),
				(double) (f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double) u2, (double) v1)
				.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240)
				.normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos((double) (f5 + rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4),
				(double) (f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double) u1, (double) v1)
				.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240)
				.normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos((double) (f5 + rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4),
				(double) (f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double) u1, (double) v2)
				.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240)
				.normal(0.0F, 1.0F, 0.0F).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.enableLighting();
	}

	public int getBrightnessForRender(float f) {
		return 61680;
	}

	/**
	 * Retrieve what effect layer (what texture) the particle should be rendered
	 * with. 0 for the particle sprite sheet, 1 for the main Texture atlas, and
	 * 3 for a custom texture
	 */
	public int getFXLayer() {
		return 3;
	}
}