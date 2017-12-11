package iblis.client;

import iblis.init.IblisItems;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientRenderEventHandler {

	public static final Int2IntMap playerKickAnimationState = new Int2IntOpenHashMap();
	public static final int PLAYER_KICK_ANIMATION_LENGTH = 15;
	public static float renderFirstPersonPlayerkickAnimation = 0f;
	
	@SubscribeEvent
	public void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
		GlStateManager.pushMatrix();
		EntityPlayer player = event.getEntityPlayer();
        float limbSwingAmount = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * event.getPartialRenderTick();
		if (limbSwingAmount <= 0f)
			return;
		int kickAnimationState = playerKickAnimationState.get(player.getEntityId());
		if (kickAnimationState == 0)
			return;
		playerKickAnimationState.put(player.getEntityId(), --kickAnimationState);
		float f = (player.rotationYaw + 90f) * 0.017453292F;
		float xr = -MathHelper.sin(f);
		float zr = MathHelper.cos(f);
		f = player.rotationYaw * 0.017453292F;
		float xd = -MathHelper.sin(f) * limbSwingAmount;
		float zd = MathHelper.cos(f) * limbSwingAmount;
		GlStateManager.translate(xd, 0, zd);
		GlStateManager.rotate(limbSwingAmount * 30f, xr, 0f, zr);
	}

	@SubscribeEvent
	public void onPostRenderPlayer(RenderPlayerEvent.Post event) {
		GlStateManager.popMatrix();
	}
	
	@SubscribeEvent
	public void onRenderMainHandFirstPerson(RenderSpecificHandEvent event) {
		float pitch = event.getInterpolatedPitch();
		if (event.getHand() == EnumHand.OFF_HAND) {
			if(renderFirstPersonPlayerkickAnimation < 0)
				return;
			GlStateManager.pushMatrix();
			this.renderLegsFirstPerson(event.getPartialTicks(), pitch);
			GlStateManager.popMatrix();
			renderFirstPersonPlayerkickAnimation-=0.1f;
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (!player.isHandActive() || player.getActiveItemStack().getItem() != IblisItems.CROSSBOW_RELOADING) {
			return;
		}
		GlStateManager.rotate(pitch - 90f, 1f, 0f, 0f);
	}
	
    private void renderLegsFirstPerson(float partialTicks, float pitch)
    {
    	Minecraft mc = Minecraft.getMinecraft();
    	RenderManager renderManager = mc.getRenderManager();
        AbstractClientPlayer abstractclientplayer = mc.player;
        mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        RenderPlayer renderplayer = (RenderPlayer)renderManager.<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
        GlStateManager.disableCull();		
		GlStateManager.rotate(pitch - 180f, 1f, 0f, 0f);
        GlStateManager.translate(0.0F, -0.52F, -0.4F);
        renderLegs(renderplayer, abstractclientplayer, partialTicks);
        GlStateManager.enableCull();
    }
    
    public void renderLegs(RenderPlayer renderPlayer, AbstractClientPlayer clientPlayer, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        ModelPlayer modelplayer = renderPlayer.getMainModel();
        modelplayer.swingProgress = clientPlayer.getSwingProgress(partialTicks);
        modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        GlStateManager.enableBlend();
        float f5 = clientPlayer.prevLimbSwingAmount + (clientPlayer.limbSwingAmount - clientPlayer.prevLimbSwingAmount) * partialTicks;
        float f6 = clientPlayer.limbSwing - clientPlayer.limbSwingAmount * (1.0F - partialTicks);
        f5 = renderFirstPersonPlayerkickAnimation;
        f6 = 1.5f;
        modelplayer.setLivingAnimations(clientPlayer, f6, f5, partialTicks);
        modelplayer.setRotationAngles(f6, f5, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.bipedLeftLeg.render(0.0625F);
        modelplayer.bipedLeftLegwear.render(0.0625F);
        modelplayer.bipedRightLeg.render(0.0625F);
        modelplayer.bipedRightLegwear.render(0.0625F);
        GlStateManager.disableBlend();
    }
}
