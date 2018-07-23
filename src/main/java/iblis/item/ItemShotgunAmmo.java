package iblis.item;

import java.util.List;

import iblis.client.ItemTooltipEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemShotgunAmmo extends ItemAmmoBase {

	private final int damageBase;
	private final int ammoType;

	public ItemShotgunAmmo(int damageBaseIn, int ammoTypeIn) {
		super();
		damageBase = damageBaseIn;
		ammoType = ammoTypeIn;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null)
			return;
		tooltip.add(I18n.format("iblis.ammo_damage", this.getAmmoDamage(stack)));
		ItemTooltipEventHandler.addQualityTooltip(tooltip, this.getQuality(stack));
	}

	public float getAmmoDamage(ItemStack stack) {
		float a = stack.getMetadata() * 0.1f + 1.0f;
		return damageBase * a * a;
	}

	@Override
	public int getAmmoType(ItemStack stack) {
		return ammoType;
	}

	@Override
	public int getQuality(ItemStack stack) {
		return stack.getMetadata() - 5;
	}
}
