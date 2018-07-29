package iblis.item;

import iblis.constants.NBTTagsKeys;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCrossbowReloading extends ItemBaseFirearmsReloading {

	public final static int ARMING_ONE_BOLT_TIME = 60;

	public ItemCrossbowReloading(Item gunBaseIn) {
		super(gunBaseIn);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		int ammoAmount = nbt.getTagList(NBTTagsKeys.AMMO, 10).tagCount();
		if (ammoAmount >= 2) {
			ItemStack loadedGunStack = new ItemStack(gunBase, 1, stack.getItemDamage());
			loadedGunStack.setTagCompound(nbt);
			playerIn.resetCooldown();
			return new ActionResult<ItemStack>(EnumActionResult.PASS, loadedGunStack);
		}
		if (this.findAmmo(playerIn) == null)
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
		if (!(entityIn instanceof EntityPlayer))
			return stack;
		EntityPlayer playerIn = (EntityPlayer) entityIn;
		playerIn.resetActiveHand();
		playerIn.setActiveHand(EnumHand.MAIN_HAND);
		if(worldIn.isRemote)
			return stack;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		int cockedBowstring = 0;
		int ammo = 0;
		if (stack.hasTagCompound()) {
			cockedBowstring = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
			ammo = stack.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10).tagCount();
		}
		if (ammo >= 2) {
			ItemStack loadedGunStack = new ItemStack(gunBase, 1, stack.getItemDamage());
			loadedGunStack.setTagCompound(nbt);
			return loadedGunStack;
		}
		if (ammo >= cockedBowstring) {
			cockedBowstring++;
			stack.getTagCompound().setInteger(NBTTagsKeys.COCKED_STATE, cockedBowstring);
			return stack;
		} else {
			ItemStack ammoStack = this.findAmmo((EntityPlayer) playerIn);
			this.reloadAmmo(worldIn, ammoStack, (EntityPlayer) playerIn, stack.getTagCompound(), ammo);
		}
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		int cockedBowstring = 0;
		int ammo = 0;
		if (stack.hasTagCompound()) {
			cockedBowstring = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
			ammo = stack.getTagCompound().getInteger(NBTTagsKeys.AMMO);
		}
		if (ammo >= cockedBowstring)
			return 15;
		else
			return ARMING_ONE_BOLT_TIME - 15;
	}

	@Override
	protected boolean isShot(ItemStack stack) {
		return stack.getItem() == IblisItems.CROSSBOW_BOLT;
	}

	@Override
	public void onLeftClick(World world, EntityPlayerMP playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		ItemStack loadedGunStack = new ItemStack(gunBase, 1, stack.getItemDamage());
		loadedGunStack.setTagCompound(stack.getTagCompound());
		playerIn.resetCooldown();
		playerIn.setHeldItem(handIn, loadedGunStack);
	}

	// 'count' value is in decreasing order. Here only sound.
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
		if (!(playerIn instanceof EntityPlayer))
			return;
		if (count != 4)
			return;
		World worldIn = playerIn.world;
		int cockedBowstring = 0;
		int ammo = 0;
		if (stack.hasTagCompound()) {
			cockedBowstring = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
			ammo = stack.getTagCompound().getInteger(NBTTagsKeys.AMMO);
		}
		if (ammo >= cockedBowstring) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.crossbow_cock,
					SoundCategory.PLAYERS, 0.8f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		} else {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.crossbow_putting_bolt,
					SoundCategory.PLAYERS, 0.4f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		}
	}
}
