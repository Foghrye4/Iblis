package iblis.villager;

import java.util.Random;

import iblis.init.IblisItems;
import iblis.player.PlayerSkills;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class EmeraldForRandomSkillBook implements ITradeList {
	
	private final PriceInfo priceInfo;
    
    public EmeraldForRandomSkillBook(PriceInfo priceInfoIn)
    {
        this.priceInfo = priceInfoIn;
    }

    @Override
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random rand)
    {
		PlayerSkills skill = PlayerSkills.values().get(rand.nextInt(PlayerSkills.values().size()));
		ItemStack guideStack = new ItemStack(IblisItems.GUIDE, 1, 1);
		NBTTagCompound guideNBT = new NBTTagCompound();
		NBTTagList skills = new NBTTagList();
		NBTTagCompound skillNBT = new NBTTagCompound();
		skillNBT.setString("name", skill.name());
		skillNBT.setDouble("value", rand.nextFloat() * rand.nextFloat() + 0.1f);
		skills.appendTag(skillNBT);
		guideNBT.setTag("skills", skills);
		guideStack.setTagCompound(guideNBT);
		recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, priceInfo.getPrice(rand), 0), guideStack));
    }
}
