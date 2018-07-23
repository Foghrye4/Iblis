package iblis.item;

import java.util.List;

import iblis.client.ItemTooltipEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemAmmoBase extends Item {
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null)
			return;
		int quality, qualityRaw = this.getQuality(stack);
		quality = qualityRaw;
		if(quality<-5)
			quality = -5;
		if(quality>5)
			quality = 5;
		String qualityLevel = ItemTooltipEventHandler.qualityLevels[quality+5];
		tooltip.add(I18n.format("iblis.ammo_damage", this.getAmmoDamage(stack)));
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("iblis.quality", I18n.format(qualityLevel), qualityRaw));
	}

	abstract float getAmmoDamage(ItemStack stack);
	abstract int getAmmoType(ItemStack stack);
	abstract int getQuality(ItemStack stack);
}
