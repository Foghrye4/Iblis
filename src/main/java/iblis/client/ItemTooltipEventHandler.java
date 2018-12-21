package iblis.client;

import java.util.List;

import com.google.common.collect.Multimap;

import iblis.util.IblisItemUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ItemTooltipEventHandler {
	
	AttributeMap attributeMap = new AttributeMap();
	IAttributeInstance armor = new ModifiableAttributeInstance(attributeMap, SharedMonsterAttributes.ARMOR);
	IAttributeInstance armorToughness = new ModifiableAttributeInstance(attributeMap, SharedMonsterAttributes.ARMOR);
	
	public final static String[] qualityLevels = new String[] {
			"iblis.qualityLevel.worthless",
			"iblis.qualityLevel.trash",
			"iblis.qualityLevel.miserable",
			"iblis.qualityLevel.awful",
			"iblis.qualityLevel.bad",
			"iblis.qualityLevel.normal",
			"iblis.qualityLevel.good",
			"iblis.qualityLevel.excellent",
			"iblis.qualityLevel.marvelous",
			"iblis.qualityLevel.exceptional",
			"iblis.qualityLevel.perfect"
	};

	@SubscribeEvent
	public void onItemTooltipEvent(ItemTooltipEvent event) {
		ItemStack is = event.getItemStack();
		if (is.hasTagCompound() && is.getTagCompound().hasKey("quality")) {
			int quality = is.getTagCompound().getInteger("quality");
			addQualityTooltip(event.getToolTip(), quality);
		}
		float headGearDamageAbsorbMultiplier = IblisItemUtils.getHeadgearProtection(is);
		if (headGearDamageAbsorbMultiplier != 1.0f)
			event.getToolTip().add(TextFormatting.LIGHT_PURPLE
					+ I18n.format("iblis.headshot_protection", (int) ((1.0f - headGearDamageAbsorbMultiplier) * 100f))
					+ "%");
	}
	
	public static void addQualityTooltip(List<String> tooltip, int qualityRaw){
		int quality = qualityRaw;
		if(quality<-5)
			quality = -5;
		if(quality>5)
			quality = 5;
		String qualityLevel = qualityLevels[quality+5];
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("iblis.quality", I18n.format(qualityLevel), qualityRaw));
	}
}
