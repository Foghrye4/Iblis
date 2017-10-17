package iblis.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.server.FMLServerHandler;

public class HeadShotHandler {
	private static final AxisAlignedBB zero = new AxisAlignedBB(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d);
	private static final AxisAlignedBB slimeCore = new AxisAlignedBB(0.4d, 0.4d, 0.4d, 0.6d, 0.6d, 0.6d);
	private static final AxisAlignedBB shulkerCore = new AxisAlignedBB(0.4d, 0.1d, 0.4d, 0.6d, 0.4d, 0.6d);
	private static final AxisAlignedBB humanoidHead = new AxisAlignedBB(0.2d, 0.75d, 0.2d, 0.8d, 1.0d, 0.8d);
	private static final AxisAlignedBB huskHead = new AxisAlignedBB(0.2d, 0.75d, 0.2d, 0.8d, 1.1d, 0.8d);
	private static final AxisAlignedBB spiderHead = new AxisAlignedBB(0.6d, 0.5d, 0.4d, 1.2d, 1.2d, 0.6d);
	private static final AxisAlignedBB chickenHead = new AxisAlignedBB(0.9d, 0.8d, 0.4d, 1.4d, 1.4d, 0.6d);
	private static final AxisAlignedBB cowHead = new AxisAlignedBB(0.9d, 0.7d, 0.2d, 1.4d, 1.2d, 0.8d);
	private static final AxisAlignedBB donkeyHead = new AxisAlignedBB(0.7d, 0.7d, 0.2d, 1.0d, 1.0d, 0.8d);
	private static final AxisAlignedBB guardianEye = new AxisAlignedBB(0.8d, 0.4d, 0.4d, 1.0d, 0.6d, 0.6d);
	private static final AxisAlignedBB ghastEyes = new AxisAlignedBB(0.8d, 0.6d, 0.2d, 1.0d, 0.7d, 0.8d);
	private static final AxisAlignedBB polarBearHead = new AxisAlignedBB(1.0d, 0.6d, 0.2d, 1.5d, 1.0d, 0.8d);

	public static RayTraceResult traceHeadShot(EntityLivingBase entity, Vec3d impactStart, Vec3d impactEnd) {
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
			return null;
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
		return box.calculateIntercept(impactStart, impactEnd);
	}

	private static AxisAlignedBB rotateAroundY(AxisAlignedBB headBox, float yaw) {
		float cos = MathHelper.cos(-yaw * 0.017453292F);
		float sin = MathHelper.sin(-yaw * 0.017453292F);
		double dminX = headBox.minX - 0.5;
		double dmaxX = headBox.maxX - 0.5;
		double dminZ = headBox.minZ - 0.5;
		double dmaxZ = headBox.maxZ - 0.5;
		float dmin = MathHelper.sqrt(dminX*dminX + dminZ*dminZ);
		float dmax = MathHelper.sqrt(dmaxX*dmaxX + dmaxZ*dmaxZ);
		double minX = 0.5d + sin * dmin;
		double minZ = 0.5d + cos * dmin;
		double maxX = 0.5d + sin * dmax;
		double maxZ = 0.5d + cos * dmax;
		return new AxisAlignedBB(minX, headBox.minY, minZ, maxX, headBox.maxY, maxZ);
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
