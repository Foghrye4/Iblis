package iblis.villager;

import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.oredict.OreDictionary;

public class EmeraldForOreDictionaryItems implements ITradeList {
	
    private final String buyingItem;
	private final PriceInfo priceInfo;
    
    public EmeraldForOreDictionaryItems(String buyingItemIn, PriceInfo priceInfoIn)
    {
        this.buyingItem = buyingItemIn;
        this.priceInfo = priceInfoIn;
    }

    @Override
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
    {
    	for(ItemStack stack: OreDictionary.getOres(buyingItem)){
    		ItemStack itemToBuy = stack.copy();
    		itemToBuy.setCount(priceInfo.getPrice(random));
            recipeList.add(new MerchantRecipe(itemToBuy, Items.EMERALD));
    	}
    }
}
