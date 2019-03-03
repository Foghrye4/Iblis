package iblis;

import static iblis.init.IblisBlocks.IRONORE_COAL;
import static iblis.init.IblisBlocks.IRON_COAL;
import static iblis.init.IblisBlocks.SLAG;
import static iblis.init.IblisItems.BOULDER;
import static iblis.init.IblisItems.GUIDE;
import static iblis.init.IblisItems.HEAVY_SHIELD;
import static iblis.init.IblisItems.INGOT;
import static iblis.init.IblisItems.MEDKIT;
import static iblis.init.IblisItems.NONSTERILE_MEDKIT;
import static iblis.init.IblisItems.NUGGET_STEEL;
import static iblis.init.IblisItems.RAISIN;
import static iblis.init.IblisItems.STEEL_BOOTS;
import static iblis.init.IblisItems.STEEL_CHESTPLATE;
import static iblis.init.IblisItems.STEEL_HELMET;
import static iblis.init.IblisItems.STEEL_LEGGINS;
import static iblis.init.IblisItems.TRIGGER_SPRING;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.stream.JsonReader;

import iblis.client.ClientGameEventHandler;
import iblis.client.ClientRenderEventHandler;
import iblis.client.ItemTooltipEventHandler;
import iblis.client.gui.GuiEventHandler;
import iblis.client.particle.ParticleBoulderShard;
import iblis.client.particle.ParticleDecal;
import iblis.client.particle.ParticleFlame;
import iblis.client.particle.ParticleSliver;
import iblis.client.particle.ParticleSpark;
import iblis.client.renderer.entity.RenderBoulder;
import iblis.client.renderer.entity.RenderCrossbowBolt;
import iblis.client.renderer.entity.RenderThrowingKnife;
import iblis.client.renderer.item.CrossbowItemMeshDefinition;
import iblis.client.renderer.item.CrossbowReloadingItemMeshDefinition;
import iblis.client.renderer.item.SingleIconItemMeshDefinition;
import iblis.client.util.DecalHelper;
import iblis.constants.NBTTagsKeys;
import iblis.entity.EntityBoulder;
import iblis.entity.EntityCrossbowBolt;
import iblis.entity.EntityThrowingKnife;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.item.ItemIngot;
import iblis.player.PlayerSkills;
import net.minecraft.block.Block;
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
import net.minecraft.item.Item;
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

		ModelLoader.setCustomMeshDefinition(IblisItems.SHOTGUN_BULLET, new SingleIconItemMeshDefinition(IblisMod.MODID,"shotgun_bullet","inventory"));
		ModelLoader.setCustomMeshDefinition(IblisItems.SHOTGUN_SHOT, new SingleIconItemMeshDefinition(IblisMod.MODID,"shotgun_shot","inventory"));
		
		SingleIconItemMeshDefinition cb = new SingleIconItemMeshDefinition(IblisMod.MODID,"crossbow_bolt","inventory");
		ModelLoader.setCustomMeshDefinition(IblisItems.CROSSBOW_BOLT, cb);
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW_BOLT, cb.getModelLocation(null));
		
		ModelLoader.setCustomMeshDefinition(IblisItems.IRON_THROWING_KNIFE, new SingleIconItemMeshDefinition(IblisMod.MODID,"iron_throwing_knife","inventory"));
		ModelBakery.registerItemVariants(IblisItems.GUIDE,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "adventurer_diary"),
						new ResourceLocation(IblisMod.MODID, "guide"),
						new ResourceLocation(IblisMod.MODID, "guide_opened") });
		ModelBakery.registerItemVariants(IblisItems.INGOT,
				new ResourceLocation[] { new ResourceLocation(IblisMod.MODID, "ingot_steel"),
						new ResourceLocation(IblisMod.MODID, "ingot_bronze") });
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
				new RenderCrossbowBolt(Minecraft.getMinecraft().getRenderManager(),
						Minecraft.getMinecraft().getRenderItem()));
		registerItemRenders();
		registerBlockRenders();
	}
	
	private void registerItemRenders() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"adventurer_diary"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 1,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"guide"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(GUIDE, 2,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"guide_opened"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INGOT, ItemIngot.STEEL,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"ingot_steel"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INGOT, ItemIngot.BRONZE,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"ingot_bronze"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NUGGET_STEEL, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"nugget_steel"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TRIGGER_SPRING, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"trigger_spring"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(RAISIN, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"raisin"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(NONSTERILE_MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"non-sterile_medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MEDKIT, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"medkit"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(BOULDER, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"boulder"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(HEAVY_SHIELD, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"heavy_shield"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_HELMET, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_helmet"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_CHESTPLATE, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_chestplate"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_LEGGINS, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_leggins"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(STEEL_BOOTS, 0,
				new ModelResourceLocation(new ResourceLocation(IblisMod.MODID,"steel_boots"), "inventory"));
		
		
	}
	
	private void registerBlockRenders() {
		registerRender(IRON_COAL, 0, IRON_COAL.getRegistryName());
		registerRender(IRONORE_COAL, 0, IRONORE_COAL.getRegistryName());
		registerRender(SLAG, 0, SLAG.getRegistryName());
	}
	
	private static void registerRender(Block block, int metadata, ResourceLocation modelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), metadata,
				new ModelResourceLocation(modelResourceLocation, "inventory"));
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
		case FLAME:
			entityParticle = new ParticleFlame(mc.getTextureManager(), mc.world, posX, posY, posZ, xSpeedIn, ySpeedIn,
					zSpeedIn, 0.01f);
			entityParticle.setMaxAge(16);
			entityParticle.multipleParticleScaleBy(0.2f);
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
	
	@Override
	public InputStream getResourceInputStream(ResourceLocation location) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.getResourceInputStream(location);
	}
	
	public void displayGuiScreenBookExtended(EntityPlayer playerIn, JsonReader jsonReader) {
	}
	
	public void setToggleSprintByKeyBindSprint(boolean value) {
		ClientGameEventHandler.instance.toggleSprintByKeyBindSprint = value;
	}
	
	@Override
	public void setHPRender(boolean renderHPIn) {
		GuiEventHandler.renderHP = renderHPIn;
	}
}
