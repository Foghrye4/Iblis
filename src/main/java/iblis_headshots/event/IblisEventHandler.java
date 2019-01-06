package iblis_headshots.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import iblis_headshots.IblisHeadshotsMod;
import iblis_headshots.advacements.criterion.HeadshotTrigger;
import iblis_headshots.init.IblisParticles;
import iblis_headshots.util.HeadShotHandler;
import iblis_headshots.util.IblisItemUtils;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
		World world = victim.world;
		if (world.isRemote || damage < 0.1f || !(world instanceof WorldServer))
			return;
		Entity projectile = event.getSource().getImmediateSource();
		if (projectile != null) {
			Vec3d start = new Vec3d(
					projectile.posX - projectile.motionX, 
					projectile.posY - projectile.motionY,
					projectile.posZ - projectile.motionZ);
			Vec3d end = new Vec3d(
					projectile.posX + projectile.motionX, 
					projectile.posY + projectile.motionY,
					projectile.posZ + projectile.motionZ);
			if ((!playersHaveNoHeads||!(victim instanceof EntityPlayer)) && HeadShotHandler.traceHeadShot(victim, start, end) != null) {
				IblisHeadshotsMod.network.spawnCustomParticle(victim.world, new Vec3d(victim.posX,victim.posY+victim.getEyeHeight(),victim.posZ), new Vec3d(0d, 0.2d, 0d),
						IblisParticles.HEADSHOT);
				float multiplier = damageMultiplier;
				ItemStack headgear = victim.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				
	            HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
	            this.addModifierOfStackInSlot(victim, multimap, EntityEquipmentSlot.CHEST);
	            this.addModifierOfStackInSlot(victim, multimap, EntityEquipmentSlot.LEGS);
	            this.addModifierOfStackInSlot(victim, multimap, EntityEquipmentSlot.FEET);
	            if(!multimap.isEmpty()) {
					victim.getAttributeMap().removeAttributeModifiers(multimap);
					WorldServer wserver = (WorldServer) world;
					wserver.addScheduledTask(() -> {
			            HashMultimap<String, AttributeModifier> multimap2 = HashMultimap.<String, AttributeModifier>create();
			            this.addModifierOfStackInSlot(victim, multimap2, EntityEquipmentSlot.CHEST);
			            this.addModifierOfStackInSlot(victim, multimap2, EntityEquipmentSlot.LEGS);
			            this.addModifierOfStackInSlot(victim, multimap2, EntityEquipmentSlot.FEET);
						victim.getAttributeMap().applyAttributeModifiers(multimap2);
					});
	            }
				if (!headgear.isEmpty()) {
					float headGearDamageAbsorbMultiplier = IblisItemUtils.getHeadgearProtection(headgear);
					multiplier = 1.0f + Math.max(multiplier - 1.0f, 0.0f) * headGearDamageAbsorbMultiplier;
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
	
	private void addModifierOfStackInSlot(EntityLivingBase entity, HashMultimap<String, AttributeModifier> multimap, EntityEquipmentSlot slot) {
		ItemStack stack = entity.getItemStackFromSlot(slot);
        if(!stack.isEmpty())
        	multimap.putAll(stack.getAttributeModifiers(slot));
	}
}
