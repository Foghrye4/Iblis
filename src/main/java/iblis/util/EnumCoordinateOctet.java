package iblis.util;

public enum EnumCoordinateOctet {
	PXPYPZ(1,1,1),
	NXPYPZ(-1,1,1),
	PXNYPZ(1,-1,1),
	PXPYNZ(1,1,-1),
	NXNYPZ(-1,-1,1),
	NXPYNZ(-1,1,-1),
	PXNYNZ(1,-1,-1),
	NXNYNZ(-1,-1,-1);
	
	public final int x,y,z,shiftX,shiftY,shiftZ;
	
	EnumCoordinateOctet(int x1, int y1, int z1) {
		x = x1;
		y = y1;
		z = z1;
		shiftX = x<0?-1:0;
		shiftY = y<0?-1:0;
		shiftZ = z<0?-1:0;
	}
	
	public int getX(int xOrigin) {
		return xOrigin * x + shiftX;
	}
	public int getY(int yOrigin) {
		return yOrigin * y + shiftY;
	}
	public int getZ(int zOrigin) {
		return zOrigin * z + shiftZ;
	}
	
	public int getVectorX(int xWorld) {
		return (xWorld - shiftX) * x;
	}
	public int getVectorY(int yWorld) {
		return (yWorld - shiftY) * y;
	}
	public int getVectorZ(int zWorld) {
		return (zWorld - shiftZ) * z;
	}

}
