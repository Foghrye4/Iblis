package iblis.client.renderer.entity;

import iblis.entity.EntityThrowingKnife;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderThrowingKnife extends Render<EntityThrowingKnife> {

	public static final ResourceLocation TEXTURE = new ResourceLocation("iblis:textures/particle/particles.png");
	private static final double texScale = 1 / 256d;

	public RenderThrowingKnife(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityThrowingKnife entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		this.bindEntityTexture(entity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate(
				entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F,
				1.0F, 0.0F);
		if(!entity.onHardSurface)
			GlStateManager.rotate(
				entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F,
				1.0F);
		GlStateManager.enableRescaleNormal();
		if(!entity.onHardSurface)
			GlStateManager.rotate(90f, 1.0f, 0f, 0f);
		GlStateManager.scale(0.05625F, 0.05625F, 0.05625F);
		GlStateManager.translate(-4.0F, 0.0F, 0.0F);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		this.drawBlade(0, -0.6, -1, 8, 0.2, 1, 0, 28 * texScale, 16 * texScale, 23 * texScale);
		this.drawBox(-8, -0.6, -1, 0, 0.2, 1, 0, 10 * texScale, 23 * texScale, 33 * texScale);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	private void drawBlade(double x1, double y1, double z1,
			double x2, double y2, double z2, 
			double u1, double u2, double v1, double v2){
		double x15 = x1 * 0.5 + x2 * 0.5;
		double y15 = y1 * 0.5 + y2 * 0.5;
		double z15 = z1 * 0.5 + z2 * 0.5;
		double v15 = v1 * 0.5 + v2 * 0.5;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		// 1 Top right
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.2F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y15, z2).tex(u1, v2).endVertex();
		bufferbuilder.pos(x15, y15, z2).tex(u2, v2).endVertex();
		bufferbuilder.pos(x2, y15, z15).tex(u2, v15).endVertex();
		bufferbuilder.pos(x1, y2, z15).tex(u1, v15).endVertex();
		tessellator.draw();
		// 2 Top left
		GlStateManager.glNormal3f(0.0F, 1.0F, -0.2F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y2, z15).tex(u1, v15).endVertex();
		bufferbuilder.pos(x2, y15, z15).tex(u2, v15).endVertex();
		bufferbuilder.pos(x15, y15, z1).tex(u2, v1).endVertex();
		bufferbuilder.pos(x1, y15, z1).tex(u1, v1).endVertex();
		tessellator.draw();
		// 3 Bottom right
		GlStateManager.glNormal3f(0.0F, -1.0F, 0.2F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y1, z15).tex(u1, v15).endVertex();
		bufferbuilder.pos(x2, y15, z15).tex(u2, v15).endVertex();
		bufferbuilder.pos(x15, y15, z2).tex(u2, v1).endVertex();
		bufferbuilder.pos(x1, y15, z2).tex(u1, v1).endVertex();
		tessellator.draw();
		// 4 Bottom left
		GlStateManager.glNormal3f(0.05625F, 0.0F, 0.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y15, z1).tex(u1, v2).endVertex();
		bufferbuilder.pos(x15, y15, z1).tex(u2, v2).endVertex();
		bufferbuilder.pos(x2, y15, z15).tex(u2, v15).endVertex();
		bufferbuilder.pos(x1, y1, z15).tex(u1, v15).endVertex();
		tessellator.draw();
		
	}

	private void drawBox(double x1, double y1, double z1,
			double x2, double y2, double z2, 
			double u1, double u2, double v1, double v2){
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		//Top
		GlStateManager.glNormal3f(0,1,0);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x2, y1, z1).tex(u1, v1).endVertex();
		bufferbuilder.pos(x2, y1, z2).tex(u1, v2).endVertex();
		bufferbuilder.pos(x1, y1, z2).tex(u2, v2).endVertex();
		bufferbuilder.pos(x1, y1, z1).tex(u2, v1).endVertex();
		tessellator.draw();
		//Bottom
		GlStateManager.glNormal3f(0,-1 ,0);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x2, y2, z2).tex(u1, v1).endVertex();
		bufferbuilder.pos(x2, y2, z1).tex(u1, v2).endVertex();
		bufferbuilder.pos(x1, y2, z1).tex(u2, v2).endVertex();
		bufferbuilder.pos(x1, y2, z2).tex(u2, v1).endVertex();
		tessellator.draw();
		//North
		GlStateManager.glNormal3f(0,0,1);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y2, z1).tex(u1, v1).endVertex();
		bufferbuilder.pos(x2, y2, z1).tex(u1, v2).endVertex();
		bufferbuilder.pos(x2, y1, z1).tex(u2, v2).endVertex();
		bufferbuilder.pos(x1, y1, z1).tex(u2, v1).endVertex();
		tessellator.draw();
		//South
		GlStateManager.glNormal3f(0,0,-1);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x2, y2, z2).tex(u1, v1).endVertex();
		bufferbuilder.pos(x1, y2, z2).tex(u1, v2).endVertex();
		bufferbuilder.pos(x1, y1, z2).tex(u2, v2).endVertex();
		bufferbuilder.pos(x2, y1, z2).tex(u2, v1).endVertex();
		tessellator.draw();
		//West
		GlStateManager.glNormal3f(1,0,0);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x1, y2, z2).tex(u1, v1).endVertex();
		bufferbuilder.pos(x1, y2, z1).tex(u1, v2).endVertex();
		bufferbuilder.pos(x1, y1, z1).tex(u2, v2).endVertex();
		bufferbuilder.pos(x1, y1, z2).tex(u2, v1).endVertex();
		tessellator.draw();
		//East
		GlStateManager.glNormal3f(-1,0,0);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x2, y2, z1).tex(u1, v1).endVertex();
		bufferbuilder.pos(x2, y2, z2).tex(u1, v2).endVertex();
		bufferbuilder.pos(x2, y1, z2).tex(u2, v2).endVertex();
		bufferbuilder.pos(x2, y1, z1).tex(u2, v1).endVertex();
		tessellator.draw();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityThrowingKnife entity) {
		return TEXTURE;
	}

}
