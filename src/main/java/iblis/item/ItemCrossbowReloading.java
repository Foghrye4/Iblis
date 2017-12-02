package iblis.item;

import iblis.constants.NBTTagsKeys;
import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		if(ammoAmount>=2) {
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
		if (!(entityIn instanceof EntityPlayer) || worldIn.isRemote)
			return stack;
		EntityPlayer playerIn = (EntityPlayer) entityIn;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}
		ItemStack ammo = this.findAmmo(playerIn);
		this.reloadAmmo(worldIn, ammo, playerIn, nbt, stack.getTagCompound().getInteger(NBTTagsKeys.AMMO));
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
		return 130;
    }

	private ItemStack findAmmo(EntityPlayer player) {
		if (this.isShot(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (this.isShot(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (this.isShot(itemstack)) {
					return itemstack;
				}
			}

			return ItemStack.EMPTY;
		}
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
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase playerIn, int count) {
		World worldIn = playerIn.world;
		int maxDuration = this.getMaxItemUseDuration(stack);
		if (count == maxDuration - 14) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.crossbow_cock,
					SoundCategory.PLAYERS, 0.8f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		} else if (count == 16) {
			worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.crossbow_putting_bolt,
					SoundCategory.PLAYERS, 0.4f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		}
	}
}
