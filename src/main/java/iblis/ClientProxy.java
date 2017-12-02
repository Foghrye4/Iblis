package iblis;

import javax.annotation.Nonnull;

import iblis.client.ClientGameEventHandler;
import iblis.client.ClientRenderEventHandler;
import iblis.client.ItemTooltipEventHandler;
import iblis.client.particle.ParticleBoulderShard;
import iblis.client.particle.ParticleHeadshot;
import iblis.client.particle.ParticleSliver;
import iblis.client.particle.ParticleSpark;
import iblis.client.renderer.entity.RenderBoulder;
import iblis.client.renderer.entity.RenderCrossbowBolt;
import iblis.client.renderer.entity.RenderThrowingKnife;
import iblis.constants.NBTTagsKeys;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityCrossbowBolt;
import iblis.entity.EntityThrowingKnife;
import iblis.gui.GuiEventHandler;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.player.PlayerSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy {

	@Override
	void load() {
		OBJLoader.INSTANCE.addDomain(IblisMod.MODID);
		MinecraftForge.EVENT_BUS.register(GuiEventHandler.instance);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
		MinecraftForge.EVENT_BUS.register(new ClientGameEventHandler());
		MinecraftForge.EVENT_BUS.register(new ClientRenderEventHandler());
	}

	@Override
	public void registerRenders() {
		final ModelResourceLocation mShotgun = new ModelResourceLocation(IblisMod.MODID + ":" + "six_barrels_shotgun",
				"inventory");
		final ModelResourceLocation mShotgunAim = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun", "aiming");
		ModelLoader.setCustomMeshDefinition(IblisItems.SHOTGUN, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if (player != null && player.isHandActive() && player.getActiveItemStack() == stack)
					return mShotgunAim;
				return mShotgun;
			}
		});
		ModelBakery.registerItemVariants(IblisItems.SHOTGUN, new ResourceLocation[] { mShotgun, mShotgunAim });

		final ModelResourceLocation m0 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "inventory");
		final ModelResourceLocation m1 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_1");
		final ModelResourceLocation m2 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_2");
		final ModelResourceLocation m3 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_3");
		final ModelResourceLocation m4 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_4");
		final ModelResourceLocation m5 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_5");
		final ModelResourceLocation m6 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "six_barrels_shotgun_reloading", "ammo_6");

		ModelLoader.setCustomMeshDefinition(IblisItems.SHOTGUN_RELOADING, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (!stack.hasTagCompound())
					return m0;
				switch (stack.getTagCompound().getInteger("ammo")) {
				case 0:
					return m0;
				case 1:
					return m1;
				case 2:
					return m2;
				case 3:
					return m3;
				case 4:
					return m4;
				case 5:
					return m5;
				case 6:
					return m6;
				}
				return m0;
			}
		});
		ModelBakery.registerItemVariants(IblisItems.SHOTGUN_RELOADING,
				new ResourceLocation[] { m0, m1, m2, m3, m4, m5, m6 });

		final ModelResourceLocation mCrossbowNoAmmo = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow", "inventory_no_ammo");
		final ModelResourceLocation mCrossbowAmmo1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
				"inventory_ammo_1");
		final ModelResourceLocation mCrossbowAmmo2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
				"inventory");
		final ModelResourceLocation mCrossbowAimNoAmmo = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow", "aiming_no_ammo");
		final ModelResourceLocation mCrossbowAimAmmo1 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow", "aiming_ammo_1");
		final ModelResourceLocation mCrossbowAimAmmo2 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow", "aiming");
		ModelLoader.setCustomMeshDefinition(IblisItems.CROSSBOW, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if (player != null && player.isHandActive() && player.getActiveItemStack() == stack) {
					if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTTagsKeys.AMMO))
						return mCrossbowAimNoAmmo;
					switch (stack.getTagCompound().getInteger(NBTTagsKeys.AMMO)) {
					case 0:
						return mCrossbowAimNoAmmo;
					case 1:
						return mCrossbowAimAmmo1;
					default:
						return mCrossbowAimAmmo2;
					}
				}
				if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTTagsKeys.AMMO))
					return mCrossbowNoAmmo;
				switch (stack.getTagCompound().getInteger(NBTTagsKeys.AMMO)) {
				case 0:
					return mCrossbowNoAmmo;
				case 1:
					return mCrossbowAmmo1;
				default:
					return mCrossbowAmmo2;
				}
			}
		});
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW, new ResourceLocation[] { mCrossbowNoAmmo, mCrossbowAmmo1,
				mCrossbowAmmo2, mCrossbowAimNoAmmo, mCrossbowAimAmmo1, mCrossbowAimAmmo2 });

		final ModelResourceLocation m1_f0 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "inventory");
		final ModelResourceLocation m1_f1 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_upper_1");
		final ModelResourceLocation m1_f2 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_upper_2");
		final ModelResourceLocation m1_f3 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_upper_3");
		final ModelResourceLocation m1_f4 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_upper_4");
		final ModelResourceLocation m1_f5 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_upper_5");
		final ModelResourceLocation m1_f6 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "arming_upper_1");
		final ModelResourceLocation m1_f7 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "arming_upper_2");
		final ModelResourceLocation m2_f1 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_lower_1");
		final ModelResourceLocation m2_f2 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_lower_2");
		final ModelResourceLocation m2_f3 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_lower_3");
		final ModelResourceLocation m2_f4 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_lower_4");
		final ModelResourceLocation m2_f5 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "stretching_lower_5");
		final ModelResourceLocation m2_f6 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "arming_lower_1");
		final ModelResourceLocation m2_f7 = new ModelResourceLocation(
				IblisMod.MODID + ":" + "double_crossbow_reloading", "arming_lower_2");

		ModelLoader.setCustomMeshDefinition(IblisItems.CROSSBOW_RELOADING, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if (player != null && player.isHandActive() && player.getActiveItemStack() == stack) {
					int max = player.getItemInUseMaxCount();
					int current = player.getItemInUseCount();
					if (!stack.hasTagCompound() || stack.getTagCompound().getInteger(NBTTagsKeys.AMMO) == 0) {
						if (current <= 4)
							return m1_f7;
						else if (current <= 8)
							return m1_f6;
						else if (max <= 10)
							return m1_f0;
						else if (max <= 11)
							return m1_f1;
						else if (max <= 12)
							return m1_f2;
						else if (max <= 13)
							return m1_f3;
						else if (max <= 14)
							return m1_f4;
						else
							return m1_f5;
					}
					if (current <= 4)
						return m2_f7;
					else if (current <= 8)
						return m2_f6;
					else if (max <= 10)
						return m1_f7;
					else if (max <= 11)
						return m2_f1;
					else if (max <= 12)
						return m2_f2;
					else if (max <= 13)
						return m2_f3;
					else if (max <= 14)
						return m2_f4;
					else
						return m2_f5;
				}
				if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTTagsKeys.AMMO))
					return mCrossbowNoAmmo;
				switch (stack.getTagCompound().getInteger(NBTTagsKeys.AMMO)) {
				case 0:
					return mCrossbowNoAmmo;
				case 1:
					return mCrossbowAmmo1;
				default:
					return mCrossbowAmmo2;
				}
			}
		});
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW_BOLT, new ResourceLocation[] {m1_f1,m1_f2,m1_f3,m1_f4,m1_f5,m1_f6,m1_f7,m2_f1,m2_f2,m2_f3,m2_f4,m2_f5,m2_f6,m2_f7});
		final ModelResourceLocation crossbow_bolt = new ModelResourceLocation(IblisMod.MODID + ":" + "crossbow_bolt",
				"inventory");
		ModelLoader.setCustomModelResourceLocation(IblisItems.CROSSBOW_BOLT, 0, crossbow_bolt);
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW_BOLT, crossbow_bolt);
		ModelBakery.registerItemVariants(IblisItems.GUIDE,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "adventurer_diary"),
						new ResourceLocation(IblisMod.MODID, "guide"),
						new ResourceLocation(IblisMod.MODID, "guide_opened") });
	}
	
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ParticleSliver.bakeModels();
	}
	

	@Override
	public void init() {
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityBoulder.class, new RenderBoulder(
				Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityThrowingKnife.class,
				new RenderThrowingKnife(Minecraft.getMinecraft().getRenderManager()));
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityCrossbowBolt.class,
				new RenderCrossbowBolt(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
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

	public void spawnParticle(@Nonnull IblisParticles particle, double posX, double posY, double posZ, double xSpeedIn,
			double ySpeedIn, double zSpeedIn) {
		@Nonnull
		Particle entityParticle;
		Minecraft mc = Minecraft.getMinecraft();
		Entity renderViewEntity = mc.getRenderViewEntity();
		double distance = renderViewEntity.getDistanceSq(posX, posY, posZ);
		distance = MathHelper.sqrt(distance);
		switch (particle) {
		case HEADSHOT:
			entityParticle = new ParticleHeadshot(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn,
					ySpeedIn, zSpeedIn, (float) (distance / 100d + 0.1d));
			break;
		case BOULDER:
			entityParticle = new ParticleBoulderShard(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn,
					ySpeedIn, zSpeedIn, 1.0f);
			break;
		case SPARK:
			entityParticle = new ParticleSpark(mc.world, posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn, -0.04f);
			break;
		case SLIVER:
			entityParticle = new ParticleSliver(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn, 1.0f);
			break;
		default:
			entityParticle = mc.effectRenderer.spawnEffectParticle(10, posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn,
					new int[] { 0 });
			break;
		}
		mc.effectRenderer.addEffect(entityParticle);
	}
}
