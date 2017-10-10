package iblis.item;

import javax.annotation.Nullable;

import iblis.init.IblisItems;
import iblis.init.IblisSounds;
import iblis.player.PlayerSkills;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShotgunReloading extends Item {

	public static final int MAX_AMMO = 6;
	private final Item gunBase;

	public ItemShotgunReloading(Item gunBaseIn) {
		super();
		this.gunBase = gunBaseIn;
		this.addPropertyOverride(new ResourceLocation("ammo"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (!stack.hasTagCompound())
					return 0;
				return stack.getTagCompound().getInteger("ammo");
			}
		});
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		ItemStack loadedGunStack = new ItemStack(gunBase, 1, itemstack.getItemDamage());
		if (itemstack.getTagCompound() == null)
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, loadedGunStack);
		NBTTagCompound nbt = itemstack.getTagCompound();
		loadedGunStack.setTagCompound(nbt);
		playerIn.resetCooldown();
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, IblisSounds.shotgun_hammer_cock,
				SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
		return new ActionResult<ItemStack>(EnumActionResult.PASS, loadedGunStack);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!(entityIn instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) entityIn;
		if (stack == player.getHeldItem(EnumHand.MAIN_HAND)) {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null)
				return;
			int ammoIn = nbt.getInteger("ammo");
			if (ammoIn < MAX_AMMO) {
				player.resetCooldown();
				if (!worldIn.isRemote) {
					ItemStack ammo = this.findAmmo(player);
					if (!ammo.isEmpty()) {
						double sharpshootingSkillValue = PlayerSkills.SHARPSHOOTING.getFullSkillValue(player);
						int reload_tick = player.getEntityData().getInteger("reload_tick");
						if (reload_tick++ >= 16 / (sharpshootingSkillValue + 1) + 2) {
							this.reloadAmmo(worldIn, ammo, player, nbt, ammoIn);
							reload_tick = 0;
						}
						player.getEntityData().setInteger("reload_tick", reload_tick);
					}
				}
			} else {
				if (player.getCooledAttackStrength(0.0F) < 1f)
					return;
				if (!worldIn.isRemote) {
					ItemStack loadedGunStack = new ItemStack(gunBase, 1, stack.getItemDamage());
					loadedGunStack.setTagCompound(nbt);
					player.setHeldItem(EnumHand.MAIN_HAND, loadedGunStack);
				}
				player.resetCooldown();
				worldIn.playSound(null, player.posX, player.posY, player.posZ, IblisSounds.shotgun_hammer_cock,
						SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
			}
		}
	}

	private void reloadAmmo(World worldIn, ItemStack ammo, EntityPlayer player, NBTTagCompound nbt, int ammoIn) {
		worldIn.playSound(null, player.posX, player.posY, player.posZ, IblisSounds.shotgun_ammo_loading,
				SoundCategory.PLAYERS, 1.0f, 1.0f);
		nbt.setInteger("ammo", ++ammoIn);
		if(!player.isCreative())
			ammo.shrink(1);
		if (ammo.isEmpty()) {
			player.inventory.deleteStack(ammo);
		}
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
		return stack.getItem() == IblisItems.SHOTGUN_BULLET;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		int[] oreIds = OreDictionary.getOreIDs(repair);
		for (int oreId : oreIds) {
			if (OreDictionary.getOreName(oreId).equals("ingotSteel"))
				return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}
}
