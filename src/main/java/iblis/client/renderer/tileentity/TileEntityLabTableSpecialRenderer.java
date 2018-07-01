package iblis.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import iblis.IblisMod;
import iblis.tileentity.TileEntityLabTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class TileEntityLabTableSpecialRenderer extends TileEntitySpecialRenderer<TileEntityLabTable> {
	
	private static IBakedModel staticGlass;
	private static IBakedModel reactor;
	private static IBakedModel reactorOut;
	private static IBakedModel separatorOut;
	private static IBakedModel filterOut;

	public static void bakeModels() {
		staticGlass = bake("static_glass");
		reactor = bake("reactor");
		reactorOut = bake("reactor_out");
		separatorOut = bake("separator_out");
		filterOut = bake("filter_out");
	}
	
	private static IBakedModel bake(String model) {
		IModel raw;
		try {
			raw = OBJLoader.INSTANCE.loadModel(
					new ResourceLocation(IblisMod.MODID, "models/block/chemical_lab_installation_" + model + ".obj"));
			return raw.bake(raw.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void render(TileEntityLabTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		if(te.getWorld()==null)
			return;
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int light = te.getWorld().getCombinedLight(te.getPos(), 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light >> 8, light & 255);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		this.render(staticGlass);
		this.render(reactor);
		this.render(reactorOut);
		this.render(filterOut);
		this.render(separatorOut);
		tessellator.draw();
		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void render(IBakedModel model) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		for (BakedQuad quad : model.getQuads(null, null, 0L))
			LightUtil.renderQuadColor(bufferbuilder, quad, -1);
	}
}
