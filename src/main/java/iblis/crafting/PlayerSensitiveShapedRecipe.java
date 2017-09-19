package iblis.crafting;

import javax.annotation.Nonnull;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PlayerSensitiveShapedRecipe extends ShapedRecipes {

	public PlayerSensitiveShapedRecipe(String groupIn, int width, int height, NonNullList<Ingredient> ingredientsIn,
			ItemStack output) {
		super(groupIn, height, height, ingredientsIn, output);
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack output1 = getRecipeOutput().copy();
		if (!output1.hasTagCompound())
			output1.setTagCompound(new NBTTagCompound());
		EntityPlayer playerIn = IblisMod.proxy.getPlayer(inv);

		int id = playerIn.getName().hashCode();
		long timeOfCreation = playerIn.getEntityWorld().getTotalWorldTime();

		NBTTagList skillsNBT = new NBTTagList();
		for (PlayerSkills skill : PlayerSkills.values()) {
			NBTTagCompound skillNBT = new NBTTagCompound();
			skillNBT.setString("name", skill.name());
			skillNBT.setDouble("value", skill.getCurrentValue(playerIn));
			skillsNBT.appendTag(skillNBT);
		}
		output1.getTagCompound().setTag("skills", skillsNBT);
		NBTTagList exploredBooksNBTList = new NBTTagList();
		NBTTagList books = playerIn.getEntityData().getTagList("exploredBooks", 10);
		int diaryTagIndex = -1;
		for (int bookIndex = 0; bookIndex < books.tagCount(); bookIndex++) {
			NBTTagCompound playerBookNBT = books.getCompoundTagAt(bookIndex);
			int bookId = playerBookNBT.getInteger("id");
			if (id != bookId) {
				long bookVersion = playerBookNBT.getLong("timeOfCreation");
				NBTTagCompound bookInfoNBT = new NBTTagCompound();
				bookInfoNBT.setInteger("id", bookId);
				bookInfoNBT.setLong("timeOfCreation", bookVersion);
				exploredBooksNBTList.appendTag(bookInfoNBT);
			}
			else
				diaryTagIndex = bookIndex;
		}
		output1.getTagCompound().setTag("exploredBooks", exploredBooksNBTList);
		output1.getTagCompound().setInteger("id", id);
		output1.getTagCompound().setString("author", playerIn.getName());
		output1.getTagCompound().setLong("timeOfCreation", timeOfCreation);
		NBTTagCompound craftedBookInfoNBT = new NBTTagCompound();
		craftedBookInfoNBT.setInteger("id", id);
		craftedBookInfoNBT.setLong("timeOfCreation", timeOfCreation);
		if(diaryTagIndex==-1)
			books.appendTag(craftedBookInfoNBT);
		else
			books.set(diaryTagIndex, craftedBookInfoNBT);
		playerIn.getEntityData().setTag("exploredBooks", books);
		if (playerIn instanceof EntityPlayerMP)
			IblisMod.network.sendPlayerBookListInfo((EntityPlayerMP) playerIn);
		return output1;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		return super.matches(inv, worldIn);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return super.getRecipeOutput();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return super.getRemainingItems(inv);
	}
}
