package iblis.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleDecal extends Particle {
	private static final ResourceLocation TEXTURE = new ResourceLocation("iblis:textures/particle/particles.png");
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F)
			.addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB)
			.addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B)
			.addElement(DefaultVertexFormats.PADDING_1B);
	/** The Rendering Engine. */
	private final TextureManager textureManager;
	public final EnumFacing faceDirection;
	private final static double FACE_OFFSET_START = 0.001;
	private final static double FACE_OFFSET = 0.0001;
	private final double x1;
	private final double x2;
	private final double y1;
	private final double y2;
	private final double z1;
	private final double z2;
	private final float cropU1;
	private final float cropU2;
	private final float cropV1;
	private final float cropV2;
	private final int spriteIndex;
	private int lightU;
	private int lightV;

	public ParticleDecal(TextureManager textureManagerIn, World worldIn, double xCoordIn, double yCoordIn,
			double zCoordIn, EnumFacing faceDirectionIn, AxisAlignedBB renderBordersIn, float sizeIn, int colorIndex, int packedLight, int layerIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
		this.textureManager = textureManagerIn;
		this.particleMaxAge = 1500;
		this.spriteIndex = worldIn.rand.nextInt(4);
		this.faceDirection = faceDirectionIn;
		this.setLight(worldIn, packedLight);
		double faceOffset = FACE_OFFSET_START + FACE_OFFSET * layerIn;
		switch (faceDirectionIn) {
		case UP:
		case DOWN:
			x1 = Math.max(xCoordIn - sizeIn / 2, renderBordersIn.minX);
			x2 = Math.min(xCoordIn + sizeIn / 2, renderBordersIn.maxX);
			z1 = Math.max(zCoordIn - sizeIn / 2, renderBordersIn.minZ);
			z2 = Math.min(zCoordIn + sizeIn / 2, renderBordersIn.maxZ);
			cropU1 = (float) Math.max(renderBordersIn.minX - xCoordIn + sizeIn / 2, 0) / sizeIn;
			cropU2 = (float) Math.max(xCoordIn + sizeIn / 2 - renderBordersIn.maxX, 0) / sizeIn;
			cropV1 = (float) Math.max(renderBordersIn.minZ - zCoordIn + sizeIn / 2, 0) / sizeIn;
			cropV2 = (float) Math.max(zCoordIn + sizeIn / 2 - renderBordersIn.maxZ, 0) / sizeIn;
			y1 = yCoordIn - faceOffset;
			y2 = yCoordIn + faceOffset;
			break;
		case NORTH:
		case SOUTH:
			x1 = Math.max(xCoordIn - sizeIn / 2, renderBordersIn.minX);
			x2 = Math.min(xCoordIn + sizeIn / 2, renderBordersIn.maxX);
			y1 = Math.max(yCoordIn - sizeIn / 2, renderBordersIn.minY);
			y2 = Math.min(yCoordIn + sizeIn / 2, renderBordersIn.maxY);
			cropU1 = (float) Math.max(renderBordersIn.minX - xCoordIn + sizeIn / 2, 0) / sizeIn;
			cropU2 = (float) Math.max(xCoordIn + sizeIn / 2 - renderBordersIn.maxX, 0) / sizeIn;
			cropV1 = (float) Math.max(renderBordersIn.minY - yCoordIn + sizeIn / 2, 0) / sizeIn;
			cropV2 = (float) Math.max(yCoordIn + sizeIn / 2 - renderBordersIn.maxY, 0) / sizeIn;
			z1 = zCoordIn - faceOffset;
			z2 = zCoordIn + faceOffset;
			break;
		case WEST: // Normal X
		case EAST:
		default:
			y1 = Math.max(yCoordIn - sizeIn / 2, renderBordersIn.minY);
			y2 = Math.min(yCoordIn + sizeIn / 2, renderBordersIn.maxY);
			z1 = Math.max(zCoordIn - sizeIn / 2, renderBordersIn.minZ);
			z2 = Math.min(zCoordIn + sizeIn / 2, renderBordersIn.maxZ);
			cropU1 = (float) Math.max(renderBordersIn.minY - yCoordIn + sizeIn / 2, 0) / sizeIn;
			cropU2 = (float) Math.max(yCoordIn + sizeIn / 2 - renderBordersIn.maxY, 0) / sizeIn;
			cropV1 = (float) Math.max(renderBordersIn.minZ - zCoordIn + sizeIn / 2, 0) / sizeIn;
			cropV2 = (float) Math.max(zCoordIn + sizeIn / 2 - renderBordersIn.maxZ, 0) / sizeIn;
			x1 = xCoordIn - faceOffset;
			x2 = xCoordIn + faceOffset;
			break;
		}
		this.setRBGColorF((colorIndex >> 16) / 255f, 
				((colorIndex >> 8) & 255) / 255f, 
				(colorIndex & 255) / 255f);
	}

	public void setLight(World worldIn, int packedLight) {
		this.particleAge = 0;
		this.lightU = packedLight & 65535;
		this.lightV = (packedLight >> 16) - (worldIn.getSkylightSubtracted() << 4);
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		this.textureManager.bindTexture(TEXTURE);
		float u0 = 0f;
		float v0 = 33f / 256f + 16f * this.spriteIndex / 256f;
		float u1 = u0 + cropU1 * 16f / 256f;
		float u2 = u0 + (1f - cropU2) * 16f / 256f;
		float v1 = v0 + cropV1 * 16f / 256f;
		float v2 = v0 + (1f - cropV2) * 16f / 256f;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();

		Particle.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		Particle.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		Particle.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
		
		this.drawBox(x1 - interpPosX, y1 - interpPosY, z1 - interpPosZ, x2 - interpPosX, y2 - interpPosY,
				z2 - interpPosZ, u1, u2, v1, v2, faceDirection);
		GlStateManager.enableLighting();
	}

	private void drawBox(double x1, double y1, double z1, double x2, double y2, double z2, double u1, double u2,
			double v1, double v2, EnumFacing faceDirectionIn) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		switch (faceDirectionIn) {
		case UP:
		case DOWN:
			// Top
			GlStateManager.glNormal3f(0, 1, 0);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x2, y1, z1).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x2, y1, z2).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y1, z2).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y1, z1).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			// Bottom
			GlStateManager.glNormal3f(0, -1, 0);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x2, y2, z2).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, -1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x2, y2, z1).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, -1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y2, z1).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, -1.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y2, z2).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, -1.0F, 0.0F).endVertex();
			tessellator.draw();
			break;
		case NORTH:
		case SOUTH:
			// North
			GlStateManager.glNormal3f(0, 0, 1);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x1, y2, z1).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, 1.0F).endVertex();
			bufferbuilder.pos(x2, y2, z1).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, 1.0F).endVertex();
			bufferbuilder.pos(x2, y1, z1).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, 1.0F).endVertex();
			bufferbuilder.pos(x1, y1, z1).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, 1.0F).endVertex();
			tessellator.draw();
			// South
			GlStateManager.glNormal3f(0, 0, -1);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x2, y2, z2).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, -1.0F).endVertex();
			bufferbuilder.pos(x1, y2, z2).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, -1.0F).endVertex();
			bufferbuilder.pos(x1, y1, z2).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, -1.0F).endVertex();
			bufferbuilder.pos(x2, y1, z2).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(0.0F, 0.0F, -1.0F).endVertex();
			tessellator.draw();
			break;
		case WEST:
		case EAST:
			// West
			GlStateManager.glNormal3f(1, 0, 0);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x1, y2, z2).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y2, z1).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y1, z1).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x1, y1, z2).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(1.0F, 0.0F, 0.0F).endVertex();
			tessellator.draw();
			// East
			GlStateManager.glNormal3f(-1, 0, 0);
			bufferbuilder.begin(7, VERTEX_FORMAT);
			bufferbuilder.pos(x2, y2, z1).tex(u2, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(-1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x2, y2, z2).tex(u2, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(-1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x2, y1, z2).tex(u1, v2)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(-1.0F, 0.0F, 0.0F).endVertex();
			bufferbuilder.pos(x2, y1, z1).tex(u1, v1)
			.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
			.lightmap(this.lightU, this.lightV)
			.normal(-1.0F, 0.0F, 0.0F).endVertex();
			tessellator.draw();
			break;
		}
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