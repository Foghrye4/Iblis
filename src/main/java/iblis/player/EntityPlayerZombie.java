package iblis.player;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class EntityPlayerZombie extends EntityZombie {

	public final NonNullList<ItemStack> inventoryInherited = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);

	public EntityPlayerZombie(World worldIn) {
		super(worldIn);
	}

	public EntityPlayerZombie(EntityPlayer playerIn, boolean noDeathPenalty) {
		super(playerIn.world);
		this.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
		this.experienceValue = playerIn.experienceTotal;
		if (!noDeathPenalty) {
			for (PlayerCharacteristics characteristic : PlayerCharacteristics.values()) {
				int characteristicLevel = characteristic.getCurrentLevel(playerIn);
				while (characteristicLevel-- > 0) {
					playerIn.experienceLevel = characteristicLevel;
					while (playerIn.experienceLevel-- > 0) {
						this.experienceValue += playerIn.xpBarCap();
					}
				}
			}
		}
		InventoryPlayer inv = playerIn.inventory;
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inv.getCurrentItem());
		this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, inv.offHandInventory.get(0));
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, inv.armorInventory.get(0));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, inv.armorInventory.get(1));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, inv.armorInventory.get(2));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, inv.armorInventory.get(3));
		inv.mainInventory.set(inv.currentItem, ItemStack.EMPTY);
		for (int i = 0; i < inventoryInherited.size(); i++) {
			inventoryInherited.set(i, inv.mainInventory.get(i).copy());
		}
		inv.clear();
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		NBTTagList nbtTagListIn = new NBTTagList();
		for (int i = 0; i < this.inventoryInherited.size(); ++i) {
			if (!this.inventoryInherited.get(i).isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.inventoryInherited.get(i).writeToNBT(nbttagcompound);
				nbtTagListIn.appendTag(nbttagcompound);
			}
		}
		compound.setTag("inventoryInherited", nbtTagListIn);
		compound.setInteger("experienceValue", this.experienceValue);
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		NBTTagList nbtTagListIn = compound.getTagList("inventoryInherited", 10);
		for (int i = 0; i < nbtTagListIn.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = new ItemStack(nbttagcompound);

			if (!itemstack.isEmpty()) {
				if (j >= 0 && j < this.inventoryInherited.size()) {
					this.inventoryInherited.set(j, itemstack);
				}
			}
		}
		this.experienceValue = compound.getInteger("experienceValue");
	}

	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		super.dropEquipment(wasRecentlyHit, lootingModifier);
		for (ItemStack itemstack : this.inventoryInherited) {
			if (!itemstack.isEmpty()) {
				this.entityDropItem(itemstack, 0.0F);
			}
		}
	}

	@Override
	public boolean canDespawn() {
		return false;
	}
}
