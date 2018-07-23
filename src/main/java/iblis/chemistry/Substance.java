package iblis.chemistry;

import java.util.HashSet;
import java.util.Set;

public class Substance {

	public final int id;
	public final String unlocalizedName;
	private int color = 0x9ED0FF;
	
	private int boilingPoint = 373;
	private int meltingPoint = 273;
	private int molecularMass = 18;
	private float densityAtMeltingPoint = 1000.0f; // kg/m3
	private float densityAtBoilingPoint = 800.0f; // kg/m3
	private float evaporationEntalpy = 200.0f; // kJ/kg
	private float meltingEntalpy = 200.0f; // kJ/kg
	
	Set<Substance> solvents = new HashSet<Substance>();

	public Substance(int idIn, String unlocalizedNameIn) {
		id = idIn;
		unlocalizedName = unlocalizedNameIn;
	}

	public Substance setMeltingPoint(int meltingPointIn) {
		meltingPoint = meltingPointIn;
		if (meltingPoint > boilingPoint)
			boilingPoint = meltingPoint + 1;
		return this;
	}
	
	public Substance setEvaporationEntalpy(float evaporationEntalpyIn) {
		evaporationEntalpy = evaporationEntalpyIn;
		return this;
	}

	public Substance setMeltingEntalpy(float meltingEntalpyIn) {
		meltingEntalpy = meltingEntalpyIn;
		return this;
	}
	
	public Substance setColor(int colorIn) {
		color = colorIn;
		return this;
	}

	public int getMeltingPoint() {
		return meltingPoint;
	}

	public Substance setBoilingPoint(int boilingPointIn) {
		this.boilingPoint = boilingPointIn;
		return this;
	}
	
	public int getBoilingPoint() {
		return boilingPoint;
	}
	
	public Substance addSolvent(Substance solvent) {
		ChemistryRegistry.addToSolutionGroup(this, solvent);
		return this;
	}
	
	public boolean dissolve(Substance substance) {
		return ChemistryRegistry.getSolutionGroup(this).contains(substance);
	}

	public Substance setMolecularMass(int molecularMassIn) {
		molecularMass = molecularMassIn;
		return this;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public float getEvaporationEntalpy() {
		return this.evaporationEntalpy;
	}

	public float getMeltingEntalpy() {
		return this.meltingEntalpy;
	}

	public float getDensity(float temperature) {
		if (temperature > this.boilingPoint)
			return molecularMass / 22.4f * temperature / 293;
		return densityAtBoilingPoint + (densityAtMeltingPoint - densityAtBoilingPoint)
				* (this.boilingPoint - temperature) / (this.boilingPoint - this.meltingPoint);
	}

	public Substance setDensity(float densityIn) {
		this.densityAtMeltingPoint = densityIn;
		this.densityAtBoilingPoint = densityIn*0.92f;
		return this;
	}
	
	public Substance registerSubstance() {
		ChemistryRegistry.registerSubstance(this, id);
		return this;
	}

	public int getColor() {
		return color;
	}
}
