package iblis.init;

import iblis.chemistry.Substance;

public class IblisSubstances {
	
	public static Substance IMPURITY;
	public static Substance WATER;
	public static Substance MERCURY;
	
	public static void init(){
		IMPURITY = new Substance(0,"iblis.impurity");
		WATER = new Substance(1,"iblis.water");
		MERCURY = new Substance(2,"iblis.mercury");
	}

}
