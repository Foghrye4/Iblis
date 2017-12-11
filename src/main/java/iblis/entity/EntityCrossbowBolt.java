package iblis.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import iblis.ClientProxy;
import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.init.IblisParticles;
import iblis.init.IblisSounds;
import iblis.util.HeadShotHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrossbowBolt extends EntityArrow {
	@SuppressWarnings("unchecked")
	private static final com.google.common.base.Predicate<Entity> TARGETS = Predicates
			.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
				@Override
				public boolean apply(@Nullable Entity entity) {
					return entity.canBeCollidedWith();
				}

				@Override
				public boolean test(@Nullable Entity input) {
					return apply(input);
				}
			});
	private boolean keepYawAndPitch = false;
	private float yaw = 0;
	private float pitch = 0;
	private IBlockState inBlock;
	private BlockPos blockPos = BlockPos.ORIGIN;
	public boolean onHardSurface = false;

	public EntityCrossbowBolt(World worldIn) {
		super(worldIn);
	}

	public EntityCrossbowBolt(World worldIn, EntityPlayer playerIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.shootingEntity = playerIn;
		if (playerIn instanceof EntityPlayer) {
			this.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}

	public void onUpdate() {
		if (this.inGround) {
			if (world.getBlockState(blockPos) != inBlock) {
				this.inGround = false;
				this.onHardSurface = false;
				return;
			}
		} else {
			super.onUpdate();
		}
		if (keepYawAndPitch) {
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = this.pitch;
			this.rotationYaw = this.yaw;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id != 3)
			return;
		for (int i = 0; i < 8; ++i) {
			ClientProxy cproxy = (ClientProxy) IblisMod.proxy;
			double ppx = this.lastTickPosX - this.motionX;
			double ppy = this.lastTickPosY - this.motionY;
			double ppz = this.lastTickPosZ - this.motionZ;
			cproxy.spawnParticle(IblisParticles.SPARK, ppx, ppy, ppz,
					this.world.rand.nextDouble() - 0.5d - this.motionX*0.1d, this.world.rand.nextDouble() - 0.75d,
					this.world.rand.nextDouble() - 0.5d - this.motionZ*0.1d);
		}
	}

	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(IblisItems.CROSSBOW_BOLT);
	}

	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
		Entity target = null;
		double closestDistanceTo = Double.MAX_VALUE;
		List<Entity> list = world.getEntitiesInAABBexcluding(this, (new AxisAlignedBB(start, end)).grow(0.5d), TARGETS);
		Iterator<Entity> ei = list.iterator();
		while (ei.hasNext()) {
			Entity entity = ei.next();
			if (entity == this.shootingEntity)
				continue;
			RayTraceResult raytraceresult = null;
			if (entity instanceof EntityLivingBase) {
				raytraceresult = HeadShotHandler.traceHeadShot((EntityLivingBase) entity, start, end);
			}
			if (raytraceresult == null) {
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
				raytraceresult = axisalignedbb.calculateIntercept(start, end);
			}
			if (raytraceresult != null) {
				double d1 = start.squareDistanceTo(raytraceresult.hitVec);
				if (d1 < closestDistanceTo) {
					closestDistanceTo = d1;
					target = entity;
				}
			}
		}
		return target;
	}

	protected void onHit(RayTraceResult result) {
		if (result.entityHit != null) {
			DamageSource damageSource = DamageSource.causeThrownDamage(this, this.shootingEntity);
			damageSource.damageType = "crossbow";
			result.entityHit.attackEntityFrom(damageSource,
					(float) this.getDamage());
			world.playSound(null, result.hitVec.x, result.hitVec.y, result.hitVec.z, IblisSounds.knife_impact,
					SoundCategory.PLAYERS, 1.0f, world.rand.nextFloat() * 0.4f + 0.6f);
			if (!world.isRemote)
				this.setDead();
		}
		if (result.typeOfHit == Type.BLOCK) {
			double mx = this.motionX;
			double my = this.motionY;
			double mz = this.motionZ;
			double velocitySq = mx * mx + my * my + mz * mz;
			IBlockState state = world.getBlockState(result.getBlockPos());
			boolean isHardSurface = state.getMaterial() == Material.GLASS || state.getMaterial() == Material.ANVIL
					|| state.getMaterial() == Material.IRON 
					|| state.getMaterial() == Material.ROCK 
					|| state.getMaterial() == Material.CLAY;
			if (isHardSurface) {
				switch (result.sideHit) {
				case NORTH:
				case SOUTH:
					mz = -mz;
					break;
				case WEST:
				case EAST:
					mx = -mx;
					break;
				default:
					my = -my;
				}
				this.motionX = mx * 0.4;
				this.motionY = my * 0.4 - 0.1;
				this.motionZ = mz * 0.4;
				this.keepYawAndPitch = true;
				this.pitch = this.rotationPitch;
				this.yaw = this.rotationYaw;
				world.playSound(null, result.hitVec.x, result.hitVec.y, result.hitVec.z, IblisSounds.knife_impact_stone,
						SoundCategory.PLAYERS, 1.0f, 1.0f);
				if (!this.world.isRemote)
					this.world.setEntityState(this, (byte) 3);
				if (!this.world.isRemote && velocitySq > 3) {
					IblisMod.network.spawnCustomParticles(getEntityWorld(), result.hitVec, new Vec3d(mx, my, mz),
							IblisParticles.SLIVER);
					this.setDead();
				}
				return;
			} else {
				this.motionX = (double) ((float) (result.hitVec.x - this.posX));
				this.motionY = (double) ((float) (result.hitVec.y - this.posY));
				this.motionZ = (double) ((float) (result.hitVec.z - this.posZ));
				this.posX -= this.motionX / (double) velocitySq * 0.05;
				this.posY -= this.motionY / (double) velocitySq * 0.05;
				this.posZ -= this.motionZ / (double) velocitySq * 0.05;
	            this.inBlock = state;
	            this.blockPos = result.getBlockPos();
				this.setIsCritical(false);
				this.inGround = true;
				if (isHardSurface) {
					List<AxisAlignedBB> collidingBoxes = new ArrayList<AxisAlignedBB>();
					state.addCollisionBoxToList(world, blockPos, getEntityBoundingBox().expand(1, 1, 1), collidingBoxes,
							this, false);
					double minY = Double.MIN_VALUE;
					for (AxisAlignedBB aabb : collidingBoxes)
						if (aabb.minY > minY)
							minY = aabb.minY;//result.sideHit == EnumFacing.UP && 
					if (minY != Double.MIN_VALUE) {
						this.posY = minY + 0.1;
					}
					this.onHardSurface = true;
				} else {
					this.playSound(IblisSounds.knife_impact, 0.5F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
				}
				if (state.getMaterial() != Material.AIR) {
					state.getBlock().onEntityCollidedWithBlock(this.world, result.getBlockPos(), state, this);
				}
			}
		}
	}
}
