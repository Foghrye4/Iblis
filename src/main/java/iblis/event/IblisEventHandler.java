package iblis.event;

import com.google.common.collect.Multimap;

import iblis.IblisMod;
import iblis.advacements.criterion.HeadshotTrigger;
import iblis.init.IblisParticles;
import iblis.util.HeadShotHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisEventHandler {

	public static float damageMultiplier = 4.0f;
	public static float missMultiplier = 1.0f;
	public static boolean playersHaveNoHeads = false;

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		EntityLivingBase victim = event.getEntityLiving();
		if (victim.world.isRemote || damage < 0.1f)
			return;
		Entity projectile = event.getSource().getImmediateSource();
		if (projectile != null) {
			Vec3d start = new Vec3d(projectile.posX, projectile.posY, projectile.posZ);
			Vec3d end = new Vec3d(projectile.posX + projectile.motionX, projectile.posY + projectile.motionY,
					projectile.posZ + projectile.motionZ);
			if ((!playersHaveNoHeads||!(victim instanceof EntityPlayer)) && HeadShotHandler.traceHeadShot(victim, start, end) != null) {
				IblisMod.network.spawnCustomParticle(victim.world, start, new Vec3d(0d, 0.2d, 0d),
						IblisParticles.HEADSHOT);
				float multiplier = damageMultiplier;
				ItemStack headgear = victim.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				float totalArmorValue = (float) victim.getTotalArmorValue();
				float totalToughtnessValue = (float) victim.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
						.getAttributeValue();
				float damageAbsorbMultiplier = CombatRules.getDamageAfterAbsorb(1.0f, totalArmorValue,
						totalToughtnessValue);
				damage /= damageAbsorbMultiplier;
				if (!victim.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
					Multimap<String, AttributeModifier> aMods = headgear
							.getAttributeModifiers(EntityEquipmentSlot.HEAD);
					victim.getAttributeMap().removeAttributeModifiers(aMods);
					float headgearArmor = totalArmorValue - victim.getTotalArmorValue();
					float headgearArmorToughtness = totalToughtnessValue - (float) victim
							.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
					victim.getAttributeMap().applyAttributeModifiers(aMods);
					float headGearDamageAbsorbMultiplier = CombatRules.getDamageAfterAbsorb(1.0f,
							headgearArmor, headgearArmorToughtness);
					float headGearDamageAbsorbMultiplier2 = headGearDamageAbsorbMultiplier
							* headGearDamageAbsorbMultiplier;
					headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
					headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
					headGearDamageAbsorbMultiplier2 *= headGearDamageAbsorbMultiplier2;
					multiplier = 1.0f + Math.max(multiplier - 1.0f, 0.0f) * headGearDamageAbsorbMultiplier2;
					damage *= headGearDamageAbsorbMultiplier;
					headgear.damageItem((int) (damage * 4.0F + victim.world.rand.nextFloat() * damage * 2.0F), victim);
				}
				damage *= multiplier;
				Entity shooter = event.getSource().getTrueSource();
				if (shooter instanceof EntityPlayerMP && !(victim instanceof EntityPlayerMP)) {
					HeadshotTrigger.instance.trigger((EntityPlayerMP) shooter, victim);
				} else if (victim instanceof EntityPlayerMP) {
					HeadshotTrigger.instance.trigger((EntityPlayerMP) victim, victim);
				}
				if (victim.getHealth() < damage && victim instanceof EntitySlime
						&& ((EntitySlime) victim).getSlimeSize() > 1) {
					((EntitySlime) victim).setSlimeSize(0, false);
				}
			} else {
				damage *= missMultiplier;
			}
			event.setAmount(damage);
		}
	}
}
