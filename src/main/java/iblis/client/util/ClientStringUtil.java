package iblis.client.util;

public class ClientStringUtil {
	
	public static float formatFloat(float value, int decimals){
		value*=decimals;
		return ((int)value)/decimals;
	}

}
