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

public class ItemCrossbowReloading extends Item implements ICustomLeftClickItem {

	private final Item gunBase;
	public final static int ARMING_ONE_BOLT_TIME = 60;

	public ItemCrossbowReloading(Item gunBaseIn) {
		super();
		this.gunBase = gunBaseIn;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		int ammoAmount = nbt.getInteger(NBTTagsKeys.AMMO);
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
			ammo = stack.getTagCompound().getInteger(NBTTagsKeys.AMMO);
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

	private void reloadAmmo(World worldIn, ItemStack ammo, EntityPlayer player, NBTTagCompound nbt, int ammoIn) {
		nbt.setInteger(NBTTagsKeys.AMMO, ++ammoIn);
		if (!player.isCreative())
			ammo.shrink(1);
		if (ammo.isEmpty()) {
			player.inventory.deleteStack(ammo);
		}
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

	private ItemStack findAmmo(EntityPlayer player) {
		if (this.isShot(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (this.isShot(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		}
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			if (this.isShot(itemstack))
				return itemstack;
		}
		return ItemStack.EMPTY;
	}

	protected boolean isShot(ItemStack stack) {
		return stack.getItem() == IblisItems.CROSSBOW_BOLT;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return gunBase.getIsRepairable(toRepair, repair);
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
