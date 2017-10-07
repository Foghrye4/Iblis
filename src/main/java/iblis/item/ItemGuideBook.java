package iblis.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import iblis.IblisMod;
import iblis.init.IblisSounds;
import iblis.player.PlayerSkills;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGuideBook extends Item {

	private final static float BOOK_KNOWLEDGE_PENALTY = 0.7f;
	private final static String[] GUIDE_LEVEL = new String[] { "beginners", "novice", "experienced", "professional",
			"experts", "ultimate" };

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return stack.getMetadata() == 0 ? "item.adventurer_diary" : "item.guide";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab))
			return;
		ItemStack guideStack = new ItemStack(this, 1, 1);
		NBTTagCompound guideNBT = new NBTTagCompound();
		NBTTagList skills = new NBTTagList();
		NBTTagCompound skill = new NBTTagCompound();
		skill.setString("name", PlayerSkills.ARMORSMITH.name());
		skill.setDouble("value", 0.7d);
		skills.appendTag(skill);
		guideNBT.setTag("skills", skills);
		guideNBT.setInteger("id", 0);
		guideStack.setTagCompound(guideNBT);
		subItems.add(guideStack);
		ItemStack diaryStack = new ItemStack(this, 1, 0);
		NBTTagCompound diaryNBT = guideNBT.copy();
		diaryNBT.setInteger("id", 1);
		diaryNBT.setString("author", "Foghrye4");
		diaryStack.setTagCompound(diaryNBT);
		subItems.add(diaryStack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null)
			return;
		if (!stack.hasTagCompound())
			return;
		if (stack.getMetadata() == 0) {
			String author = stack.getTagCompound().getString("author");
			tooltip.add(I18n.format("iblis.diary", author));
		} else {
			NBTTagList skills = stack.getTagCompound().getTagList("skills", 10);
			NBTTagCompound skillNBT = skills.getCompoundTagAt(0);
			String skillName = skillNBT.getString("name");
			int skillValue = (int) (skillNBT.getDouble("value") * 3d);
			if (skillValue >= GUIDE_LEVEL.length)
				skillValue = GUIDE_LEVEL.length - 1;
			String skillNameFormatted = I18n.format("iblis." + skillName);
			String skillValueFormatted = I18n.format("iblis.guideLevel." + GUIDE_LEVEL[skillValue]);
			tooltip.add(I18n.format("iblis.guideTitle", skillValueFormatted, skillNameFormatted));
		}
		NBTTagCompound bookIn = stack.getTagCompound();
		int bookIdIn = bookIn.getInteger("id");
		long timeOfCreation = bookIn.getLong("timeOfCreation");
		long timeGap = (worldIn.getTotalWorldTime() - timeOfCreation) / 20;
		String dataOfCreation = null;
		if (timeGap < 60) {
			if (timeGap == 1)
				dataOfCreation = I18n.format("iblis.second");
			else
				dataOfCreation = I18n.format("iblis.seconds", timeGap);
		} else {
			timeGap /= 60;
			if (timeGap < 60)
				if (timeGap == 1)
					dataOfCreation = I18n.format("iblis.minute");
				else
					dataOfCreation = I18n.format("iblis.minutes", timeGap);
			else {
				timeGap /= 60;
				if (timeGap < 24)
					if (timeGap == 1)
						dataOfCreation = I18n.format("iblis.hour");
					else
						dataOfCreation = I18n.format("iblis.hours", timeGap);
				else {
					timeGap /= 24;
					if (timeGap == 1)
						dataOfCreation = I18n.format("iblis.day");
					else
						dataOfCreation = I18n.format("iblis.days", timeGap);
				}
			}
		}
		if (timeOfCreation > 0)
			tooltip.add(I18n.format("iblis.thisBookWasWritten", dataOfCreation));
		NBTTagList books = FMLClientHandler.instance().getClientPlayerEntity().getEntityData()
				.getTagList("exploredBooks", 10);
		for (int i = 0; i < books.tagCount(); i++) {
			NBTTagCompound book = books.getCompoundTagAt(i);
			int bookId = book.getInteger("id");
			if (bookIdIn == bookId) {
				tooltip.add(I18n.format("iblis.youAlreadyReadThatBook"));
				break;
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (itemstack.hasTagCompound() && !worldIn.isRemote) {
			boolean playerAlreadyReadBook = false;
			boolean bookNewVersion = false;
			NBTTagCompound bookIn = itemstack.getTagCompound();
			int bookIdIn = bookIn.getInteger("id");
			if (bookIdIn == 0) {
				bookIdIn = worldIn.rand.nextInt();
				bookIn.setInteger("id", bookIdIn);
			}
			long bookVersionIn = bookIn.getLong("timeOfCreation");
			NBTTagList books = playerIn.getEntityData().getTagList("exploredBooks", 10);
			NBTTagCompound playerBookNBT = null;
			int bookIndex = 0;
			for (; bookIndex < books.tagCount(); bookIndex++) {
				playerBookNBT = books.getCompoundTagAt(bookIndex);
				int bookId = playerBookNBT.getInteger("id");
				long bookVersion = playerBookNBT.getLong("timeOfCreation");
				if (bookIdIn == bookId) {
					playerAlreadyReadBook = true;
					if (bookVersionIn > bookVersion)
						bookNewVersion = true;
					if (bookVersionIn <= bookVersion) {
						if (bookNewVersion)
							fixBookTags(playerIn, books);
						bookNewVersion = false;
						break;
					}
				}
			}
			
			if (!playerAlreadyReadBook || bookNewVersion) {
				NBTTagList skills = bookIn.getTagList("skills", 10);
				for (int i = 0; i < skills.tagCount(); i++) {
					NBTTagCompound skillNBT = skills.getCompoundTagAt(i);
					PlayerSkills skill = PlayerSkills.valueOf(skillNBT.getString("name"));
					double skillOldValue = skill.getCurrentValue(playerIn);
					double skillBookValue = skillNBT.getDouble("value");
					if (bookNewVersion && skillBookValue * BOOK_KNOWLEDGE_PENALTY > skillOldValue) {
						skill.raiseSkillTo(playerIn, skillBookValue * BOOK_KNOWLEDGE_PENALTY);
					} else {
						skill.raiseSkillTo(playerIn, skillOldValue + skillBookValue * BOOK_KNOWLEDGE_PENALTY);
					}
				}
				if (!playerAlreadyReadBook) {
					NBTTagCompound bookInfoNBT = new NBTTagCompound();
					bookInfoNBT.setInteger("id", bookIn.getInteger("id"));
					bookInfoNBT.setLong("timeOfCreation", bookIn.getLong("timeOfCreation"));
					books.appendTag(bookInfoNBT);
				} else {
					playerBookNBT.setLong("timeOfCreation", bookVersionIn);
					books.set(bookIndex, playerBookNBT);
				}
				NBTTagList diaryBooks = bookIn.getTagList("exploredBooks", 10);
				for (int i1 = 0; i1 < diaryBooks.tagCount(); i1++) {
					if (!isNBTListContainEntryFor(books, diaryBooks.getCompoundTagAt(i1)))
						books.appendTag(diaryBooks.getCompoundTagAt(i1));
				}
				playerIn.getEntityData().setTag("exploredBooks", books);
				IblisMod.network.sendPlayerBookListInfo((EntityPlayerMP) playerIn);
				playerIn.sendMessage(new TextComponentString(I18n.format("iblis.youLearnedSomethingNew")));
			} else {
				playerIn.sendMessage(new TextComponentString(I18n.format("iblis.youAlreadyReadThatBook")));
			}
			if (itemstack.getMetadata() == 0) {
				worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.book_reading,
						SoundCategory.PLAYERS, 1.0f, 1.0f);
			} else if (itemstack.getMetadata() == 1) {
				worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.book_reading,
						SoundCategory.PLAYERS, 1.0f, 1.0f);
				itemstack.setItemDamage(2);
			} else if (itemstack.getMetadata() == 2) {
				worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.book_closing,
						SoundCategory.PLAYERS, 1.0f, 1.0f);
				itemstack.setItemDamage(1);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
	}

	private void fixBookTags(EntityPlayer playerIn, NBTTagList books) {
		NBTTagList fixed = new NBTTagList();
		Map<Integer, Long> bookToTimeOfCreation = new HashMap<Integer, Long>();
		for (int i1 = 0; i1 < books.tagCount(); i1++) {
			NBTTagCompound bookNBT = books.getCompoundTagAt(i1);
			int bookId = bookNBT.getInteger("id");
			long bookVersion = bookNBT.getLong("timeOfCreation");
			Long existingBookVersion = bookToTimeOfCreation.get(bookId);
			if (existingBookVersion == null || existingBookVersion.longValue() < bookVersion)
				bookToTimeOfCreation.put(bookId, bookVersion);
		}
		for(Entry<Integer, Long> bookToTime:bookToTimeOfCreation.entrySet()){
			NBTTagCompound bookInfoNBT = new NBTTagCompound();
			bookInfoNBT.setInteger("id", bookToTime.getKey());
			bookInfoNBT.setLong("timeOfCreation", bookToTime.getValue());
			fixed.appendTag(bookInfoNBT);
			playerIn.getEntityData().setTag("exploredBooks", fixed);
		}
	}

	private boolean isNBTListContainEntryFor(NBTTagList books, NBTTagCompound bookIn) {
		int bookInId = bookIn.getInteger("id");
		for (int i1 = 0; i1 < books.tagCount(); i1++) {
			NBTTagCompound bookNBT = books.getCompoundTagAt(i1);
			int bookId = bookNBT.getInteger("id");
			if (bookInId == bookId)
				return true;
		}
		return false;
	}
}
