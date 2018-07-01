package iblis.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;


public class BloodHandler {
	private static final int RED = 0xC82100;
	private static final int CYAN = 0x00E4FF;
	private static final int ORANGE = 0xFFA200;
	private static final int ROTTEN_RED = 0x380B0B;
	private static final int IHOR = 0xDEDDB7;
	private static final int PURPLE = 0xFF00F6;
	private static final int GREEN = 0x037200;
	
	public static int getBloodColour(EntityLivingBase victim){
		if (victim instanceof EntityGhast|| victim instanceof EntityBlaze|| victim instanceof EntityMagmaCube)
			return ORANGE;
		else if (victim instanceof EntitySlime)
			return GREEN;
		else if (victim instanceof EntitySilverfish || victim instanceof EntitySpider)
			return IHOR;
		else if (victim instanceof EntityEnderman ||victim instanceof EntityEndermite || victim instanceof EntityShulker)
			return PURPLE;
		else if (victim instanceof EntitySquid)
			return CYAN;
		else if (victim instanceof EntitySquid)
			return ORANGE;
		else if (victim instanceof EntityPlayer 
				|| victim instanceof EntityVex 
				|| victim instanceof EntityDragon
				|| victim instanceof EntityVillager
				|| victim instanceof AbstractIllager
				|| victim instanceof EntityWolf
				|| victim instanceof EntitySheep
				|| victim instanceof EntityRabbit
				|| victim instanceof EntityParrot
				|| victim instanceof EntityOcelot
				|| victim instanceof EntityMule
				|| victim instanceof EntityLlama
				|| victim instanceof EntityDonkey
				|| victim instanceof EntityCow
				|| victim instanceof AbstractHorse
				|| victim instanceof EntityChicken
				|| victim instanceof EntityBat
				|| victim instanceof EntityMule
				|| victim instanceof EntityGuardian
				|| victim instanceof EntityPolarBear
				|| victim instanceof EntityPig
				|| victim instanceof EntityWitch)
			return RED;
		else if (victim instanceof EntityZombie)
			return ROTTEN_RED;
		return -1;
	}

}
