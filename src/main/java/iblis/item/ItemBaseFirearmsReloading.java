package iblis.item;

import iblis.constants.NBTTagsKeys;
import iblis.init.IblisSounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public abstract class ItemBaseFirearmsReloading extends Item implements ICustomLeftClickItem {
	
	protected final Item gunBase;

	public ItemBaseFirearmsReloading(Item gunBaseIn) {
		super();
		this.gunBase = gunBaseIn;
	}
	
	protected ItemStack findAmmo(EntityPlayer player) {
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
	
	protected void reloadAmmo(World worldIn, ItemStack ammo, EntityPlayer player, NBTTagCompound nbt, int ammoIn) {
		NBTTagList ammoList = nbt.getTagList(NBTTagsKeys.AMMO, 10);
		NBTTagCompound ammoCartridge = this.ammoStackToCartridgeNBT(ammo);
		ammoList.appendTag(ammoCartridge);
		nbt.setTag(NBTTagsKeys.AMMO, ammoList);
		if(!player.isCreative())
			ammo.shrink(1);
		if (ammo.isEmpty()) {
			player.inventory.deleteStack(ammo);
		}
	}

	protected abstract NBTTagCompound ammoStackToCartridgeNBT(ItemStack ammo);
	
	protected abstract boolean isShot(ItemStack heldItem);

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
}
