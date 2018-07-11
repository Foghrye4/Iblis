package iblis.chemistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Substance {

	public final int id;
	public final String unlocalizedName;
	
	private int boilingPoint = 373;
	private int meltingPoint = 273;
	private int molecularMass = 18;
	private float densityAtMeltingPoint = 1000.0f; // kg/m3
	private float densityAtBoilingPoint = 800.0f; // kg/m3
	private float evaporationEntalpy = 20.0f; // K/mol
	private float meltingEntalpy = 20.0f; // K/mol
	
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
		for (Substance other : solvents) {
			other.addSolventRaw(solvent);
		}
		solvent.addSolvent(this);
		return this;
	}
	
	private void addSolventRaw(Substance solvent){
		solvents.add(solvent);
	}
	
	public boolean dissolve(Substance substance) {
		return solvents.contains(substance);
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

	public float getDensity(int temperature) {
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
}
