package iblis_headshots.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class HeadShotHandler {
	private static final AxisAlignedBB zero = new AxisAlignedBB(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d);
	private static final AxisAlignedBB slimeCore = new AxisAlignedBB(0.4d, 0.4d, 0.4d, 0.6d, 0.6d, 0.6d);
	private static final AxisAlignedBB shulkerCore = new AxisAlignedBB(0.4d, 0.1d, 0.4d, 0.6d, 0.4d, 0.6d);
	private static final AxisAlignedBB humanoidHead = new AxisAlignedBB(0.1d, 0.8d, 0.1d, 0.9d, 1.0d, 0.9d);
	private static final AxisAlignedBB huskHead = new AxisAlignedBB(0.1d, 0.8d, 0.1d, 0.9d, 1.1d, 0.9d);
	private static final AxisAlignedBB spiderHead = new AxisAlignedBB(0.6d, 0.5d, 0.3d, 1.2d, 1.2d, 0.7d);
	private static final AxisAlignedBB chickenHead = new AxisAlignedBB(0.9d, 0.8d, 0.3d, 1.4d, 1.4d, 0.7d);
	private static final AxisAlignedBB cowHead = new AxisAlignedBB(0.9d, 0.7d, 0.2d, 1.4d, 1.2d, 0.8d);
	private static final AxisAlignedBB donkeyHead = new AxisAlignedBB(0.7d, 0.7d, 0.2d, 1.0d, 1.0d, 0.8d);
	private static final AxisAlignedBB guardianEye = new AxisAlignedBB(0.8d, 0.4d, 0.4d, 1.0d, 0.6d, 0.6d);
	private static final AxisAlignedBB ghastEyes = new AxisAlignedBB(0.8d, 0.6d, 0.2d, 1.0d, 0.7d, 0.8d);
	private static final AxisAlignedBB polarBearHead = new AxisAlignedBB(1.0d, 0.6d, 0.2d, 1.5d, 1.0d, 0.8d);

	public static RayTraceResult traceHeadShot(EntityLivingBase entity, Vec3d impactStart, Vec3d impactEnd) {
		return getHeadBox(entity).calculateIntercept(impactStart, impactEnd);
	}
	
	public static AxisAlignedBB getHeadBox(EntityLivingBase entity) {
		AxisAlignedBB box = zero;
		AxisAlignedBB collisionBoundingBox = entity.getEntityBoundingBox();
		if (entity instanceof EntitySlime)
			box = shrinkBoxTo(collisionBoundingBox, slimeCore);
		else if (entity instanceof EntityShulker)
			box = shrinkBoxTo(collisionBoundingBox, shulkerCore);
		// Endermite has armored head. Cute animals should not die. Squid is all head.
		else if(entity instanceof EntityBat || 
				entity instanceof EntityEndermite || 
				entity instanceof EntityOcelot || 
				entity instanceof EntityParrot || 
				entity instanceof EntitySilverfish || 
				entity instanceof EntitySquid)
			return zero;
		else if(entity instanceof EntitySpider){
			AxisAlignedBB headBoxRotated = rotateAroundY(spiderHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityChicken || entity instanceof EntityRabbit){
			AxisAlignedBB headBoxRotated = rotateAroundY(chickenHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityCow ||
				entity instanceof EntityPig ||
				entity instanceof EntitySheep){
			AxisAlignedBB headBoxRotated = rotateAroundY(cowHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityDonkey || 
				entity instanceof EntityMule || 
				entity instanceof EntityLlama || 
				entity instanceof AbstractHorse){
			AxisAlignedBB headBoxRotated = rotateAroundY(donkeyHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityGuardian){
			AxisAlignedBB headBoxRotated = rotateAroundY(guardianEye, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityGhast){
			AxisAlignedBB headBoxRotated = rotateAroundY(ghastEyes, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityPolarBear){
			AxisAlignedBB headBoxRotated = rotateAroundY(polarBearHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
			if (((EntityPolarBear) entity).swingProgress > 0.2f)
				box = box.offset(0d, ((EntityPolarBear) entity).swingProgress, 0d);
		}
		else if(entity instanceof EntityWolf){
			AxisAlignedBB headBoxRotated = rotateAroundY(polarBearHead, entity.renderYawOffset);
			box = shrinkBoxTo(collisionBoundingBox, headBoxRotated);
		}
		else if(entity instanceof EntityHusk){
			box = shrinkBoxTo(collisionBoundingBox, huskHead);
		}
		else/* if(entity instanceof AbstractSkeleton || 
				entity instanceof AbstractIllager ||
				entity instanceof EntityZombie ||
				entity instanceof EntityCreeper ||
				entity instanceof EntityBlaze ||
				entity instanceof EntityVex ||
				entity instanceof EntityWitch
				)*/
			box = shrinkBoxTo(collisionBoundingBox, humanoidHead);
		return box;
	}

	private static AxisAlignedBB rotateAroundY(AxisAlignedBB headBox, float yaw) {
		float cos = MathHelper.cos(-yaw * 0.017453292F);
		float sin = MathHelper.sin(-yaw * 0.017453292F);
		float dminX = (float) (headBox.minX - 0.5);
		float dmaxX = (float) (headBox.maxX - 0.5);
		float dminZ = (float) (headBox.minZ - 0.5);
		float dmaxZ = (float) (headBox.maxZ - 0.5);
		float x00 = 0.5f + sin * dminX + cos * dminZ;
		float z00 = 0.5f + cos * dminX + sin * dminZ;
		float x11 = 0.5f + sin * dmaxX + cos * dmaxZ;
		float z11 = 0.5f + cos * dmaxX + sin * dmaxZ;
		float x10 = 0.5f + sin * dmaxX + cos * dminZ;
		float z10 = 0.5f + cos * dmaxX + sin * dminZ;
		float x01 = 0.5f + sin * dminX + cos * dmaxZ;
		float z01 = 0.5f + cos * dminX + sin * dmaxZ;
		return new AxisAlignedBB(
				min(x00, x10, x01, x11), 
				headBox.minY, 
				min(z00, z10, z01, z11),
				max(x00, x10, x01, x11), 
				headBox.maxY, 
				max(z00, z10, z01, z11));
	}
	
	private static float min(float v1,float v2,float v3,float v4) {
		float v = v1;
		v = v2<v?v2:v;
		v = v3<v?v3:v;
		v = v4<v?v4:v;
		return v;
	}
	
	private static float max(float v1,float v2,float v3,float v4) {
		float v = v1;
		v = v2>v?v2:v;
		v = v3>v?v3:v;
		v = v4>v?v4:v;
		return v;
	}

	private static AxisAlignedBB shrinkBoxTo(AxisAlignedBB original, AxisAlignedBB shrinkTo) {
		double sizeX = original.maxX - original.minX;
		double sizeY = original.maxY - original.minY;
		double sizeZ = original.maxZ - original.minZ;
		return new AxisAlignedBB(
				original.minX + shrinkTo.minX * sizeX, 
				original.minY + shrinkTo.minY * sizeY,
				original.minZ + shrinkTo.minZ * sizeZ, 
				original.minX + shrinkTo.maxX * sizeX,
				original.minY + shrinkTo.maxY * sizeY, 
				original.minZ + shrinkTo.maxZ * sizeZ);
	}

}
