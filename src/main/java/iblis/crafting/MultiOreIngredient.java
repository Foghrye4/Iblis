package iblis.crafting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class MultiOreIngredient extends Ingredient {

    private NonNullList<ItemStack> ores = NonNullList.create();
    private IntList itemIds = null;
    private ItemStack[] array = null;
    private int lastSizeA = -1, lastSizeL = -1;

    public MultiOreIngredient(String... oreNames)
    {
        super(0);
		for (String ore : oreNames)
			ores.addAll(OreDictionary.getOres(ore));
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks()
    {
        if (array == null || this.lastSizeA != ores.size())
        {
            NonNullList<ItemStack> lst = NonNullList.create();
            for (ItemStack itemstack : this.ores)
            {
                if (itemstack.getMetadata() == OreDictionary.WILDCARD_VALUE)
                    itemstack.getItem().getSubItems(CreativeTabs.SEARCH, lst);
                else
                    lst.add(itemstack);
            }
            this.array = lst.toArray(new ItemStack[lst.size()]);
            this.lastSizeA = ores.size();
        }
        return this.array;
    }


    @Override
    @Nonnull
    public IntList getValidItemStacksPacked()
    {
        if (this.itemIds == null || this.lastSizeL != ores.size())
        {
            this.itemIds = new IntArrayList(this.ores.size());

            for (ItemStack itemstack : this.ores)
            {
                if (itemstack.getMetadata() == OreDictionary.WILDCARD_VALUE)
                {
                    NonNullList<ItemStack> lst = NonNullList.create();
                    itemstack.getItem().getSubItems(CreativeTabs.SEARCH, lst);
                    for (ItemStack item : lst)
                        this.itemIds.add(RecipeItemHelper.pack(item));
                }
                else
                {
                    this.itemIds.add(RecipeItemHelper.pack(itemstack));
                }
            }

            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
            this.lastSizeL = ores.size();
        }

        return this.itemIds;
    }


    @Override
    public boolean apply(@Nullable ItemStack input)
    {
        if (input == null)
            return false;

        for (ItemStack target : this.ores)
            if (OreDictionary.itemMatches(target, input, false))
                return true;

        return false;
    }

    @Override
    protected void invalidate()
    {
        this.itemIds = null;
        this.array = null;
    }

    @Override
    public boolean isSimple()
    {
        return true;
    }
}
