package iblis.client.particle;


import iblis.IblisMod;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSliver extends Particle {

	/** The Rendering Engine. */
	private final TextureManager textureManager;
	private static IBakedModel[] sliver = new IBakedModel[3];
	private final IBakedModel model;
	private float rotationYaw;
	private float rotationPitch;

	public static void bakeModels() {
		try {
			IModel sliver1raw = OBJLoader.INSTANCE.loadModel(new ResourceLocation(IblisMod.MODID, "models/item/sliver_1.obj"));
			IModel sliver2raw = OBJLoader.INSTANCE.loadModel(new ResourceLocation(IblisMod.MODID, "models/item/sliver_2.obj"));
			IModel sliver3raw = OBJLoader.INSTANCE.loadModel(new ResourceLocation(IblisMod.MODID, "models/item/sliver_3.obj"));
			sliver[0] = sliver1raw.bake(sliver1raw.getDefaultState(), DefaultVertexFormats.ITEM,
					ModelLoader.defaultTextureGetter());
			sliver[1] = sliver2raw.bake(sliver2raw.getDefaultState(), DefaultVertexFormats.ITEM,
					ModelLoader.defaultTextureGetter());
			sliver[2] = sliver3raw.bake(sliver3raw.getDefaultState(), DefaultVertexFormats.ITEM,
					ModelLoader.defaultTextureGetter());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ParticleSliver(TextureManager textureManagerIn, World worldIn, double xCoordIn, double yCoordIn,
			double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float sizeIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.textureManager = textureManagerIn;
		this.particleMaxAge = 15;
		this.particleGravity = 10;
		this.model = sliver[worldIn.rand.nextInt(sliver.length)];
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
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
			GlStateManager.rotate(rotationYaw - 90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(rotationPitch, 0.0F, 0.0F, 1.0F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
			for (BakedQuad quad : model.getQuads(null, null, 0L))
				LightUtil.renderQuadColor(bufferbuilder, quad, -1);
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}
	}

	public int getFXLayer() {
		return 3;
	}
}