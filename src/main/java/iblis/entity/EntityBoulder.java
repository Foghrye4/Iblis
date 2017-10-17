package iblis.entity;

import iblis.ClientProxy;
import iblis.IblisMod;
import iblis.init.IblisParticles;
import iblis.init.IblisSounds;
import iblis.player.PlayerSkills;
import iblis.player.SharedIblisAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBoulder extends EntityThrowable {

	private float damage = 0f;
	private EntityPlayer thrower;

	public EntityBoulder(World worldIn) {
		super(worldIn);
		this.setSize(0.4f, 0.4f);
	}

	public EntityBoulder(World worldIn, EntityPlayer playerIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		double damageBase = playerIn.getAttributeMap().getAttributeInstance(SharedIblisAttributes.PROJECTILE_DAMAGE)
				.getAttributeValue();
		double skill = PlayerSkills.THROWING.getFullSkillValue(playerIn);
		this.damage = (float) (damageBase * (skill + 0.2));
		this.ignoreEntity = playerIn;
		this.thrower = playerIn;
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
			cproxy.spawnParticle(IblisParticles.BOULDER, ppx, ppy, ppz,
					this.world.rand.nextDouble() - 0.5d - this.motionX*0.1d, this.world.rand.nextDouble() - 0.75d,
					this.world.rand.nextDouble() - 0.5d - this.motionZ*0.1d);
			this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, ppx, ppy, ppz,
					this.world.rand.nextDouble() - 0.5d - this.motionX*0.1d, this.world.rand.nextDouble() - 0.75d  - this.motionY*0.1d,
					this.world.rand.nextDouble() - 0.5d - this.motionZ*0.1d, 1);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null) {
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
		}
		world.playSound(null, result.hitVec.x, result.hitVec.y, result.hitVec.z, IblisSounds.boulder_impact,
				SoundCategory.PLAYERS, 1.0f, 1.0f);
		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
        if(this.thrower!=null)
        	compound.setString("ownerName", this.thrower.getName());
		compound.setFloat("damage", damage);
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		damage = compound.getFloat("damage");
	}
	
	@Override
	public EntityLivingBase getThrower() {
		EntityLivingBase thrower = super.getThrower();
		if (thrower != null)
			return thrower;
		return this.thrower;
	}
}
