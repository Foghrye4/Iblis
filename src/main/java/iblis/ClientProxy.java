package iblis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import iblis.client.ClientGameEventHandler;
import iblis.client.ClientRenderEventHandler;
import iblis.client.ItemTooltipEventHandler;
import iblis.client.gui.GuiEventHandler;
import iblis.client.particle.ParticleBoulderShard;
import iblis.client.particle.ParticleDecal;
import iblis.client.particle.ParticleHeadshot;
import iblis.client.particle.ParticleSliver;
import iblis.client.particle.ParticleSpark;
import iblis.client.renderer.entity.RenderBoulder;
import iblis.client.renderer.entity.RenderCrossbowBolt;
import iblis.client.renderer.entity.RenderThrowingKnife;
import iblis.client.renderer.item.CrossbowItemMeshDefinition;
import iblis.client.renderer.item.CrossbowReloadingItemMeshDefinition;
import iblis.client.renderer.tileentity.TileEntityLabTableSpecialRenderer;
import iblis.client.util.DecalHelper;
import iblis.constants.NBTTagsKeys;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityCrossbowBolt;
import iblis.entity.EntityThrowingKnife;
import iblis.init.IblisBlocks;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.player.PlayerSkills;
import iblis.tileentity.TileEntityLabTable;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends ServerProxy {

	@SubscribeEvent
	void onWorldLoadedEvent(WorldEvent.Load event) {
		if (!event.getWorld().isRemote)
			return;
		event.getWorld().addEventListener(ClientGameEventHandler.instance);
	}

	@Override
	void load() {
		OBJLoader.INSTANCE.addDomain(IblisMod.MODID);
		MinecraftForge.EVENT_BUS.register(GuiEventHandler.instance);
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
		MinecraftForge.EVENT_BUS.register(ClientGameEventHandler.instance);
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
				switch (stack.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10).tagCount()) {
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

		CrossbowItemMeshDefinition crossbowMeshDef = new CrossbowItemMeshDefinition();
		ModelLoader.setCustomMeshDefinition(IblisItems.CROSSBOW, crossbowMeshDef);
		crossbowMeshDef.registerVariants();
		CrossbowReloadingItemMeshDefinition crossbowReloadMeshDef = new CrossbowReloadingItemMeshDefinition();
		ModelLoader.setCustomMeshDefinition(IblisItems.CROSSBOW_RELOADING, crossbowReloadMeshDef);
		crossbowReloadMeshDef.registerVariants();

		final ModelResourceLocation crossbow_bolt = new ModelResourceLocation(IblisMod.MODID + ":" + "crossbow_bolt",
				"inventory");
		ModelLoader.setCustomModelResourceLocation(IblisItems.CROSSBOW_BOLT, 0, crossbow_bolt);
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW_BOLT, crossbow_bolt);
		ModelBakery.registerItemVariants(IblisItems.GUIDE,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "adventurer_diary"),
						new ResourceLocation(IblisMod.MODID, "guide"),
						new ResourceLocation(IblisMod.MODID, "guide_opened") });
		ModelBakery.registerItemVariants(IblisItems.SHOTGUN_BULLET,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "shotgun_bullet"),
						new ResourceLocation(IblisMod.MODID, "shotgun_shot") });
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ParticleSliver.bakeModels();
		TileEntityLabTableSpecialRenderer.bakeModels();
	}

	@Override
	public void init() {
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityBoulder.class, new RenderBoulder(
				Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()));
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityThrowingKnife.class,
				new RenderThrowingKnife(Minecraft.getMinecraft().getRenderManager()));
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityCrossbowBolt.class,
				new RenderCrossbowBolt(Minecraft.getMinecraft().getRenderManager(),
						Minecraft.getMinecraft().getRenderItem()));
		IblisItems.registerRenders();
		IblisBlocks.registerRenders();
		TileEntityLabTableSpecialRenderer renderer = new TileEntityLabTableSpecialRenderer();
		MinecraftForge.EVENT_BUS.register(renderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLabTable.class, renderer);
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
			entityParticle = new ParticleSliver(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn, ySpeedIn,
					zSpeedIn, 1.0f);
			break;
		default:
			entityParticle = mc.effectRenderer.spawnEffectParticle(10, posX, posY, posZ, xSpeedIn, ySpeedIn, zSpeedIn,
					new int[] { 0 });
			break;
		}
		mc.effectRenderer.addEffect(entityParticle);
	}

	public void addDecal(@Nonnull IblisParticles decalIn, double posX, double posY, double posZ,
			@Nonnull EnumFacing facingIn, int colourIn, float size) {
		Minecraft mc = Minecraft.getMinecraft();
		int spriteIndexX = 0;
		switch (decalIn) {
		case BULLET_HOLE:
			spriteIndexX = 0;
			break;
		case BLOOD_SPLATTER:
			spriteIndexX = 1;
			break;
		case TRACE_OF_SHOT:
			spriteIndexX = 2;
			break;
		default:
			spriteIndexX = 0;
			IblisMod.log.error("Incorrect/unhandled decal recieved on client: " + decalIn.name());
		}

		AxisAlignedBB particleBB = new AxisAlignedBB(posX - size / 2, posY - size / 2, posZ - size / 2, posX + size / 2,
				posY + size / 2, posZ + size / 2);
		List<AxisAlignedBB> collidingBoxes = new ArrayList<AxisAlignedBB>();
		int x1 = MathHelper.floor(posX - size / 2);
		int y1 = MathHelper.floor(posY - size / 2);
		int z1 = MathHelper.floor(posZ - size / 2);
		int x2 = MathHelper.ceil(posX - size / 2);
		int y2 = MathHelper.ceil(posY - size / 2);
		int z2 = MathHelper.ceil(posZ - size / 2);
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				for (int z = z1; z <= z2; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					IBlockState bstate = mc.world.getBlockState(pos);
					// For a custom cases when collision box not match display
					// borders.
					DecalHelper.addDecalDisplayBoxToList(mc.world, pos, particleBB, collidingBoxes, bstate);
					if (!collidingBoxes.isEmpty()) {
						int packedLight = bstate.getPackedLightmapCoords(mc.world, pos.offset(facingIn));
						int layer = ClientGameEventHandler.instance.getDecalLayer(pos);
						for (AxisAlignedBB cbb : collidingBoxes) {
							double posX1 = posX;
							double posY1 = posY;
							double posZ1 = posZ;
							switch (facingIn) {
							case DOWN:
								posY1 = Math.max(posY, cbb.minY);
								break;
							case UP:
								posY1 = Math.min(posY, cbb.maxY);
								break;
							case NORTH:
								posZ1 = Math.max(posZ, cbb.minZ);
								break;
							case SOUTH:
								posZ1 = Math.min(posZ, cbb.maxZ);
								break;
							case WEST:
								posX1 = Math.max(posX, cbb.minX);
								break;
							case EAST:
								posX1 = Math.min(posX, cbb.maxX);
								break;
							}
							int colour = colourIn;
							if (colourIn == -1)
								colour = DecalHelper.getDecalColour(mc.world, pos, bstate);
							ParticleDecal decal = new ParticleDecal(mc.getTextureManager(), mc.world, posX1, posY1,
									posZ1, facingIn, cbb, size, colour, packedLight, layer, spriteIndexX);
							mc.effectRenderer.addEffect(decal);
							ClientGameEventHandler.instance.attachParticleToBlock(decal, pos);
						}
						collidingBoxes.clear();
					}
				}
	}
}
