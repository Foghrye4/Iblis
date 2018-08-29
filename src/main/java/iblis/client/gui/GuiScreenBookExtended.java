package iblis.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.stream.JsonReader;

import iblis.IblisMod;
import iblis.chemistry.ChemicalReaction;
import iblis.chemistry.ChemistryRegistry;
import iblis.chemistry.Substance;
import iblis.chemistry.SubstanceStack;
import iblis.client.util.ClientStringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@SuppressWarnings("unused")
public class GuiScreenBookExtended extends GuiScreen {
	private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
	/** The player editing the book */
	private final static int bookImageWidth = 192;
	private final static int bookImageHeight = 192;
	private final static int letterWidth = 7;
	private final static int MAX_STRING_LENGTH = 23;
	private int currPage;
	private NextPageButton buttonNextPage;
	private NextPageButton buttonPreviousPage;
	private GuiButton buttonDone;
	private final List<List<GuiElement>> pages = new ArrayList<List<GuiElement>>();

	public GuiScreenBookExtended(EntityPlayer player, JsonReader reader) {
		List<GuiElement> allElements = new ArrayList<GuiElement>();
		try {
			StringBuffer sb = new StringBuffer();
			reader.beginArray();
			while(reader.hasNext()) {
				reader.beginObject();{
					String name = reader.nextName();
					if(name.equals("translate")) {
						String[] value = I18n.format(reader.nextString()).split(" ");
						for(int i=0;i<value.length;i++) {
							if(sb.length()*letterWidth +value[i].length()*letterWidth>bookImageWidth-40) {
								allElements.add(new GuiElementTextLine(sb.toString()));
								sb.setLength(0);
							}
							sb.append(value[i]);
							sb.append(" ");
						}
						allElements.add(new GuiElementTextLine(sb.toString()));
						sb.setLength(0);
					} else if(name.equals("crafting_by_output")) {
						String value = reader.nextString();
						Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(value));
						IForgeRegistry<IRecipe> recipeRegistry = ForgeRegistries.RECIPES;
						Iterator<IRecipe> irecipes = recipeRegistry.iterator();
						while (irecipes.hasNext()) {
							IRecipe recipe = irecipes.next();
							if(recipe.getRecipeOutput().getItem()==item) {
								allElements.add(new GuiElementCraftingRecipe(recipe));
								break;
							}
						}
					} else if(name.equals("custom")) {
						String value = reader.nextString();
						if(value.equals("stack_to_substance_conversion")) {
							Iterator<Entry<Item, List<SubstanceStack>>> si = ChemistryRegistry.itemToSubstanceMap.entrySet().iterator();
							while(si.hasNext()) {
								Entry<Item, List<SubstanceStack>> entry = si.next();
								allElements.add(new GuiElementItemStack(new ItemStack(entry.getKey())));
								allElements.add(new GuiElementTextLine(I18n.format("iblis.gui.contains")));
								for(SubstanceStack ss:entry.getValue()) {
									allElements.add(new GuiElementTextLine(ClientStringUtil.formatSubstanceAmount(ss)));
								}
							}
							Iterator<Entry<String, List<SubstanceStack>>> odsi = ChemistryRegistry.oreDictionaryToSubstanceMap.entrySet().iterator();
							while(odsi.hasNext()) {
								Entry<String, List<SubstanceStack>> entry = odsi.next();
								String orename = entry.getKey();
								if(!OreDictionary.doesOreNameExist(orename))
									continue;
								ItemStack ore = OreDictionary.getOres(orename).iterator().next();
								allElements.add(new GuiElementItemStack(ore));
								allElements.add(new GuiElementTextLine(I18n.format("iblis.gui.contains")));
								for(SubstanceStack ss:entry.getValue()) {
									allElements.add(new GuiElementTextLine(ClientStringUtil.formatSubstanceAmount(ss)));
								}
							}
						}
						else if(value.equals("substance_list")) {
							Iterator<Substance> si = ChemistryRegistry.substancesByID.values().iterator();
							while(si.hasNext()) {
								Substance s = si.next();
								String sinfo = ClientStringUtil.formatSubstanceInfo(s);
								if(sinfo.length()>MAX_STRING_LENGTH) {
									String longDash = I18n.format("iblis.gui.longDash");
									String[] sinfoA = sinfo.split(longDash);
									allElements.add(new GuiElementTextLine(sinfoA[0]+longDash));
									allElements.add(new GuiElementTextLine(longDash+sinfoA[1]));
								}
								else {
									allElements.add(new GuiElementTextLine(sinfo));
								}
							}
						}else if(value.equals("chemical_reaction_list")) {
							Iterator<ChemicalReaction> reactionI = ChemistryRegistry.allReactions.iterator();
							while(reactionI.hasNext()) {
								ChemicalReaction reaction = reactionI.next();
								ClientStringUtil.formatReaction(reaction, allElements, MAX_STRING_LENGTH);
								allElements.add(new GuiElementHorizontalSeparator(100));
							}
						}
					}
				}
				reader.endObject();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<GuiElement> page = new ArrayList<GuiElement>();
		int pageHeight = 0;
		for(GuiElement element: allElements) {
			if(pageHeight+element.height>bookImageHeight-50) {
				pages.add(page);
				page = new ArrayList<GuiElement>();
				pageHeight = 0;
			}
			page.add(element);
			pageHeight += element.height;
		}
		pages.add(page);
	}

	public void initGui() {
		this.buttonList.clear();
		this.buttonDone = this.addButton(new GuiButton(0, this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done")));
		int x = (this.width - bookImageWidth) / 2;
		int y = 156;
		this.buttonNextPage = this.addButton(new NextPageButton(1, x + 120, y, true));
		this.buttonPreviousPage = this.addButton(new NextPageButton(2, x + 38, y, false));
		this.updateButtons();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 0) {
				this.mc.displayGuiScreen((GuiScreen) null);
			} else if (button.id == 1) {
				if (this.currPage < this.pages.size() - 1) {
					++this.currPage;
				}
			} else if (button.id == 2) {
				if (this.currPage > 0) {
					--this.currPage;
				}
			}
			this.updateButtons();
		}
	}

	private void updateButtons() {
		this.buttonNextPage.visible = this.currPage < this.pages.size() - 1;
		this.buttonPreviousPage.visible = this.currPage > 0;
		this.buttonDone.visible = true;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
		int x = (this.width - bookImageWidth) / 2;
		int y = 2;
		this.drawTexturedModalRect(x, y, 0, 0, bookImageWidth, bookImageHeight);
		x += 34;
		y += 14;
		for(GuiElement element:pages.get(this.currPage)) {
			element.render(x, y);
			y+=element.height;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static class NextPageButton extends GuiButton {
		private final boolean isForward;

		public NextPageButton(int buttonId, int x, int y, boolean isForwardIn) {
			super(buttonId, x, y, 23, 13, "");
			this.isForward = isForwardIn;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean selected = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
				int x = 0;
				int y = bookImageHeight;
				if (selected)
					x += 23;
				if (!this.isForward)
					y += 13;
				this.drawTexturedModalRect(this.x, this.y, x, y, 23, 13);
			}
		}
	}

}
