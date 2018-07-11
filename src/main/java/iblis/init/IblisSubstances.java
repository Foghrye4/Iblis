package iblis.init;

import iblis.chemistry.Substance;

public class IblisSubstances {
	
	public static Substance IMPURITY = new Substance(0,"iblis.impurity").setMeltingPoint(500);
	public static Substance WATER = new Substance(1,"iblis.water");
	public static Substance MERCURY = new Substance(2,"iblis.mercury").setMeltingPoint(234).setBoilingPoint(630).setDensity(13546.0f);
	public static Substance NITRIC_ACID = new Substance(3,"iblis.nitric_acid").setMeltingPoint(232).setBoilingPoint(356);
	public static Substance MERCURY2_NITRATE = new Substance(4,"iblis.mercury2_nitrate").setMeltingPoint(500);
	public static Substance MERCURY2_FULMINATE = new Substance(5,"iblis.mercury2_fulminate").setMeltingPoint(500);
	public static Substance ETHANOLE = new Substance(6,"iblis.ethanole").setMeltingPoint(250).setBoilingPoint(340);
	public static Substance YEAST = new Substance(7,"iblis.yeast").setMeltingPoint(500);
	public static Substance CHITIN = new Substance(8,"iblis.chitin").setMeltingPoint(500);
	public static Substance SUGAR = new Substance(9,"iblis.sugar").setMeltingPoint(500);
	public static Substance SUGAR_SYRUP = new Substance(9,"iblis.sugar_syrup");
	public static Substance CARBON_DIOXIDE = new Substance(9,"iblis.carbon_dioxide").setMeltingPoint(20).setBoilingPoint(100);
	public static Substance NITRIC_OXIDE4 = new Substance(10,"iblis.nitric_oxide4").setMeltingPoint(20).setBoilingPoint(100);
	
	public static void init(){
		ETHANOLE.addSolvent(WATER);
		NITRIC_ACID.addSolvent(WATER);
		SUGAR_SYRUP.addSolvent(WATER);
	}

}
