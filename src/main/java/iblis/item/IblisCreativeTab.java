package iblis.item;

import iblis.init.IblisItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class IblisCreativeTab extends CreativeTabs {

	public IblisCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(IblisItems.GUIDE, 1, 1);
	}
}
