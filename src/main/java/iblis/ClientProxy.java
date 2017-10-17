package iblis;

import javax.annotation.Nonnull;

import iblis.client.ClientGameEventHandler;
import iblis.client.ItemTooltipEventHandler;
import iblis.client.particle.ParticleBoulderShard;
import iblis.client.particle.ParticleHeadshot;
import iblis.client.renderer.entity.RenderBoulder;
import iblis.client.renderer.entity.RenderThrowingKnife;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityThrowingKnife;
import iblis.gui.GuiEventHandler;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.player.PlayerSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy {

	@Override
	void load() {
		MinecraftForge.EVENT_BUS.register(GuiEventHandler.instance);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
		MinecraftForge.EVENT_BUS.register(new ClientGameEventHandler());
		ModelBakery.registerItemVariants(IblisItems.GUIDE,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "adventurer_diary"),
						new ResourceLocation(IblisMod.MODID, "guide"),
						new ResourceLocation(IblisMod.MODID, "guide_opened") });
		ModelBakery.registerItemVariants(IblisItems.SHOTGUN_RELOADING,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_1"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_2"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_3"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_4"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_5"),
						new ResourceLocation(IblisMod.MODID, "six_barrels_shotgun_reloading_6") });
	}

	@Override
	public void init() {
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityBoulder.class, new RenderBoulder(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityThrowingKnife.class, new RenderThrowingKnife(Minecraft.getMinecraft().getRenderManager()));
		IblisItems.registerRenders();
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public double getPlayerSkillValue(PlayerSkills sensitiveSkill, InventoryCrafting inv) {
		double serverValue = super.getPlayerSkillValue(sensitiveSkill, inv);
		if (serverValue != 0d)
			return serverValue;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return sensitiveSkill.getFullSkillValue(player);
	}

	@Override
	public EntityPlayer getPlayer(InventoryCrafting inv) {
		EntityPlayer serverPlayer = super.getPlayer(inv);
		if (serverPlayer != null)
			return serverPlayer;
		return Minecraft.getMinecraft().player;
	}
	
	public void spawnParticle(@Nonnull IblisParticles particle, 
			double posX, double posY, double posZ, 
			double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		@Nonnull Particle entityParticle;
		Minecraft mc = Minecraft.getMinecraft();
		Entity renderViewEntity = mc.getRenderViewEntity();
		double distance = renderViewEntity.getDistanceSq(posX, posY, posZ);
		switch (particle) {
		case HEADSHOT:
			entityParticle = new ParticleHeadshot(mc.getTextureManager(), mc.world, 
					posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn, (float) (distance/1000d+0.1d));
			break;
		case BOULDER:
			entityParticle = new ParticleBoulderShard(mc.getTextureManager(), mc.world, 
					posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn, 1.0f);
			break;
		default:
			entityParticle = mc.effectRenderer.spawnEffectParticle(10, posX, posY, posZ, 
					xSpeedIn, ySpeedIn, zSpeedIn, new int[] { 0 });
			break;
		}
		mc.effectRenderer.addEffect(entityParticle);
	}
}
