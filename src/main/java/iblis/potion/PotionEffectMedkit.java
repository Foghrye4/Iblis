package iblis.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionEffectMedkit extends PotionEffect {

	private final int applyFrequency;
	public PotionEffectMedkit(Potion potionIn, int durationIn, int amplifierIn, double medicalAidSkillIn) {
		super(potionIn,durationIn, amplifierIn);
		applyFrequency = (int)(4096/++medicalAidSkillIn);
	}

	public boolean onUpdate(EntityLivingBase entityIn) {
		if (this.getDuration() > 0) {
			if (this.getDuration() % applyFrequency == 0)
				return super.onUpdate(entityIn);
			else
				return true;
		}
		return false;
	}
}
