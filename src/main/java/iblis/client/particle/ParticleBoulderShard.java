package iblis.client.particle;

import iblis.init.IblisItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBoulderShard extends Particle {

	private final ItemStack itemstack = new ItemStack(IblisItems.BOULDER);
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F)
			.addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB)
			.addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B)
			.addElement(DefaultVertexFormats.PADDING_1B);
	/** The Rendering Engine. */
	private final TextureManager textureManager;
	private final float size;
	private RenderItem itemRenderer;

	public ParticleBoulderShard(TextureManager textureManagerIn, World worldIn, double xCoordIn, double yCoordIn,
			double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float sizeIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.textureManager = textureManagerIn;
		this.size = sizeIn;
		this.particleMaxAge = 15;
		this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
		this.particleGravity = 10;
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (this.particleAge <= 15) {
			this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
			float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
			float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translate(f5, f6, f7);
			GlStateManager.scale(0.1d, 0.1d, 0.1d);
			GlStateManager.rotate(this.world.rand.nextInt(360), (float)entity.motionX, (float)entity.motionY, (float)entity.motionZ);
			IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world,
					(EntityLivingBase) null);
			IBakedModel transformedModel = ForgeHooksClient
					.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
			this.itemRenderer.renderItem(itemstack, transformedModel);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}
	}
	
	public int getFXLayer() {
		return 3;
	}
}