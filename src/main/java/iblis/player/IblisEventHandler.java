package iblis.player;

import com.google.common.collect.Multimap;

import iblis.util.NBTTagsKeys;
import iblis.world.WorldSavedDataPlayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IblisEventHandler {
	
	public boolean spawnPlayerZombie = true;
	public boolean noDeathPenalty = false;
	
	@SubscribeEvent
	public void onPlayerGetBreakSpeed(PlayerEvent.BreakSpeed event){
		float speed = event.getOriginalSpeed();
		if (speed <= 0.0f || !PlayerSkills.DIGGING.enabled)
			return;
		EntityPlayer player = event.getEntityPlayer();
		if (!ForgeHooks.isToolEffective(player.world, event.getPos(), player.getHeldItemMainhand()))
			return;
		double msm = PlayerSkills.DIGGING.getFullSkillValue(player);
		speed *= msm * 0.1 + 0.2;
		event.setNewSpeed(speed);
		
	}
	
	@SubscribeEvent
	public void onBlockBreaking(BlockEvent.BreakEvent event){
		EntityPlayer player = event.getPlayer();
		float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
		if (hardness > 0f && !player.world.isRemote)
			PlayerSkills.DIGGING.raiseSkill(player, hardness);
	}

	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (event.getDistance() > 2f && living instanceof EntityPlayerMP){
			EntityPlayer player = (EntityPlayer) living;
			PlayerSkills.FALLING.raiseSkill(player, 1d);
			float distance = event.getDistance();
			distance -= PlayerSkills.FALLING.getFullSkillValue(player);
			event.setDistance(distance);
		}
	}
	
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (!(living instanceof EntityPlayer) || !PlayerSkills.JUMPING.enabled)
			return;
		EntityPlayer player = (EntityPlayer) living;
		if (!PlayerUtils.canJump(player))
			return;
		double multiplier = PlayerSkills.JUMPING.getFullSkillValue(player) * 0.1;
		float sprintButtonState = (float)PlayerUtils.getSprintButtonCounterState(player) / PlayerUtils.MAX_SPRINT_SPEED;
		multiplier *= sprintButtonState;
		multiplier++;
		living.motionX *= multiplier;
		living.motionY *= multiplier;
		living.motionZ *= multiplier;
		if (living instanceof EntityPlayerMP) {
			PlayerSkills.JUMPING.raiseSkill(player, 1f + sprintButtonState);
			if(sprintButtonState > 0.2f)
				player.addExhaustion(0.2f * sprintButtonState);
		}
	}	

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		EntityLivingBase living = event.getEntityLiving();
		if (!(living instanceof EntityPlayerMP))
			return;
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
		living.removePotionEffect(MobEffects.REGENERATION);
		event.setAmount(damage);
	}

	@SubscribeEvent
	public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(living.world.isRemote)
			return;
		ItemStack stackInHand = living.getHeldItemMainhand();
		Multimap<String, AttributeModifier> am = stackInHand.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		if (event.getTarget() instanceof EntityLivingBase) {
			if (am.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
				EntityLivingBase target = (EntityLivingBase) event.getTarget();
				PlayerSkills.SWORDSMANSHIP.raiseSkill(event.getEntityPlayer(),
						target.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
			}
		}
	}

	//Called twice for players
	@SubscribeEvent
	public void onLivingEntityAttackedEvent(LivingAttackEvent event) {
		EntityLivingBase target = event.getEntityLiving();
		if (event.getSource().isProjectile()) {
			if (event.getSource() instanceof EntityDamageSourceIndirect) {
				EntityDamageSourceIndirect dsi = (EntityDamageSourceIndirect) event.getSource();
				if (dsi.damageType.equals("arrow")) {
					Entity shooter = dsi.getTrueSource();
					if (shooter instanceof EntityPlayerMP) {
						PlayerSkills.ARCHERY.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			}
			else if (event.getSource() instanceof EntityDamageSource) {
				EntityDamageSource dsi = (EntityDamageSource) event.getSource();
				if (dsi.damageType.equals("shotgun")) {
					Entity shooter = dsi.getTrueSource();
					if (shooter instanceof EntityPlayerMP) {
						PlayerSkills.SHARPSHOOTING.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onNockEvent(LivingEntityUseItemEvent event) {
		if(!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		if(!(event.getItem().getItem() instanceof ItemBow))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		double skillValue = PlayerSkills.ARCHERY.getFullSkillValue(player) + 1.0d;
		if(event.getDuration() % (int)(128.0d / skillValue) == 0)
			event.setDuration(event.getDuration() - 1);
	}


	@SubscribeEvent
	public void onEntityConstructingEvent(EntityEvent.EntityConstructing event) {
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			SharedIblisAttributes.registerAttributes(player.getAttributeMap());
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) event.getEntity();
			if (arrow.shootingEntity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) arrow.shootingEntity;
				double arrowDamage = (arrow.getDamage() - 2.0d + player.getAttributeMap()
						.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue())
						* (PlayerSkills.ARCHERY.getFullSkillValue(player) + 0.2d);
				if (arrowDamage > 0.5d)
					arrow.setDamage(arrowDamage);
				else
					arrow.setDamage(0.5d);
			}
		}
		if (event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			IAttributeInstance aiAttackDamage = player.getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
			aiAttackDamage.removeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER);
			aiAttackDamage.applyModifier(new AttributeModifier(
					SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER, "Characteristic modifier",
					PlayerCharacteristics.MELEE_DAMAGE_BONUS.getCurrentValue(player), 1));
			
			World worldIn = player.world;
			WorldSavedDataPlayers playersData = (WorldSavedDataPlayers) worldIn.getPerWorldStorage().getOrLoadData(WorldSavedDataPlayers.class, WorldSavedDataPlayers.DATA_IDENTIFIER);
			NBTTagList attributesNBTList = null;
			NBTTagList books = null;
			if (playersData != null) {
				attributesNBTList = playersData.playerDataAttributes.remove(player.getUniqueID());
				books = playersData.playerDataBooks.remove(player.getUniqueID());
				playersData.markDirty();
			}
			if (attributesNBTList != null && noDeathPenalty)
				SharedMonsterAttributes.setAttributeModifiers(player.getAttributeMap(), attributesNBTList);
			if (books != null && noDeathPenalty)
				player.getEntityData().setTag(NBTTagsKeys.EXPLORED_BOOKS, books);
		}
	}

	@SubscribeEvent
	public void onLivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.getEntity();
		Multimap<String, AttributeModifier> amto = event.getTo().getAttributeModifiers(event.getSlot());
		if (event.getSlot().getSlotType() == EntityEquipmentSlot.Type.HAND) {
			IAttributeInstance aiAttackDamage = event.getEntityLiving().getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
			aiAttackDamage.removeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_SKILL_MODIFIER);
			if (amto.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
				aiAttackDamage.applyModifier(new AttributeModifier(
						SharedIblisAttributes.ATTACK_DAMAGE_BY_SKILL_MODIFIER, "Weapon skill modifier",
						PlayerSkills.SWORDSMANSHIP.getFullSkillValue(player), 0));
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayerMP))
			return;
		if (noDeathPenalty) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			World worldIn = event.getEntity().world;
			WorldSavedDataPlayers playersData = PlayerUtils.getOrCreateWorldSavedData(worldIn);
			playersData.playerDataKeys.add(player.getUniqueID());
			playersData.playerDataAttributes.put(player.getUniqueID(), SharedMonsterAttributes.writeBaseAttributeMapToNBT(player.getAttributeMap()));
			playersData.markDirty();
		}
		if (spawnPlayerZombie) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			EntityPlayerZombie playerZombie = new EntityPlayerZombie(player, noDeathPenalty);
			player.world.spawnEntity(playerZombie);
		}
	}
}
