package iblis.client.gui;

import java.util.Random;

import iblis.ClientNetworkHandler;
import iblis.IblisMod;
import iblis.ServerNetworkHandler.ServerCommands;
import iblis.client.ClientGameEventHandler;
import iblis.constants.NBTTagsKeys;
import iblis.crafting.IRecipeRaiseSkill;
import iblis.crafting.PlayerSensitiveShapedRecipeWrapper;
import iblis.init.IblisItems;
import iblis.item.ItemFirearmsBase;
import iblis.item.ItemShotgunReloading;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.util.ModIntegrationUtil;
import iblis.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEventHandler {
	/** I hope I'm lucky to not interfere with other mods. **/
	private static final int RECIPE_BOOK_BUTTON_INDEX = 10;
	private static final int CHARACTERISTICS_BUTTON_INDEX = 13;
	private static final int SKILLS_BUTTON_INDEX = CHARACTERISTICS_BUTTON_INDEX + 1;
	private static final int TRAIN_CRAFT_BUTTON_INDEX = SKILLS_BUTTON_INDEX + 1;

	public static final ResourceLocation IBLIS_ICONS = new ResourceLocation(IblisMod.MODID, "textures/gui/icons.png");
	public static final GuiEventHandler instance = new GuiEventHandler();

	private long healthUpdateCounter;
	private int playerHealth;
	private long lastSystemTime;
	private int lastPlayerHealth;
	private Random rand = new Random();
	GuiButtonImage characteristicsButton;
	GuiButtonImage skillsButton;
	GuiButtonImage trainCraftButton;

	private String currentSkillHint = "";
	private String requiredOrExpSkillHint = "";
	
	private String genericHint = "";
	private int genericHintDisplayCountdown = 0;

	@SubscribeEvent
	public void onGuiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiInventory) {
			GuiInventory gui = (GuiInventory) event.getGui();
			characteristicsButton = new GuiButtonImage(CHARACTERISTICS_BUTTON_INDEX, gui.getGuiLeft() + 125,
					gui.getGuiTop() + 61, 20, 18, 0, 220, 18, IBLIS_ICONS);
			skillsButton = new GuiButtonImage(SKILLS_BUTTON_INDEX, gui.getGuiLeft() + 146, gui.getGuiTop() + 61, 20, 18,
					20, 220, 18, IBLIS_ICONS);
			event.getGui().buttonList.add(characteristicsButton);
			event.getGui().buttonList.add(skillsButton);
		}
		refreshTrainCraftingButton();
	}

	@SubscribeEvent
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (!(event.getGui() instanceof GuiCrafting))
			return;
		GuiCrafting gui = (GuiCrafting) event.getGui();
		Minecraft mc = Minecraft.getMinecraft();
		GuiCrafting gc = (GuiCrafting) event.getGui();
		GlStateManager.color(1f, 1f, 1f, 1f);
		mc.fontRenderer.drawString(currentSkillHint, gc.getGuiLeft() + 88, gc.getGuiTop() + 6, 4210752);
		mc.fontRenderer.drawString(requiredOrExpSkillHint, gc.getGuiLeft() + 88, gc.getGuiTop() + 19, 4210752);
		if (trainCraftButton != null && trainCraftButton.isMouseOver() && gui.buttonList.contains(trainCraftButton)) {
			mc.currentScreen.drawHoveringText(trainCraftButton.displayString, event.getMouseX(), event.getMouseY());
		}
	}

	@SubscribeEvent
	public void onButtonPressed(GuiScreenEvent.ActionPerformedEvent.Post action) {
		if (action.getGui() instanceof GuiInventory) {
			GuiContainer gui = (GuiContainer) action.getGui();
			Minecraft mc = Minecraft.getMinecraft();
			switch (action.getButton().id) {
			case CHARACTERISTICS_BUTTON_INDEX:
				mc.displayGuiScreen(new GuiCharacteritics(mc.player));
				break;
			case SKILLS_BUTTON_INDEX:
				mc.displayGuiScreen(new GuiSkills(mc.player));
				break;
			case RECIPE_BOOK_BUTTON_INDEX:
				if (characteristicsButton != null)
					characteristicsButton.setPosition(gui.getGuiLeft() + 125, gui.getGuiTop() + 61);
				if (skillsButton != null)
					skillsButton.setPosition(gui.getGuiLeft() + 146, gui.getGuiTop() + 61);
				break;
			}
		} else if (action.getGui() instanceof GuiCrafting) {
			GuiContainer gui = (GuiContainer) action.getGui();
			switch (action.getButton().id) {
			case RECIPE_BOOK_BUTTON_INDEX:
				if (trainCraftButton != null)
					trainCraftButton.setPosition(gui.getGuiLeft() + 122, gui.getGuiTop() + 61);
				break;
			case TRAIN_CRAFT_BUTTON_INDEX:
				ClientNetworkHandler network = (ClientNetworkHandler) IblisMod.network;
				network.sendCommand(ServerCommands.TRAIN_TO_CRAFT);
				break;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onOverlayRender(RenderGameOverlayEvent.Pre action) {
		if (action.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			ScaledResolution res = action.getResolution();
			int screenWidth = res.getScaledWidth();
			int screenHeight = res.getScaledHeight();
			this.renderHint(screenWidth, screenHeight);
			GuiLabTable.instance.render();
		}
		if (action.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;
			Item heldItem = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
			if (ModIntegrationUtil.shouldShowAimFrame(heldItem, player)) {
				ScaledResolution res = action.getResolution();
				int screenWidth = res.getScaledWidth();
				int screenHeight = res.getScaledHeight();
				int centerX = screenWidth / 2 + 1;
				int centerY = screenHeight / 2 + 1;
				double divider = PlayerUtils.getShootingAccuracyDivider(player);
				int frameSize = Math.min((int) (2 * screenHeight / divider), screenHeight - 4);
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
				if (heldItem instanceof ItemFirearmsBase)
					action.setCanceled(true);
			}
		}
		if (!IblisMod.isRPGHUDLoaded && action.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
			if(action.isCanceled())
				return;
			action.setCanceled(true);
			ScaledResolution res = action.getResolution();
			renderHealth(res.getScaledWidth(), res.getScaledHeight());
		} else if (!IblisMod.isAppleskinLoaded && action.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
			action.setCanceled(true);
			ScaledResolution res = action.getResolution();
			renderFood(res.getScaledWidth(), res.getScaledHeight());
		}
	}

	private void renderHealth(int width, int height) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
		GlStateManager.enableBlend();

		mc.mcProfiler.startSection("iblisMod");
		int right = width / 2 + 91;
		int top = height - GuiIngameForge.left_height;
		mc.getTextureManager().bindTexture(IBLIS_ICONS);
		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		if (!heldItem.isEmpty() && heldItem.hasTagCompound()
				&& (heldItem.getItem() == IblisItems.SHOTGUN || heldItem.getItem() == IblisItems.SHOTGUN_RELOADING)) {
			NBTTagList ammoList = heldItem.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10);
			int ammo = ammoList.tagCount();
			for (int i = 0; i < ItemShotgunReloading.MAX_AMMO; i++) {
				int x = right - 7 - i * 7;
				int y = top - 27;
				if (i < ammo) {
					 NBTTagCompound cartridge = ammoList.getCompoundTagAt(i);
					mc.ingameGUI.drawTexturedModalRect(x, y, 7*(1+cartridge.getInteger(NBTTagsKeys.AMMO_TYPE)), 0, 7, 16);
				} else {
					mc.ingameGUI.drawTexturedModalRect(x, y, 0, 0, 7, 16);
				}
			}
		}
		int sprintCounter = ClientGameEventHandler.instance.sprintCounter;
		int sprintButtonCounter = ClientGameEventHandler.instance.sprintButtonCounter;

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		for (int i = 0; i < sprintCounter / 4; i++)
			mc.ingameGUI.drawTexturedModalRect(width - 9, height - 9 * i, 247, 0, 9, 9);

		for (int i = 0; i < sprintButtonCounter / 4; i++)
			mc.ingameGUI.drawTexturedModalRect(width - 18, height - 9 * i, 238, 0, 9, 9);

		mc.mcProfiler.endStartSection("health");
		mc.getTextureManager().bindTexture(Gui.ICONS);
		int health = MathHelper.ceil(player.getHealth());
		int updateCounter = mc.ingameGUI.getUpdateCounter();
		boolean highlight = healthUpdateCounter > updateCounter
				&& (healthUpdateCounter - updateCounter) / 3L % 2L == 1L;

		if (health < this.playerHealth && player.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = updateCounter + 20;
		} else if (health > this.playerHealth && player.hurtResistantTime > 0) {
			this.lastSystemTime = Minecraft.getSystemTime();
			this.healthUpdateCounter = updateCounter + 10;
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

		this.rand.setSeed(updateCounter * 312871);

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

		int firstIcon = (MathHelper.floor((health - 1.0f) / 2.0F) / 10) * 10;
		int lastIcon = Math.min(firstIcon + 10, MathHelper.ceil((healthMax + absorb) / 2.0F));
		if (firstIcon > 0) {
			String hphint = "+" + firstIcon;
			int hintLeft = left + 88 - hphint.length() * 7;
			boolean unicode = mc.ingameGUI.getFontRenderer().getUnicodeFlag();
			mc.ingameGUI.getFontRenderer().setUnicodeFlag(false);
			mc.ingameGUI.getFontRenderer().drawString(hphint, hintLeft + 1, top + 1, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hphint, hintLeft - 1, top + 1, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hphint, hintLeft, top, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hphint, hintLeft, top + 2, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hphint, hintLeft, top + 1, 0xBB0000);
			mc.ingameGUI.getFontRenderer().setUnicodeFlag(unicode);
			top -= 3;
		}
		GlStateManager.color(1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(Gui.ICONS);
		for (int i = firstIcon; i < lastIcon; i++) {
			int x = left + i % 10 * 8;
			int y = top;

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

	public void renderFood(int width, int height) {
		GlStateManager.enableBlend();
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
		mc.mcProfiler.startSection("food");
		int left = width / 2 + 91;
		int top = height - 39;
		GuiIngameForge.right_height += 10;
		FoodStats stats = mc.player.getFoodStats();
		int level = stats.getFoodLevel();
		int maxLevel = MathHelper.floor(PlayerCharacteristics.GLUTTONY.getCurrentValue(player));
		int updateCounter = mc.ingameGUI.getUpdateCounter();

		int firstIcon = (MathHelper.floor((level - 1.0f) / 2.0f) / 10) * 10;
		int lastIcon = Math.min(firstIcon + 10, MathHelper.ceil(maxLevel/2.0f));
		if (firstIcon > 0) {
			String hint = "+" + firstIcon;
			int hintLeft = left + 8 - hint.length() * 7;
			boolean unicode = mc.ingameGUI.getFontRenderer().getUnicodeFlag();
			mc.ingameGUI.getFontRenderer().setUnicodeFlag(false);
			mc.ingameGUI.getFontRenderer().drawString(hint, hintLeft + 1, top + 1, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hint, hintLeft - 1, top + 1, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hint, hintLeft, top, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hint, hintLeft, top + 2, 0x000000);
			mc.ingameGUI.getFontRenderer().drawString(hint, hintLeft, top + 1, 0xBB9900);
			mc.ingameGUI.getFontRenderer().setUnicodeFlag(unicode);
			top -= 3;
		}

		GlStateManager.color(1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(Gui.ICONS);
		for (int i = firstIcon; i < lastIcon; i++) {
			int idx = i * 2 + 1;
			int x = left - i % 10 * 8 - 9;
			int y = top;
			int icon = 16;
			byte background = 0;

			if (mc.player.isPotionActive(MobEffects.HUNGER)) {
				icon += 36;
				background = 13;
			}
			
			if (player.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (level * 3 + 1) == 0) {
				y = top + (rand.nextInt(3) - 1);
			}

			mc.ingameGUI.drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

			if (idx < level)
				mc.ingameGUI.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
			else if (idx == level)
				mc.ingameGUI.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
		}
		GlStateManager.disableBlend();
		mc.mcProfiler.endSection();
	}

	public void refreshTrainCraftingButton() {
		Minecraft mc = Minecraft.getMinecraft();
		if (!(mc.currentScreen instanceof GuiCrafting))
			return;
		GuiCrafting gui = (GuiCrafting) mc.currentScreen;
		ContainerWorkbench workBenchContainer = (ContainerWorkbench) gui.inventorySlots;
		IRecipe recipe = CraftingManager.findMatchingRecipe(workBenchContainer.craftMatrix, mc.world);
		if (recipe instanceof IRecipeRaiseSkill) {
			if (trainCraftButton == null)
				trainCraftButton = new GuiButtonImageWithTooltip(TRAIN_CRAFT_BUTTON_INDEX, gui.getGuiLeft() + 122,
						gui.getGuiTop() + 61, 20, 18, 60, 220, 18, IBLIS_ICONS,
						I18n.format("iblis.trainCraftTooltip", new Object[0]));
			if (!gui.buttonList.contains(trainCraftButton))
				gui.buttonList.add(trainCraftButton);
			trainCraftButton.setPosition(gui.getGuiLeft() + 122, gui.getGuiTop() + 61);

			IRecipeRaiseSkill rsr = (IRecipeRaiseSkill) recipe;
			EntityPlayerSP player = mc.player;
			IAttribute attribute = rsr.getSensitiveSkill().getAttribute();
			this.currentSkillHint = I18n.format(attribute.getName(),
					Math.round(rsr.getSensitiveSkill().getFullSkillValue(player) * 10) / 10d);
			this.requiredOrExpSkillHint = I18n.format("iblis.skillExp",
					this.formatSkillExp(player, rsr.getSensitiveSkill(), rsr.getSkillExp()));
			if (recipe instanceof PlayerSensitiveShapedRecipeWrapper) {
				PlayerSensitiveShapedRecipeWrapper r = (PlayerSensitiveShapedRecipeWrapper) recipe;
				this.requiredOrExpSkillHint = I18n.format("iblis.requiredSkill",
						Math.round(r.getRequiredSkill() * 10) / 10d);
			}

		} else {
			this.currentSkillHint = "";
			this.requiredOrExpSkillHint = "";
			if (trainCraftButton != null)
				gui.buttonList.remove(trainCraftButton);
		}
	}

	private String formatSkillExp(EntityPlayer player, PlayerSkills sensitiveSkill, double xpValue) {
		double currentValue = sensitiveSkill.getCurrentValue(player);
		double xpReal = sensitiveSkill.pointsPerLevel * xpValue / (currentValue + 1.0d);
		if (xpReal > 0.1d)
			return Double.toString(Math.round(xpReal * 10) / 10d);
		else
			return "1/" + Math.round(1 / xpReal);
	}
	
	public void showHint(String unlocalisedHint){
		genericHintDisplayCountdown = 255;
		genericHint = I18n.format(unlocalisedHint);
	}
	
	private void renderHint(int width, int height) {
		if (genericHintDisplayCountdown > 0) {
			genericHintDisplayCountdown--;
			Minecraft mc = Minecraft.getMinecraft();
			int left = width / 2 - 91;
			int top = height / 2;
			GlStateManager.color(1f, 1f, 1f, genericHintDisplayCountdown/255.0f);
			mc.ingameGUI.getFontRenderer().drawString(genericHint, left, top, 0xFFFFFF);
		}
	}
}
