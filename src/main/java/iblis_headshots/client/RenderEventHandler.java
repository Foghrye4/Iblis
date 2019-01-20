package iblis_headshots.client;

import java.util.List;

import iblis_headshots.util.HeadShotHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEventHandler {

	@SubscribeEvent
	public void onWorldRender(RenderWorldLastEvent event) {
		if(!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox())
			return;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null)
			return;
        double renderPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)event.getPartialTicks();
        double renderPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)event.getPartialTicks();
        double renderPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)event.getPartialTicks();
        World world = player.world;
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player,  player.getEntityBoundingBox().grow(6.0D));
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        for (Entity entity : entities)
        {
        	if(!(entity instanceof EntityLiving))
        		continue;
        	AxisAlignedBB aabb = HeadShotHandler.getHeadBox((EntityLivingBase) entity);
            RenderGlobal.drawSelectionBoundingBox(aabb.grow(0.002D).offset(-renderPosX, -renderPosY, -renderPosZ), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

	}
}
