package iblis.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class IblisMathHelper {

	public static EnumFacing getImpactSide(Vec3d aim) {
		double maxVectorCoordinate = 0d;
		EnumFacing result = EnumFacing.UP;
		for(EnumFacing facing:EnumFacing.VALUES){
			Vec3i fdv = facing.getDirectionVec();
			double dx = fdv.getX()*aim.x;
			double dy = fdv.getY()*aim.y;
			double dz = fdv.getZ()*aim.z;
//			if(dx)
		}
		return null;
	}

}
