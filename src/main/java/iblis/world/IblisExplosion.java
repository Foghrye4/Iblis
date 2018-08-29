package iblis.world;

import iblis.util.EBSPos;
import iblis.util.EnumCoordinateOctet;
import iblis.util.MutableEBSPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class IblisExplosion extends Explosion {

	public static final int BITS_PER_AXIS = 10;
	public static final int AXIS_MASK = (1 << BITS_PER_AXIS) - 1;
	public static final long[] vectors = new long[1 << BITS_PER_AXIS * 3];

	public static void bake() {
		for (int r = 0; r < AXIS_MASK; r++) {
			for (int x = 0; x <= r; x++) {
				for (int y = 0; y <= r; y++) {
					for (int z = (x == r || y == r) ? 0 : r; z <= r; z++) {
						if(!findAndWriteToAncestor1(x,y,z,r)) {
							findAndWriteToAncestor2(x,y,z);
						}
					}
				}
			}
		}
	}

	private static boolean findAndWriteToAncestor1(int x, int y, int z, int r) {
		int newEncoded = encodeVector(x, y, z);
		if (x > r / 2 && y > r / 2 && z > r / 2) {
			int encoded = encodeVector(x - 1, y - 1, z - 1);
			if (write(encoded, newEncoded))
				return true;
		} else if (x > r / 2 && y > r / 2) {
			int encoded = encodeVector(x - 1, y - 1, z);
			if (write(encoded, newEncoded))
				return true;
		} else if (x > r / 2 && z > r / 2) {
			int encoded = encodeVector(x - 1, y, z - 1);
			if (write(encoded, newEncoded))
				return true;
		} else if (y > r / 2 && z > r / 2) {
			int encoded = encodeVector(x, y - 1, z - 1);
			if (write(encoded, newEncoded))
				return true;
		} else if (x > 0 && x >= y && x >= z) {
			int encoded = encodeVector(x - 1, y, z);
			if (write(encoded, newEncoded))
				return true;
		} else if (y > 0 && y >= x && y >= z) {
			int encoded = encodeVector(x, y - 1, z);
			if (write(encoded, newEncoded))
				return true;
		} else if (z > 0 && z >= x && z >= y) {
			int encoded = encodeVector(x, y, z - 1);
			if (write(encoded, newEncoded))
				return true;
		}
		return false;
	}

	private static boolean write(int encoded, int newEncoded) {
		int[] decoded = decodeVector(vectors[encoded]);
		if (decoded[0] + decoded[1] + decoded[2] == 0) {
			vectors[encoded] = newEncoded;
			return true;
		} else if (decoded[3] + decoded[4] + decoded[5] == 0) {
			vectors[encoded] |= newEncoded << BITS_PER_AXIS * 3;
			return true;
		}
		return false;
	}

	private static void findAndWriteToAncestor2(int x, int y, int z) {
		int newEncoded = encodeVector(x, y, z);
		for (int xi = x; xi >= x - 1; xi--) {
			for (int yi = y; yi >= y - 1; yi--) {
				for (int zi = z; zi >= z - 1; zi--) {
					if (xi == x && yi == y && zi == z)
						continue;
					int encoded = encodeVector(xi, yi, zi);
					if(write(encoded,newEncoded))
						return;
				}
			}
		}
		for (int xi = x; xi <= x + 1; xi++) {
			for (int yi = y; yi <= y + 1; yi++) {
				for (int zi = z; zi <= z + 1; zi++) {
					if (xi == x && yi == y && zi == z)
						continue;
					int encoded = encodeVector(xi, yi, zi);
					if(write(encoded,newEncoded))
						return;
				}
			}
		}
		throw new IllegalStateException();
	}

	private static int[] decodeVector(long vector) {
		int[] output = new int[6];
		for (int i = 0; i < output.length; i++) {
			output[i] = (int) (vector & AXIS_MASK);
			vector >>>= BITS_PER_AXIS;
		}
		return output;
	}

	private static int encodeVector(int x, int y, int z) {
		int output = 0;
		output |= x;
		output |= y << BITS_PER_AXIS;
		output |= z << BITS_PER_AXIS * 2;
		return output;
	}
	
	private static int encodeVector(int x, int z) {
		int output = 0;
		output |= x;
		output |= z << BITS_PER_AXIS;
		return output;
	}
	
	private World world;
	private Chunk[] chunkCache = new Chunk[1 << BITS_PER_AXIS * 2];
	private Entity exploder;
	
	public IblisExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming,
			boolean damagesTerrain) {
		super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
		exploder = entityIn;
	}

	private Chunk getChunk(EnumCoordinateOctet octet, EBSPos origin, MutableEBSPos currentPos) {
		int chunkKey = encodeVector(octet.getVectorX(currentPos.getX() - origin.getX()),octet.getVectorZ(currentPos.getZ() - origin.getZ()));
		if(chunkKey>=chunkCache.length)
			return null;
		return chunkCache[chunkKey];
	}
	
	private boolean canDestroyEBS(EnumCoordinateOctet octet, EBSPos origin, MutableEBSPos currentPos, int power) {
		Chunk chunk = this.getChunk(octet, origin, currentPos);
		if(chunk==null)
			return false;
		ExtendedBlockStorage ebs = chunk.getBlockStorageArray()[currentPos.getY()];
		if(ebs.isEmpty())
			return true;
		int resistance = this.getExplosionResistance(ebs, currentPos);
		return resistance<power;
	}
	
	private int getExplosionResistance(ExtendedBlockStorage ebs, MutableEBSPos currentPos) {
		int resistance = 0;
		for(int x=0;x<16;x++)
			for(int y=0;y<16;y++)
				for(int z=0;z<16;z++)
					resistance+=ebs.get(x, y, z).getBlock().getExplosionResistance(world, currentPos.getBlockPos(x,y,z), exploder, this);
		return resistance;
	}
	
	private void startExplosion(EnumCoordinateOctet octet, EBSPos origin) {
		for(int x=0;x<AXIS_MASK;x++) {
			for(int z=0;z<AXIS_MASK;z++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(octet.getX(x)+origin.getX(), octet.getZ(z)+origin.getZ());
				if(chunk==null)
					break;
				chunkCache[encodeVector(x,z)] = chunk;
			}
		}
	}
	
	public void doExplosion(EnumCoordinateOctet octet, BlockPos origin, MutableBlockPos currentPos) {
		
	}
	
	public void doExplosion(EnumCoordinateOctet octet, EBSPos origin, MutableEBSPos currentPos) {
		
	}
}
