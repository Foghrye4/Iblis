package iblis;

import iblis.client.ClientGameEventHandler;
import iblis.client.ItemTooltipEventHandler;
import iblis.gui.GuiEventHandler;
import iblis.init.IblisItems;
import iblis.player.PlayerSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy{
	
	@Override
	void load() {
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		MinecraftForge.EVENT_BUS.register(new ItemTooltipEventHandler());
		MinecraftForge.EVENT_BUS.register(new ClientGameEventHandler());
		ModelBakery.registerItemVariants(IblisItems.GUIDE, new ResourceLocation[] {new ResourceLocation(IblisMod.MODID,"adventurer_diary"),new ResourceLocation(IblisMod.MODID,"guide"),new ResourceLocation(IblisMod.MODID,"guide_opened")});
		ModelBakery.registerItemVariants(IblisItems.SHOTGUN_RELOADING, new ResourceLocation[] {
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_1"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_2"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_3"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_4"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_5"),
				new ResourceLocation(IblisMod.MODID,"six_barrels_shotgun_reloading_6")});
	}
	
	@Override
	public void init() {
		IblisItems.registerRenders();
	}

	
	@Override
	public boolean isClient() {
		return true;
	}
	
	@Override
	public double getPlayerSkillValue(PlayerSkills sensitiveSkill, InventoryCrafting inv) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return sensitiveSkill.getFullSkillValue(player);
	}
	
	@Override
	public EntityPlayer getPlayer(InventoryCrafting inv) {
		return Minecraft.getMinecraft().player;
	}
}
