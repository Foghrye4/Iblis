package iblis.event;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.entity.EntityPlayerZombie;
import iblis.init.IblisItems;
import iblis.init.IblisPotions;
import iblis.player.FoodStatsExtended;
import iblis.player.PlayerCharacteristics;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import iblis.util.ModIntegrationUtil;
import iblis.util.PlayerUtils;
import iblis.world.WorldSavedDataPlayers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class IblisEventHandler {

	public boolean spawnPlayerZombie = true;
	public boolean noDeathPenalty = false;
	public boolean noIncreasedMobSeekRange = false;
	public boolean mobReactOnlyOnShooting = false;
	
	public int lastPlayerShooterID = -1;
	public int shootingSoundCountdownTimer = -1;
	
	@SubscribeEvent
	public void onPlayerTickEvent(PlayerTickEvent event) {
		if(event.phase != TickEvent.Phase.START)
			return;
		EntityPlayer player = event.player;
		if(!IblisMod.isAppleCoreLoaded && !(player.foodStats instanceof FoodStatsExtended)) {
			FoodStats oldFoodStats = player.foodStats;
			player.foodStats = new FoodStatsExtended();
			player.foodStats.setFoodLevel(oldFoodStats.getFoodLevel());
		}
		if (event.side == Side.CLIENT)
			return;
		if (player.isSpectator())
			return;
		World world = player.world;
		if (mobReactOnlyOnShooting) {
			if (player.getEntityId() == lastPlayerShooterID && --shootingSoundCountdownTimer > 0)
				this.notifyRandomEntityAboutPlayer(world, player);
		} else {
			this.notifyRandomEntityAboutPlayer(world, player);
		}
		int knock = PlayerUtils.getKnockState(player);
		if (knock == 0)
			return;
		PlayerUtils.saveKnockState(player, 0);
		if (knock == PlayerUtils.KNOCK_BY_SHIELD && !player.isActiveItemStackBlocking())
			return;
		float power = player.getCooledAttackStrength(0);
		if (power < 0.1f)
			return;
		player.resetCooldown();
		float f = player.rotationYaw * 0.017453292F;
		double fx = (double) (-MathHelper.sin(f) * 1.2F);
		double fz = (double) (MathHelper.cos(f) * 1.2F);
		List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(fx, 0, fz),
				EntitySelectors.getTeamCollisionPredicate(player));
		double skill = 0;
		if (knock == PlayerUtils.KNOCK_BY_SHIELD) {
			skill = PlayerSkills.PARRY.getFullSkillValue(player) + 1.5d;
			IblisMod.network.launchSwingAnimation(player);
		}
		if (knock == PlayerUtils.KNOCK_BY_KICK) {
			skill = PlayerSkills.SWORDSMANSHIP.getFullSkillValue(player) + 1.5d;
			IblisMod.network.launchKickAnimation(player, power);
		}
		if (list.isEmpty())
			return;
		float playerSize = player.height * player.width;
		boolean playSound = false;
		float raiseParrySkill = 0f;
		for (Entity entity : list) {
			if (player.isRidingSameEntity(entity) || entity.noClip || entity.isBeingRidden())
				continue;
			if (!(entity instanceof EntityLivingBase))
				continue;
			EntityLivingBase living = (EntityLivingBase) entity;
			float entitySize = entity.height * entity.width;
			if (entitySize <= 0.1f)
				entitySize = 0.1f;
			double dx = entity.posX - player.posX;
			double dz = entity.posZ - player.posZ;
			dx *= fx;
			dz *= fz;
			if (dx >= 0d && dz >= 0d) {
				float strength = (float) skill * (1.0F - entity.entityCollisionReduction) * playerSize / entitySize
						* 0.2f * power;
				raiseParrySkill += entitySize;
				if (strength > 0.3f)
					living.attackEntityFrom(DamageSource.causePlayerDamage(player), strength);
				living.knockBack(player, strength, -fx, -fz);
				playSound = true;
			}
		}
		if (raiseParrySkill > 1f) {
			if (knock == PlayerUtils.KNOCK_BY_KICK)
				PlayerSkills.SWORDSMANSHIP.raiseSkill(player, raiseParrySkill);
			if (knock == PlayerUtils.KNOCK_BY_SHIELD)
				PlayerSkills.PARRY.raiseSkill(player, raiseParrySkill);
		}
		if (playSound)
			player.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
	}

	private void notifyRandomEntityAboutPlayer(World world, EntityPlayer player) {
		if (noIncreasedMobSeekRange)
			return;
		if(world.loadedEntityList.isEmpty())
			return;
		Entity randomEntity = world.loadedEntityList.get(world.rand.nextInt(world.loadedEntityList.size()));
		if (!(randomEntity instanceof EntityLiving))
			return;
		if (!(randomEntity instanceof IMob))
			return;
		EntityLiving living = (EntityLiving) randomEntity;
		Vec3d look = living.getLookVec();
		double dx = player.posX - living.posX;
		double dy = player.posY - living.posY;
		double dz = player.posZ - living.posZ;
		double aligmentFactor = look.x * dx + look.y * dy + look.z * dz;
		if (aligmentFactor < 0)
			return;
		if (!living.canEntityBeSeen(player))
			return;
		List<EntityLiving> comrads = world.getEntitiesWithinAABB(EntityLiving.class,
				(new AxisAlignedBB(living.getPosition()).grow(16, 4, 16)));
		int d = MathHelper.ceil(living.getDistanceToEntity(player));
		PotionEffect pea = new PotionEffect(IblisPotions.AWARENESS, 1200, d);
		for (EntityLivingBase comrad : comrads) {
			((EntityLiving) comrad).addPotionEffect(pea);
		}
	}

	@SubscribeEvent
	public void onPlayerGetBreakSpeed(PlayerEvent.BreakSpeed event) {
		float speed = event.getOriginalSpeed();
		if (speed <= 0.0f || !PlayerSkills.DIGGING.enabled)
			return;
		EntityPlayer player = event.getEntityPlayer();
		if (!ForgeHooks.isToolEffective(player.world, event.getPos(), player.getHeldItemMainhand()))
			return;
		double msm = PlayerSkills.DIGGING.getFullSkillValue(player);
		speed *= msm * (0.1 + player.getEntityWorld().rand.nextDouble() * 0.1) + 0.2;
		event.setNewSpeed(speed);

	}

	@SubscribeEvent
	public void onBlockBreaking(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		float hardness = event.getState().getBlockHardness(event.getWorld(), event.getPos());
		if (hardness > 0f && !player.world.isRemote)
			PlayerSkills.DIGGING.raiseSkill(player, hardness);
	}

	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (event.getDistance() > 2f && living instanceof EntityPlayerMP) {
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
		float sprintButtonState = (float) PlayerUtils.getSprintButtonCounterState(player)
				/ PlayerUtils.MAX_SPRINT_SPEED;
		multiplier *= sprintButtonState;
		multiplier++;
		living.motionX *= multiplier;
		living.motionY *= multiplier;
		living.motionZ *= multiplier;
		if (living instanceof EntityPlayerMP) {
			PlayerSkills.JUMPING.raiseSkill(player, 1f + sprintButtonState);
			if (sprintButtonState > 0.2f)
				player.addExhaustion(0.2f * sprintButtonState);
		}
	}
	
	@SubscribeEvent
	public void onHorseDismountPlayer(EntityMountEvent event) {
		if(event.isMounting())
			return;
		if(!(event.getEntityBeingMounted() instanceof AbstractHorse))
			return;
		AbstractHorse horse = (AbstractHorse) event.getEntityBeingMounted();
		if(horse.isTame())
			return;
		event.getEntityMounting().attackEntityFrom(DamageSource.causeMobDamage(horse), 1.0f);
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onLivingHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		EntityLivingBase living = event.getEntityLiving();
		if (living.world.isRemote)
			return;
		if (!(living instanceof EntityPlayerMP))
			return;
		if (event.getSource().isExplosion()) {
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.EXPLOSION_DAMAGE_REDUCTION)
					.getAttributeValue();
		}
		else if (event.getSource().isProjectile()) {
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE_REDUCTION)
					.getAttributeValue();
		}
		else if (event.getSource().isFireDamage()) {
			int amplifier = 1;
			PotionEffect overheating = living.getActivePotionEffect(IblisPotions.OVERHEATING);
			if(overheating!=null)
				amplifier += overheating.getAmplifier();
			living.addPotionEffect(new PotionEffect(IblisPotions.OVERHEATING, 200, amplifier));
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.FIRE_DAMAGE_REDUCTION)
					.getAttributeValue();
		}
		else if (event.getSource().getDamageType().equals("mob")) {
			damage -= living.getAttributeMap().getAttributeInstance(SharedIblisAttributes.MELEE_DAMAGE_REDUCTION)
					.getAttributeValue();
		}
		living.removePotionEffect(MobEffects.REGENERATION);
		event.setAmount(damage);
	}

	@SubscribeEvent
	public void onPlayerAttackEntityEvent(AttackEntityEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living.world.isRemote)
			return;
		ItemStack stackInHand = living.getHeldItemMainhand();
		Multimap<String, AttributeModifier> am = stackInHand.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		if (event.getTarget() instanceof EntityLivingBase) {
			if (am.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
				EntityLivingBase target = (EntityLivingBase) event.getTarget();
				PlayerSkills.SWORDSMANSHIP.raiseSkill(event.getEntityPlayer(), target.getAttributeMap()
						.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
			}
		}
	}

	// Called twice for players
	@SubscribeEvent
	public void onLivingEntityAttackedEvent(LivingAttackEvent event) {
		EntityLivingBase target = event.getEntityLiving();
		if (target.getEntityWorld().isRemote)
			return;
		DamageSource source = event.getSource();
		Entity shooter = source.getTrueSource();
		if (shooter == null)
			return;
		if (target instanceof EntityLiving && target instanceof IMob) {
			List<EntityLivingBase> comrads = target.world.getEntitiesWithinAABB(target.getClass(),
					(new AxisAlignedBB(target.getPosition()).grow(16, 4, 16)));
			int d = MathHelper.ceil(target.getDistanceToEntity(shooter));
			PotionEffect pea = new PotionEffect(IblisPotions.AWARENESS, 1200, d);
			for (EntityLivingBase comrad : comrads) {
				((EntityLiving) comrad).addPotionEffect(pea);
			}
		}
		if (event.getSource().isProjectile()) {
			if (event.getSource() instanceof EntityDamageSourceIndirect) {
				if (source.damageType.equals("arrow")) {
					if (shooter instanceof EntityPlayerMP) {
						PlayerSkills.ARCHERY.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				} else if (source.damageType.equals("thrown")) {
					if (shooter instanceof EntityPlayerMP) {
						PlayerSkills.THROWING.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			} else if (event.getSource() instanceof EntityDamageSource) {
				if (source.damageType.equals("shotgun") || source.damageType.equals("crossbow")) {
					if (shooter instanceof EntityPlayerMP) {
						PlayerSkills.SHARPSHOOTING.raiseSkill((EntityPlayer) shooter, target.getAttributeMap()
								.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
					}
				}
			}
		}
		if (target instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) target;
			IAttributeInstance kr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
			double svalue = PlayerSkills.EQUILIBRIUM.getFullSkillValue(player);
			kr.removeModifier(SharedIblisAttributes.EQUILIBRIUM_KNOCKBACK_MODIFIER);
			kr.applyModifier(new AttributeModifier(
					SharedIblisAttributes.EQUILIBRIUM_KNOCKBACK_MODIFIER, "Equilibrium modifier",
					1 - 1 / ++svalue, 0));
			if (player.canBlockDamageSource(source))
				PlayerSkills.PARRY.raiseSkill(player, 1.0d);
			PlayerSkills.EQUILIBRIUM.raiseSkill(player, 1.0d);
		}
	}

	@SubscribeEvent
	public void onNockEvent(LivingEntityUseItemEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer))
			return;
		boolean isBow = event.getItem().getItem() instanceof ItemBow;
		boolean isCrossbow = event.getItem().getItem() == IblisItems.CROSSBOW_RELOADING;
		if (!(isBow || isCrossbow))
			return;
		if (isBow && !PlayerSkills.ARCHERY.enabled)
			return;
		if (isCrossbow && !PlayerSkills.SHARPSHOOTING.enabled)
			return;
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		double skillValue = 0d;
		if(isBow)
			skillValue = PlayerSkills.ARCHERY.getFullSkillValue(player) + 1.0d;
		else
			skillValue = PlayerSkills.SHARPSHOOTING.getFullSkillValue(player) + 1.0d;
		int duration = event.getDuration();
		// To avoid skipping crossbow reload animation
		if (isCrossbow && duration <= 6)
			return;
		if (duration % (int) (128.0d / skillValue) == 0)
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
		if (event.getWorld().isRemote)
			return;
		if (ModIntegrationUtil.isArrow(event.getEntity())) {
			if (!PlayerSkills.ARCHERY.enabled && !PlayerSkills.MECHANICS.enabled)
				return;
			EntityArrow arrow = (EntityArrow) event.getEntity();
			if (arrow.shootingEntity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) arrow.shootingEntity;
				double arrowDamage = (arrow.getDamage() - 3.0d + player.getAttributeMap()
						.getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE).getAttributeValue());
				if (PlayerSkills.ARCHERY.enabled)
					arrowDamage *= PlayerSkills.ARCHERY.getFullSkillValue(player) + 0.2d;
				if (arrowDamage > 0.25d)
					arrow.setDamage(arrowDamage);
				else
					arrow.setDamage(0.25d);
			} else {
				arrow.setDamage(arrow.getDamage() * 0.5);
			}
		}
		else if(ModIntegrationUtil.isCustomModBolt(event.getEntity())){
			EntityArrow bolt = (EntityArrow) event.getEntity();
			if (bolt.shootingEntity instanceof EntityPlayerMP && PlayerSkills.SHARPSHOOTING.enabled) {
				EntityPlayerMP player = (EntityPlayerMP) bolt.shootingEntity;
				double divider = PlayerUtils.getShootingAccuracyDivider(player) / 10f;
				double aimRandX = event.getWorld().rand.nextDouble() - 0.5d;
				double aimRandY = event.getWorld().rand.nextDouble() - 0.5d;
				double aimRandZ = event.getWorld().rand.nextDouble() - 0.5d;
				aimRandX/=divider;
				aimRandY/=divider;
				aimRandZ/=divider;
				bolt.motionX+=aimRandX;
				bolt.motionY+=aimRandY;
				bolt.motionZ+=aimRandZ;
				PlayerSkills.SHARPSHOOTING.raiseSkill(player, 1.0);
			}
		}
		else if(ModIntegrationUtil.isCustomModThrowable(event.getEntity())){
			EntityArrow bolt = (EntityArrow) event.getEntity();
			if (bolt.shootingEntity instanceof EntityPlayerMP && PlayerSkills.THROWING.enabled) {
				EntityPlayerMP player = (EntityPlayerMP) bolt.shootingEntity;
				double skill = PlayerSkills.THROWING.getFullSkillValue(player) + 1.0d;
				double divider = skill / 2f;
				float speedMultiplier = (float) skill / 10f;
				double aimRandX = event.getWorld().rand.nextDouble() - 0.5d;
				double aimRandY = event.getWorld().rand.nextDouble() - 0.5d;
				double aimRandZ = event.getWorld().rand.nextDouble() - 0.5d;
				aimRandX/=divider;
				aimRandY/=divider;
				aimRandZ/=divider;	
				bolt.motionX+=aimRandX;
				bolt.motionY+=aimRandY;
				bolt.motionZ+=aimRandZ;
				bolt.motionX*=speedMultiplier;
				bolt.motionY*=speedMultiplier;
				bolt.motionZ*=speedMultiplier;
				PlayerSkills.THROWING.raiseSkill(player, 1.0);
			}
		}
		if (event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			IAttributeInstance aiAttackDamage = player.getAttributeMap()
					.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
			aiAttackDamage.removeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER);
			if (PlayerCharacteristics.MELEE_DAMAGE_BONUS.enabled)
				aiAttackDamage.applyModifier(new AttributeModifier(
						SharedIblisAttributes.ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER, "Characteristic modifier",
						PlayerCharacteristics.MELEE_DAMAGE_BONUS.getCurrentValue(player), 1));

			WorldSavedDataPlayers playersData = (WorldSavedDataPlayers) IblisMod.proxy.getServer().worlds[0].getPerWorldStorage()
					.getOrLoadData(WorldSavedDataPlayers.class, WorldSavedDataPlayers.DATA_IDENTIFIER);
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
			if (amto.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName()) && PlayerSkills.SWORDSMANSHIP.enabled) {
				aiAttackDamage
						.applyModifier(new AttributeModifier(SharedIblisAttributes.ATTACK_DAMAGE_BY_SKILL_MODIFIER,
								"Weapon skill modifier", PlayerSkills.SWORDSMANSHIP.getFullSkillValue(player), 0));
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerChangeDimension(EntityTravelToDimensionEvent event){
		if (!(event.getEntity() instanceof EntityPlayerMP))
			return;
		this.savePlayerData((EntityPlayerMP) event.getEntity());
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
		this.savePlayerData(player);
		if (spawnPlayerZombie) {
			EntityPlayerZombie playerZombie = new EntityPlayerZombie(player, noDeathPenalty);
			player.world.spawnEntity(playerZombie);
		}
	}
	
	private void savePlayerData(EntityPlayerMP player){
		if (noDeathPenalty) {
			WorldSavedDataPlayers playersData = PlayerUtils.getOrCreateWorldSavedData(IblisMod.proxy.getServer().worlds[0]);
			playersData.playerDataKeys.add(player.getUniqueID());
			playersData.playerDataAttributes.put(player.getUniqueID(),
					SharedMonsterAttributes.writeBaseAttributeMapToNBT(player.getAttributeMap()));
			playersData.markDirty();
		}
		
	}
}
