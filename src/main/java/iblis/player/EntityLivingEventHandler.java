package iblis.player;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityLivingEventHandler {

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		EntityLivingBase living = event.getEntityLiving();
		if (event.getSource().isExplosion())
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.EXPLOSION_DAMAGE_REDUCTION)
					.getAttributeValue();
		else if (event.getSource().isProjectile())
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE_REDUCTION)
					.getAttributeValue();
		else if (event.getSource().isFireDamage())
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.FIRE_DAMAGE_REDUCTION)
					.getAttributeValue();
		else if (event.getSource().getDamageType().equals("mob"))
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.MELEE_DAMAGE_REDUCTION)
					.getAttributeValue();
		event.setAmount(damage);
	}

	@SubscribeEvent
	public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		ItemStack stackInHand = living.getHeldItemMainhand();
		Multimap<String, AttributeModifier> am = stackInHand.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		if (event.getTarget() instanceof EntityLivingBase) {
			if (am.containsKey(SharedMonsterAttributes.MAX_HEALTH.getName())) {
				EntityLivingBase target = (EntityLivingBase) event.getTarget();
				PlayerSkills.SWORDSMANSHIP.raiseSkill(event.getEntityPlayer(),
						target.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
			}
		}
	}

	@SubscribeEvent
	public void onLivingEntityAttackedEvent(LivingAttackEvent event) {
		if (event.getSource().isProjectile()) {
			if (event.getSource() instanceof EntityDamageSourceIndirect) {
				EntityDamageSourceIndirect dsi = (EntityDamageSourceIndirect) event.getSource();
				if (dsi.damageType.equals("arrow")) {
					Entity shooter = dsi.getEntity();
					if (shooter instanceof EntityPlayer) {
						EntityLivingBase target = (EntityLivingBase) event.getEntity();
						PlayerSkills.ARCHERY.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			}
			else if (event.getSource() instanceof EntityDamageSource) {
				EntityDamageSource dsi = (EntityDamageSource) event.getSource();
				if (dsi.damageType.equals("shotgun")) {
					Entity shooter = dsi.getEntity();
					if (shooter instanceof EntityPlayer) {
						EntityLivingBase target = (EntityLivingBase) event.getEntity();
						PlayerSkills.SHARPSHOOTING.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onArrowLooseEvent(ArrowLooseEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		event.setCharge(event.getCharge() - 5 + (int) PlayerSkills.ARCHERY.getFullSkillValue(living) / 2);
	}

	@SubscribeEvent
	public void onEntityConstructingEvent(EntityEvent.EntityConstructing event) {
		if (event.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) event.getEntity();
			SharedIblisAttributes.registerAttributes(living.getAttributeMap());
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) event.getEntity();
			if (arrow.shootingEntity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) arrow.shootingEntity;
				double arrowDamage = (arrow.getDamage() - 2.0d + living.getAttributeMap()
						.getAttributeInstance(SharedIblisAttributes.ARROW_DAMAGE).getAttributeValue())
						* (PlayerSkills.ARCHERY.getFullSkillValue(living) + 0.2d);
				if (arrowDamage > 0.5d)
					arrow.setDamage(arrowDamage);
				else
					arrow.setDamage(0.5d);
			}
		}
	}

	@SubscribeEvent
	public void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
		Multimap<String, AttributeModifier> amto = event.getTo().getAttributeModifiers(event.getSlot());
		if (event.getSlot().getSlotType() == EntityEquipmentSlot.Type.HAND) {
			IAttributeInstance aiAttackDamage = event.getEntityLiving().getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
			aiAttackDamage.removeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_SKILL_MODIFIER);
			if (amto.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
				aiAttackDamage.applyModifier(new AttributeModifier(
						SharedIblisAttributes.ATTACK_DAMAGE_BY_SKILL_MODIFIER, "Weapon skill modifier",
						PlayerSkills.SWORDSMANSHIP.getFullSkillValue(event.getEntityLiving()), 0));
			}
		}
	}
}
