package iblis.chemistry;

public class Substance {

	public final int id;
	public final String unlocalizedName;
	
	private int boilingPoint = 100;
	private int meltingPoint = 0;
	private int molecularMass = 18;
	private float solidStateDencity = 1000.0f; // kg/m3
	private float evaporationEntalpy = 20.0f; // K/mol
	private float meltingEntalpy = 20.0f; // K/mol

	public Substance(int idIn, String unlocalizedNameIn) {
		id = idIn;
		unlocalizedName = unlocalizedNameIn;
	}

	public Substance setMeltingPoint(int meltingPointIn) {
		meltingPoint = meltingPointIn;
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

	public Substance setSolidStateDencity(float solidStateDencityIn) {
		solidStateDencity = solidStateDencityIn;
		return this;
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
}
