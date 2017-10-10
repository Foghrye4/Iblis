package iblis.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionEffectMedkit extends PotionEffect {

	private final int applyFrequency;

	public PotionEffectMedkit(Potion potionIn, int durationIn, int amplifierIn, double medicalAidSkillIn) {
		super(potionIn, durationIn + (int) (20 * medicalAidSkillIn), amplifierIn);
		applyFrequency = 1 + (int) (256 / ++medicalAidSkillIn);
	}

	@Override
	public boolean onUpdate(EntityLivingBase entityIn) {
		if (entityIn.getHealth() >= entityIn.getMaxHealth())
			return false;
		return super.onUpdate(entityIn);
	}
	
    @Override
	public void performEffect(EntityLivingBase entityIn)
    {
		if (this.getDuration() % applyFrequency == 0)
			super.performEffect(entityIn);
    }

}
