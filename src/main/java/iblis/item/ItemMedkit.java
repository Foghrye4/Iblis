package iblis.item;

import javax.annotation.Nullable;

import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.player.PlayerSkills;
import iblis.potion.PotionEffectMedkit;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMedkit extends Item {
	
	public ItemMedkit() {
		super();
		this.addPropertyOverride(new ResourceLocation("animation_frame"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == null) {
					return 0.0F;
				} else {
					return entityIn.getActiveItemStack().getItem() != IblisItems.MEDKIT ? 0.0F
							: (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount());
				}
			}
		});

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (playerIn.isPotionActive(MobEffects.REGENERATION))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		if (playerIn.getHealth() >= playerIn.getMaxHealth())
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
		World worldIn = playerIn.world;
		if (count == 62) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.opening_medkit,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		} else if (count == 48) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.full_bottle_shaking,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		} else if (count == 32) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.scissors_clicking,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		} else if (count == 16) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.tearing_bandage,
					SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, IblisSounds.closing_medkit,
				SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		if (entityLiving instanceof EntityPlayer) {
			PotionEffect medkitEffect = this.getPotionEffect(stack, (EntityPlayer) entityLiving);
			entityLiving.addPotionEffect(medkitEffect);
		}
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
			EnumHand hand) {
		if (!(target instanceof IMob)) {
			target.addPotionEffect(this.getPotionEffect(stack, playerIn));
			return true;
		} else {
			return false;
		}
	}

	private PotionEffect getPotionEffect(ItemStack stack, EntityPlayer playerIn) {
		double medaidSkillValue = PlayerSkills.MEDICAL_AID.getFullSkillValue(playerIn);
		stack.damageItem(1, playerIn);
		playerIn.addExhaustion(1f);
		PlayerSkills.MEDICAL_AID.raiseSkill(playerIn, 1d);
		PotionEffectMedkit medkitEffect = new PotionEffectMedkit(MobEffects.REGENERATION, 18000, 5, medaidSkillValue);
		return medkitEffect;
	}
}
