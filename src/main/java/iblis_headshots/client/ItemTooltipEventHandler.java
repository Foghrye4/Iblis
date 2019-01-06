package iblis_headshots.client;

import java.util.List;

import com.google.common.collect.Multimap;

import iblis_headshots.util.IblisItemUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.MathHelper;
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
	
	public final static String[] protectionLevels = new String[] {
			"iblis.protectionLevel.no",
			"iblis.protectionLevel.weak",
			"iblis.protectionLevel.miserable",
			"iblis.protectionLevel.awful",
			"iblis.protectionLevel.bad",
			"iblis.protectionLevel.normal",
			"iblis.protectionLevel.good",
			"iblis.protectionLevel.excellent",
			"iblis.protectionLevel.marvelous",
			"iblis.protectionLevel.exceptional",
			"iblis.protectionLevel.perfect"
	};

	@SubscribeEvent
	public void onItemTooltipEvent(ItemTooltipEvent event) {
		ItemStack is = event.getItemStack();
		float headGearDamageAbsorbMultiplier = IblisItemUtils.getHeadgearProtection(is);
		int absobtionPercents = MathHelper.ceil((1.0f - headGearDamageAbsorbMultiplier) * 100f);
		if (headGearDamageAbsorbMultiplier != 1.0f) {
			event.getToolTip().add(TextFormatting.LIGHT_PURPLE
					+ I18n.format("iblis.headshot_protection", absobtionPercents)
					+ "%");
			addProtectionTooltip(event.getToolTip(), absobtionPercents/10);
		}
		
	}
	
	public static void addProtectionTooltip(List<String> tooltip, int protectionRaw){
		int protection = protectionRaw;
		if(protection<0)
			protection = 0;
		if(protection>=protectionLevels.length)
			protection=protectionLevels.length;
		String protectionLevel = protectionLevels[protection];
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format(protectionLevel));
	}
}
