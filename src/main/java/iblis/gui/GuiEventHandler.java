package iblis.gui;

import java.util.Random;

import iblis.IblisMod;
import iblis.item.ItemShotgun;
import iblis.item.ItemShotgunReloading;
import iblis.player.PlayerSkills;
import iblis.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEventHandler {
	/** I hope I'm lucky to not interfere with other mods. **/
	private static final int RECIPE_BOOK_BUTTON_INDEX = 10;
	private static final int CHARACTERISTICS_BUTTON_INDEX = 4;
	private static final int SKILLS_BUTTON_INDEX = CHARACTERISTICS_BUTTON_INDEX + 1;

	public static final ResourceLocation IBLIS_ICONS = new ResourceLocation(IblisMod.MODID, "textures/gui/icons.png");

	private long healthUpdateCounter;
	private int playerHealth;
	private long lastSystemTime;
	private int lastPlayerHealth;
	private Random rand = new Random();
	GuiButtonImage characteristicsButton;
	GuiButtonImage skillsButton;

	@SubscribeEvent
	public void onGuiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiInventory) {
			GuiInventory gui = (GuiInventory) event.getGui();
			characteristicsButton = new GuiButtonImage(CHARACTERISTICS_BUTTON_INDEX,
					gui.getGuiLeft() + 125, gui.getGuiTop() + 61, 20, 18, 0, 220, 18, IBLIS_ICONS);
			skillsButton = new GuiButtonImage(SKILLS_BUTTON_INDEX, gui.getGuiLeft() + 146,
					gui.getGuiTop() + 61, 20, 18, 20, 220, 18, IBLIS_ICONS);
			event.getGui().buttonList.add(characteristicsButton);
			event.getGui().buttonList.add(skillsButton);
		}
	}

	@SubscribeEvent
	public void onButtonPressed(GuiScreenEvent.ActionPerformedEvent.Post action) {
		if (action.getGui() instanceof GuiInventory) {
			GuiInventory gui = (GuiInventory) action.getGui();
			Minecraft mc = Minecraft.getMinecraft();
			switch (action.getButton().id) {
			case CHARACTERISTICS_BUTTON_INDEX:
				mc.displayGuiScreen(new GuiCharacteritics(mc.player));
				break;
			case SKILLS_BUTTON_INDEX:
				mc.displayGuiScreen(new GuiSkills(mc.player));
				break;
			case RECIPE_BOOK_BUTTON_INDEX:
				if(characteristicsButton!=null)
					characteristicsButton.setPosition(gui.getGuiLeft() + 125, gui.getGuiTop() + 61);
				if(skillsButton!=null)
					skillsButton.setPosition(gui.getGuiLeft() + 146, gui.getGuiTop() + 61);
				break;
			}
		}
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGameOverlayEvent.Pre action) {
		if (action.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemShotgun) {
				double sharpshootingSkillValue = PlayerSkills.SHARPSHOOTING.getFullSkillValue(player);
				ScaledResolution res = action.getResolution();
				int screenWidth = res.getScaledWidth();
				int screenHeight = res.getScaledHeight();
				int centerX = screenWidth / 2 + 1;
				int centerY = screenHeight / 2 + 1;
				double divider = (sharpshootingSkillValue + 1d) * (1d + player.getCooledAttackStrength(0.0F))
						* (player.isSneaking() ? 2d : 1d) * (player.isSprinting() ? 0.5d : 1d);
				int frameSize = (int) (screenHeight / divider);
				int colour = 0x44ff9600;
				// Top line left
				Gui.drawRect(centerX - frameSize / 2 - 1, centerY - frameSize / 2, centerX - frameSize / 3,
						centerY - frameSize / 2 - 1, colour);
				// Top line right
				Gui.drawRect(centerX + frameSize / 3 - 1, centerY - frameSize / 2, centerX + frameSize / 2,
						centerY - frameSize / 2 - 1, colour);
				// Bottom line left
				Gui.drawRect(centerX - frameSize / 2, centerY + frameSize / 2, centerX - frameSize / 3,
						centerY + frameSize / 2 - 1, colour);
				// Bottom line right
				Gui.drawRect(centerX + frameSize / 3 - 1, centerY + frameSize / 2, centerX + frameSize / 2,
						centerY + frameSize / 2 - 1, colour);
				// Left line top
				Gui.drawRect(centerX - frameSize / 2, centerY - frameSize / 2, centerX - frameSize / 2 - 1,
						centerY - frameSize / 3, colour);
				// Left line bottom
				Gui.drawRect(centerX - frameSize / 2, centerY + frameSize / 3 - 1, centerX - frameSize / 2 - 1,
						centerY + frameSize / 2, colour);
				// Right line top
				Gui.drawRect(centerX + frameSize / 2, centerY - frameSize / 2, centerX + frameSize / 2 - 1,
						centerY - frameSize / 3, colour);
				// Right line bottom
				Gui.drawRect(centerX + frameSize / 2, centerY + frameSize / 3 - 1, centerX + frameSize / 2 - 1,
						centerY + frameSize / 2 - 1, colour);
			}
		}
		if (action.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
			action.setCanceled(true);
			ScaledResolution res = action.getResolution();
			renderHealth(res.getScaledWidth(), res.getScaledHeight());
		}
	}

	/**
	 * It's mostly copy-paste from forge. I just set healthbar row height to 1
	 **/
	private void renderHealth(int width, int height) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
		GlStateManager.enableBlend();

		mc.mcProfiler.startSection("iblisModAmmo");
		int right = width / 2 + 91;
		int top = height - GuiIngameForge.left_height;
		mc.getTextureManager().bindTexture(IBLIS_ICONS);
		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		if (!heldItem.isEmpty() && heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey("ammo")) {
			int ammo = heldItem.getTagCompound().getInteger("ammo");
			for (int i = 0; i < ItemShotgunReloading.MAX_AMMO; i++) {
				int x = right - 6 - i * 6;
				int y = top - 27;
				if (i < ammo)
					mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 7, 16);
				else
					mc.ingameGUI.drawTexturedModalRect(x, y, 8, 0, 7, 16);
			}
		}

		mc.mcProfiler.endStartSection("health");
		mc.getTextureManager().bindTexture(Gui.ICONS);
		int health = MathHelper.ceil(player.getHealth());
		int updateCounter = mc.ingameGUI.getUpdateCounter();
		boolean highlight = healthUpdateCounter > (long) updateCounter
				&& (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

		if (health < this.playerHealth && player.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (updateCounter + 20);
		} else if (health > this.playerHealth && player.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = (long) (updateCounter + 10);
		}

		if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
			this.playerHealth = health;
			this.lastPlayerHealth = health;
			this.lastSystemTime = Minecraft.getSystemTime();
		}

		this.playerHealth = health;
		int healthLast = this.lastPlayerHealth;

		IAttributeInstance attrMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		float healthMax = (float) attrMaxHealth.getAttributeValue();
		float absorb = MathHelper.ceil(player.getAbsorptionAmount());

		int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);

		this.rand.setSeed((long) (updateCounter * 312871));

		int left = width / 2 - 91;
		GuiIngameForge.left_height += healthRows + 9;

		int regen = -1;
		if (player.isPotionActive(MobEffects.REGENERATION)) {
			regen = updateCounter % 25;
		}

		final int TOP = 9 * (mc.world.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
		final int BACKGROUND = (highlight ? 25 : 16);
		int MARGIN = 16;
		if (player.isPotionActive(MobEffects.POISON))
			MARGIN += 36;
		else if (player.isPotionActive(MobEffects.WITHER))
			MARGIN += 72;
		int absorbRemaining = Math.round(absorb);

		for (int i = 0; i < MathHelper.ceil((healthMax + absorb) / 2.0F); i++) {
			int row = MathHelper.ceil((float) (i + 1) / 10.0F) - 1;
			int x = left + i % 10 * 8;
			int y = top - row * 2;

			if (health <= 4)
				y += rand.nextInt(2);
			if (i == regen)
				y -= 2;
			if (i * 2 + 2 > healthMax + absorb)
				mc.ingameGUI.drawTexturedModalRect(x, y, BACKGROUND, TOP, 5, 9);
			else
				mc.ingameGUI.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);

			if (highlight) {
				if (i * 2 + 1 < healthLast)
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); // 6
				else if (i * 2 + 1 == healthLast)
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); // 7
			}

			if (i * 2 + 1 < health)
				mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 4
			else if (i * 2 + 1 == health) {
				if (absorbRemaining > 0) {
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
					absorbRemaining--;
				} else
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); // 5
			} else if (absorbRemaining > 0) {
				if (absorbRemaining == 1) {
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
					absorbRemaining--;
				} else {
					mc.ingameGUI.drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
					absorbRemaining -= 2;
				}
			}
		}

		GlStateManager.disableBlend();
		mc.mcProfiler.endSection();

	}

}
