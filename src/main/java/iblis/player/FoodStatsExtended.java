package iblis.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;

public class FoodStatsExtended extends FoodStats {

	private int maxFood = 20;

	@Override
	public void addStats(int foodLevelIn, float foodSaturationModifier) {
		this.foodLevel = Math.min(foodLevelIn + this.foodLevel, maxFood);
		this.foodSaturationLevel = Math.min(
				this.foodSaturationLevel + (float) foodLevelIn * foodSaturationModifier, (float) this.foodLevel);
	}
	
	@Override
	public void onUpdate(EntityPlayer player) {
		maxFood = MathHelper.floor(PlayerCharacteristics.GLUTTONY.getCurrentValue(player));
		super.onUpdate(player);
	}
	
	@Override
	public boolean needFood() {
		return this.foodLevel < maxFood;
	}

	public int getFoodMaxLevel() {
		return maxFood;
	}
}
