package iblis.init;

import java.util.Arrays;

import iblis.chemistry.ChemicalReaction;
import iblis.chemistry.ChemistryRegistry;
import iblis.chemistry.ReactionIngridient;
import iblis.chemistry.Substance;
import iblis.chemistry.SubstanceStack;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;

public class IblisSubstances {
	
	public static Substance IMPURITY = new Substance(0,"iblis.impurity").setMeltingPoint(500).registerSubstance();
	public static Substance WATER = new Substance(1,"iblis.water").setEvaporationEntalpy(2260).setMeltingEntalpy(330).registerSubstance();
	public static Substance MERCURY = new Substance(2,"iblis.mercury").setMeltingPoint(234).setBoilingPoint(630).setDensity(13546.0f).setColor(0xFFFFFF).registerSubstance();
	public static Substance NITRIC_ACID = new Substance(3,"iblis.nitric_acid").setMeltingPoint(232).setBoilingPoint(356).setColor(0xFFF0B3).registerSubstance();
	public static Substance MERCURY2_NITRATE = new Substance(4,"iblis.mercury2_nitrate").setMeltingPoint(500).setColor(0xFFFFFF).registerSubstance();
	public static Substance MERCURY2_FULMINATE = new Substance(5,"iblis.mercury2_fulminate").setMeltingPoint(500).setColor(0xFFFFFF).registerSubstance();
	public static Substance ETHANOLE = new Substance(6,"iblis.ethanole").setMeltingPoint(250).setBoilingPoint(340).setColor(0xC7FDFF).registerSubstance();
	public static Substance YEAST = new Substance(7,"iblis.yeast").setMeltingPoint(500).setColor(0xC1A081).registerSubstance();
	public static Substance CHITIN = new Substance(8,"iblis.chitin").setMeltingPoint(500).setColor(0xA99390).registerSubstance();
	public static Substance SUGAR = new Substance(9,"iblis.sugar").setMeltingPoint(500).registerSubstance();
	public static Substance SUGAR_SYRUP = new Substance(10,"iblis.sugar_syrup").registerSubstance();
	public static Substance CARBON_DIOXIDE = new Substance(11,"iblis.carbon_dioxide").setMeltingPoint(20).setBoilingPoint(100).registerSubstance();
	public static Substance NITRIC_OXIDE4 = new Substance(12,"iblis.nitric_oxide4").setMeltingPoint(20).setBoilingPoint(100).registerSubstance();
	public static Substance SULPHURIC_ACID = new Substance(13,"iblis.sulphuric_acid").setMeltingPoint(283).setColor(0x4d3c3a).setBoilingPoint(610).registerSubstance();
	public static Substance SALTPETER = new Substance(14,"iblis.saltpeter").setMeltingPoint(500).setColor(0xdec8b3).registerSubstance();
	public static Substance SULPHUR = new Substance(15,"iblis.sulphur").setMeltingPoint(386).setColor(0xcd9f00).setBoilingPoint(718).registerSubstance();
	public static Substance SULPHURIC_ANHYDRIDE = new Substance(16,"iblis.sulphuric_anhydride").setMeltingPoint(290).setBoilingPoint(318).registerSubstance();
	public static Substance POTASSIUM_SULPHATE = new Substance(17,"iblis.potassium_sulphate").setMeltingPoint(1342).setBoilingPoint(1962).registerSubstance();
	public static Substance SODIUM_SULPHATE = new Substance(18,"iblis.sodium_sulphate").setMeltingPoint(1342).setBoilingPoint(1962).registerSubstance();
	public static Substance ACETALDEHYDE = new Substance(19,"iblis.acetaldehyde").setMeltingPoint(150).setBoilingPoint(293).registerSubstance();
	public static void init(){
		ETHANOLE.addSolvent(WATER);
		NITRIC_ACID.addSolvent(WATER);
		SUGAR_SYRUP.addSolvent(WATER);
		
		ChemistryRegistry.registerFluidToSubstanceConversion(FluidRegistry.WATER, WATER);
		ChemistryRegistry.registerPotionToSubstanceConversion(PotionTypes.WATER, WATER);
		ChemistryRegistry.registerItemToSubstanceStackConversion(IblisItems.RAISIN, Arrays.asList(new SubstanceStack(SUGAR).setSolidAmount(80.0f),new SubstanceStack(YEAST).setSolidAmount(20.0f),new SubstanceStack(IMPURITY).setSolidAmount(20.0f)));
		ChemistryRegistry.registerItemToSubstanceStackConversion(Items.SNOWBALL, Arrays.asList(new SubstanceStack(WATER).setSolidAmount(16.0f)));
		ChemistryRegistry.registerItemToSubstanceStackConversion(Items.SUGAR, Arrays.asList(new SubstanceStack(SUGAR).setSolidAmount(144.0f)));
		ChemistryRegistry.registerOreDictionaryToSubstanceStackConversion("dustSulphur", Arrays.asList(new SubstanceStack(SULPHUR).setSolidAmount(144.0f)));
		ChemistryRegistry.registerOreDictionaryToSubstanceStackConversion("dustSaltpeter", Arrays.asList(new SubstanceStack(SALTPETER).setSolidAmount(144.0f)));
		
		new ChemicalReaction(new ReactionIngridient(WATER,1.0f),new ReactionIngridient(SUGAR,1.0f))
			.setResult(new ReactionIngridient(SUGAR_SYRUP,2.0f)).setEntalpy(0.0f).register();
		new ChemicalReaction(new ReactionIngridient(YEAST,0.1f), new ReactionIngridient(SUGAR_SYRUP,0.1f))
			.setResult(new ReactionIngridient(YEAST,0.15f), new ReactionIngridient(ETHANOLE,0.05f)).setTemperatureStart(293).setTKRatio(1).register();
		new ChemicalReaction(new ReactionIngridient(MERCURY,1.0f),new ReactionIngridient(NITRIC_ACID,1.0f))
			.setResult(new ReactionIngridient(MERCURY2_NITRATE,1.0f),new ReactionIngridient(NITRIC_OXIDE4,1.0f)).register();
		new ChemicalReaction(new ReactionIngridient(MERCURY2_NITRATE,1.0f),new ReactionIngridient(ETHANOLE,3.0f))
			.setResult(new ReactionIngridient(MERCURY2_FULMINATE,1.0f),new ReactionIngridient(ACETALDEHYDE,2.0f),new ReactionIngridient(WATER,5.0f)).register();
		new ChemicalReaction(new ReactionIngridient(YEAST,10.0f))
			.setResult(new ReactionIngridient(CHITIN,5.0f), new ReactionIngridient(IMPURITY,5.0f)).setTemperatureStart(313).setTKRatio(1).register();
		new ChemicalReaction(new ReactionIngridient(SALTPETER,0.1f),new ReactionIngridient(SULPHUR,0.1f))
			.setResult(new ReactionIngridient(SULPHURIC_ANHYDRIDE,0.1f)).setTemperatureStart(400).setEntalpy(400).register();
		new ChemicalReaction(new ReactionIngridient(SULPHURIC_ANHYDRIDE,10f),new ReactionIngridient(WATER,10f))
			.setResult(new ReactionIngridient(SULPHURIC_ACID,20f)).setTemperatureStart(250).setEntalpy(40).register();
		new ChemicalReaction(new ReactionIngridient(SULPHURIC_ANHYDRIDE,10f),new ReactionIngridient(WATER,10f))
			.setResult(new ReactionIngridient(SULPHURIC_ACID,20f)).setTemperatureStart(250).setEntalpy(40).register();
		new ChemicalReaction(new ReactionIngridient(SALTPETER,1f),new ReactionIngridient(SULPHURIC_ACID,1f))
			.setResult(new ReactionIngridient(POTASSIUM_SULPHATE,1f),new ReactionIngridient(NITRIC_ACID,1f)).setTemperatureStart(270).setEntalpy(0).setReversive().register();
	}

}
