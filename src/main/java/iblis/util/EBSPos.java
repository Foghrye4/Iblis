package iblis.util;

import javax.annotation.concurrent.Immutable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3i;

@Immutable
public class EBSPos extends Vec3i {

	MutableBlockPos cachedPos = new MutableBlockPos();
	
	public EBSPos(int xIn, int yIn, int zIn) {
		super(xIn, yIn, zIn);
	}

	public BlockPos getBlockPos(int localX, int localY, int localZ) {
		cachedPos.setPos((getX()<<4)+localX, (getY()<<4)+localY, (getZ()<<4)+localZ);
		return cachedPos;
	}
}
