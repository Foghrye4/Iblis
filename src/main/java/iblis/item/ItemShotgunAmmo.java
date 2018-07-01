package iblis.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShotgunAmmo extends Item {

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		switch (stack.getMetadata()) {
		case 0:
			return "item.shotgun_bullet";
		case 1:
			return "item.shotgun_shot";
		}
		return "item.null";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (!this.isInCreativeTab(tab))
			return;
		for (int i = 0; i < 2; i++)
			subItems.add(new ItemStack(this, 64, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null)
			return;
		tooltip.add(I18n.format("iblis.ammo_damage", this.getAmmoDamage(stack)));
	}

	public float getAmmoDamage(ItemStack stack) {
		switch (stack.getMetadata()) {
		case 0:
			return 2.0f;
		case 1:
			return 1.0f;
		}
		return 2.0f;
	}
}
