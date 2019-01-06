package iblis_headshots.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class IblisMathUtil {
	
	public static float[] calculateOverlappingAmount(AxisAlignedBB axisalignedbb, Vec3d start, Vec3d end) {
		float sx = (float) start.x;
		float sy = (float) start.y;
		float sz = (float) start.z;
		float ax1 = (float) axisalignedbb.minX - sx;
		float ax2 = (float) axisalignedbb.maxX - sx;
		float ay1 = (float) axisalignedbb.minY - sy;
		float ay2 = (float) axisalignedbb.maxY - sy;
		float az1 = (float) axisalignedbb.minZ - sz;
		float az2 = (float) axisalignedbb.maxZ - sz;
		float ax = (ax1+ax2)*0.5f;
		float ay = (ay1+ay2)*0.5f;
		float az = (az1+az2)*0.5f;
		float bx = (float) end.x - sx;
		float by = (float) end.y - sy;
		float bz = (float) end.z - sz;
		float da = (float) Math.sqrt(ax*ax+ay*ay+az*az);
		float db = (float) Math.sqrt(bx*bx+by*by+bz*bz);
		ax1/=da;
		ay1/=da;
		az1/=da;
		ax2/=da;
		ay2/=da;
		az2/=da;
		bx/=db;
		by/=db;
		bz/=db;
		float dx = distanceToInterval(bx,ax1,ax2);
		float dy = distanceToInterval(by,ay1,ay2);
		float dz = distanceToInterval(bz,az1,az2);
		float manhattanDistance = Math.max(dx, Math.max(dy, dz));
		return new float[] {manhattanDistance, da};
	}
	
	public static float distanceToInterval(float value, float min, float max){
		if(value>=max)
			return value - max;
		if(value<=min)
			return min - value;
		return -Math.min(value-min,max-value);
	}

}
