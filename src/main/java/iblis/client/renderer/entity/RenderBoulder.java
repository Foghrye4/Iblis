package iblis.client.renderer.entity;

import iblis.entity.EntityBoulder;
import iblis.init.IblisItems;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class RenderBoulder extends Render<EntityBoulder> {

	private final RenderItem itemRenderer;
	private final ItemStack itemstack = new ItemStack(IblisItems.BOULDER);
	private int frame = 0;
	
	public RenderBoulder(RenderManager renderManagerIn, RenderItem itemRendererIn) {
		super(renderManagerIn);
		this.itemRenderer = itemRendererIn;
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	@Override
	public void doRender(EntityBoulder entity, double x, double y, double z, float entityYaw, float partialTicks) {
		boolean flag = false;
		frame++;
		if (this.bindEntityTexture(entity)) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
			flag = true;
		}
		GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world,
				(EntityLivingBase) null);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.25d, 0.25d, 0.25d);
		GlStateManager.rotate(frame, (float)entity.motionX, (float)entity.motionY, (float)entity.motionZ);
		IBakedModel transformedModel = ForgeHooksClient
				.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
		this.itemRenderer.renderItem(itemstack, transformedModel);
		GlStateManager.popMatrix();

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		this.bindEntityTexture(entity);
		if (flag) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoulder entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

}
