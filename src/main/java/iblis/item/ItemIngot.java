package iblis.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIngot extends Item {
	public static final int STEEL = 0;
	public static final int BRONZE = 1;
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		switch(stack.getMetadata()){
		case STEEL:
			return "ingot_steel";
		case BRONZE:
			return "ingot_bronze";
		}
		return super.getUnlocalizedName(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab))
			return;
		ItemStack steel = new ItemStack(this, 1, STEEL);
		subItems.add(steel);
		ItemStack bronze = new ItemStack(this, 1, BRONZE);
		subItems.add(bronze);
	}

}
