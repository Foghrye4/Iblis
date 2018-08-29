package iblis.tileentity;

import javax.annotation.Nullable;

import iblis.chemistry.ChemistryRegistry;
import iblis.chemistry.IReactorOwner;
import iblis.chemistry.Reactor;
import iblis.init.IblisItems;
import iblis.item.ItemSubstanceContainer;
import iblis.player.PlayerSkills;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class TileEntityLabTable extends TileEntity implements IReactorOwner, ITickable {
	public final Reactor hotReactor = new Reactor();
	public final Reactor coldReactor = new Reactor();
	public final Reactor separatorIn = new Reactor();
	public final Reactor separatorOut = new Reactor();
	public final Reactor filterIn = new Reactor();
	public final Reactor filterOut = new Reactor();
	public final Reactor atmosphere = new Reactor();
	private boolean hasFilterOut = true;
	private boolean hasReactorOut = true;
	private boolean hasSeparatorOut = true;
	private boolean hasReactor = true;
	private int fuel = 0;
	private boolean isBurning = true;
	private float reactionYield = 1.0f;
	private final float ambientTemperature = 293.0f;
	
	@Override
	public void update() {
		if(this.getWorld()==null || this.getWorld().isRemote)
			return;
		if(this.hasReactor) {
			hotReactor.tick(this);
			hotReactor.exhaustGasesTo(coldReactor);
		}
		coldReactor.setTemperature(ambientTemperature);
		if(this.hasReactorOut)
			coldReactor.tick(this);
		else
			coldReactor.clear();
		separatorIn.tick(this);
		separatorOut.tick(this);
		filterOut.tick(this);
		if (isBurning()) {
			fuel--;
			hotReactor.addEntalpy(400);
		}
		hotReactor.addEntalpy(ambientTemperature - hotReactor.getTemperature());
		coldReactor.addEntalpy(ambientTemperature - coldReactor.getTemperature());
		this.sendUpdatePacket();
	}
	
	public boolean isBurning(){
		return this.isBurning && fuel > 0 && !hotReactor.content().isEmpty();
	}

	public int getFuel() {
		return fuel;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound hotReactorNBT = new NBTTagCompound();
		NBTTagCompound coldReactorNBT = new NBTTagCompound();
		NBTTagCompound separatorInNBT = new NBTTagCompound();
		NBTTagCompound separatorOutNBT = new NBTTagCompound();
		NBTTagCompound filterInNBT = new NBTTagCompound();
		NBTTagCompound filterOutNBT = new NBTTagCompound();
		hotReactor.writeToNBT(hotReactorNBT);
		coldReactor.writeToNBT(coldReactorNBT);
		separatorIn.writeToNBT(separatorInNBT);
		separatorOut.writeToNBT(separatorOutNBT);
		filterIn.writeToNBT(filterInNBT);
		filterOut.writeToNBT(filterOutNBT);
		tag.setTag("hotReactor", hotReactorNBT);
		tag.setTag("coldReactor", coldReactorNBT);
		tag.setTag("separatorIn", separatorInNBT);
		tag.setTag("separatorOut", separatorOutNBT);
		tag.setTag("filterIn", filterInNBT);
		tag.setTag("filterOut", filterOutNBT);
		byte state = 0;
		state |= this.hasFilterOut ? 1 : 0;
		state |= this.hasReactorOut ? 2 : 0;
		state |= this.hasSeparatorOut ? 4 : 0;
		state |= this.hasReactor ? 8 : 0;
		tag.setByte("state", state);
		tag.setInteger("fuel", this.fuel);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		hotReactor.readFromNBT(tag.getCompoundTag("hotReactor"));
		coldReactor.readFromNBT(tag.getCompoundTag("coldReactor"));
		separatorIn.readFromNBT(tag.getCompoundTag("separatorIn"));
		separatorOut.readFromNBT(tag.getCompoundTag("separatorOut"));
		filterIn.readFromNBT(tag.getCompoundTag("filterIn"));
		filterOut.readFromNBT(tag.getCompoundTag("filterOut"));
		byte state = tag.getByte("state");
		this.hasFilterOut = (state & 1) != 0;
		this.hasReactorOut = (state & 2) != 0;
		this.hasSeparatorOut = (state & 4) != 0;
		this.hasReactor = (state & 8) != 0;
		tag.setByte("state", state);
		this.fuel = tag.getInteger("fuel");
	}

	public boolean hasFilterOut() {
		return hasFilterOut ;
	}

	public boolean hasReactorOut() {
		return hasReactorOut;
	}

	public boolean hasSeparatorOut() {
		return hasSeparatorOut;
	}

	public boolean hasReactor() {
		return hasReactor;
	}
	
	private boolean isFuel(ItemStack stack){
		return stack.isEmpty()?false:stack.getItem()==Items.BLAZE_POWDER;
	}
	
	private boolean isSubstanceContainer(ItemStack stack){
		return stack.isEmpty()?false:stack.getItem()==IblisItems.SUBSTANCE_CONTAINER;
	}
	
	private boolean isFluidContainer(ItemStack stack) {
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
		return stack.isEmpty()?false:fluidHandler==null?false:fluidHandler.drain(Integer.MAX_VALUE, false)!=null;
	}
	
	private boolean isPotion(ItemStack stack) {
		return stack.isEmpty()?false:stack.getItem() == Items.POTIONITEM;
	}

	@Override
	public float getReactionYield() {
		return reactionYield;
	}

	public void doAction(EntityPlayerMP player, Actions action) {
		reactionYield =1.0f-0.8f/(1.0f+(float)PlayerSkills.CHEMISTRY.getFullSkillValue(player));
		PlayerSkills.CHEMISTRY.raiseSkill(player, 1.0);
		ItemStack stack = player.getHeldItemMainhand();
		switch(action){
		case ADD_FUEL:
			if(isFuel(stack)){
				stack.shrink(1);
				fuel +=2000;
			}
			break;
		case CLEAR_FLASK:
			if(isSubstanceContainer(stack)) {
				stack.getTagCompound().removeTag("content");
			}
			break;
		case FILL_FILTER:
			this.fillReactor(filterIn, player);
			filterIn.dumpLiquidsTo(filterOut);
			filterIn.exhaustGasesTo(atmosphere);
			break;
		case FILL_FILTER_FLASK:
			if(this.hasFilterOut)
				this.fillReactor(filterOut, player);
			break;
		case FILL_REACTOR:
			if(this.hasReactor)
				this.fillReactor(hotReactor, player);
			break;
		case FILL_REACTOR_OUT:
			if(this.hasReactorOut)
				this.fillReactor(coldReactor, player);
			break;
		case FILL_SEPARATOR:
			this.fillReactor(separatorIn, player);
			break;
		case FILL_SEPARATOR_OUT:
			if(this.hasSeparatorOut)
				this.fillReactor(separatorOut, player);
			break;
		case GRAB_RESIDUUM:
			if(!this.filterIn.content().isEmpty()) {
				ItemStack itemStackIn = new ItemStack(IblisItems.SUBSTANCE_CONTAINER,1,0);
				NBTTagCompound tag = new NBTTagCompound();
				filterIn.writeToNBT(tag);
				itemStackIn.setTagCompound(tag);
				if(player.inventory.addItemStackToInventory(itemStackIn)){
					this.filterIn.clear();
				}
			}
			break;
		case PLACE_FILTER_FLASK:
			if(!this.hasFilterOut && isSubstanceContainer(stack) && stack.getMetadata() == ItemSubstanceContainer.FLASK){
				this.hasFilterOut = true;
				this.filterOut.readFromNBT(stack.getTagCompound());;
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			break;
		case PLACE_REACTOR:
			if(!this.hasReactor && isSubstanceContainer(stack) && stack.getMetadata() == ItemSubstanceContainer.REACTOR){
				this.hasReactor = true;
				this.hotReactor.readFromNBT(stack.getTagCompound());;
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			break;
		case PLACE_REACTOR_OUT:
			if(!this.hasReactorOut && isSubstanceContainer(stack) && stack.getMetadata() == ItemSubstanceContainer.FLASK){
				this.hasReactorOut = true;
				this.coldReactor.readFromNBT(stack.getTagCompound());;
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			break;
		case PLACE_SEPARATOR_OUT:
			if(!this.hasSeparatorOut && isSubstanceContainer(stack) && stack.getMetadata() == ItemSubstanceContainer.FLASK){
				this.hasSeparatorOut = true;
				this.separatorOut.readFromNBT(stack.getTagCompound());;
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}
			break;
		case TAKE_FILTER_FLASK:
			if (this.hasFilterOut) {
				ItemStack itemStackIn = new ItemStack(IblisItems.SUBSTANCE_CONTAINER, 1, ItemSubstanceContainer.FLASK);
				NBTTagCompound tag = new NBTTagCompound();
				filterOut.writeToNBT(tag);
				itemStackIn.setTagCompound(tag);
				if (player.inventory.addItemStackToInventory(itemStackIn)) {
					this.filterOut.clear();
					this.hasFilterOut = false;
				}
			}
			break;
		case TAKE_REACTOR:
			if (this.hasReactor) {
				ItemStack itemStackIn = new ItemStack(IblisItems.SUBSTANCE_CONTAINER, 1,
						ItemSubstanceContainer.REACTOR);
				NBTTagCompound tag = new NBTTagCompound();
				hotReactor.writeToNBT(tag);
				itemStackIn.setTagCompound(tag);
				if (player.inventory.addItemStackToInventory(itemStackIn)) {
					this.hotReactor.clear();
					this.hasReactor = false;
				}
			}
			break;
		case TAKE_REACTOR_OUT:
			if (this.hasReactorOut) {
				ItemStack itemStackIn = new ItemStack(IblisItems.SUBSTANCE_CONTAINER, 1, ItemSubstanceContainer.FLASK);
				NBTTagCompound tag = new NBTTagCompound();
				coldReactor.writeToNBT(tag);
				itemStackIn.setTagCompound(tag);
				if (player.inventory.addItemStackToInventory(itemStackIn)) {
					this.coldReactor.clear();
					this.hasReactorOut = false;
				}
			}
			break;
		case TAKE_SEPARATOR_OUT:
			if (this.hasSeparatorOut) {
				ItemStack itemStackIn = new ItemStack(IblisItems.SUBSTANCE_CONTAINER, 1, ItemSubstanceContainer.FLASK);
				NBTTagCompound tag = new NBTTagCompound();
				separatorOut.writeToNBT(tag);
				itemStackIn.setTagCompound(tag);
				if (player.inventory.addItemStackToInventory(itemStackIn)) {
					this.separatorOut.clear();
					this.hasSeparatorOut = false;
				}
			}
			break;
		case TOGGLE_BURNER:
			this.isBurning =!this.isBurning;
			break;
		case USE_SEPARATOR:
			this.useSeparator();
			break;
		default:
			break;}
	}
	
	private void fillReactor(Reactor reactor, EntityPlayer player){
		ItemStack stack = player.getHeldItemMainhand();
		if(isSubstanceContainer(stack)) {
			reactor.addContentOf(IblisItems.SUBSTANCE_CONTAINER.readReactor(stack.getTagCompound()));
			stack.getTagCompound().removeTag("content");
			player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.getItem().getContainerItem(stack));
		} else if(isFluidContainer(stack)){
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
			FluidStack fs = fluidHandler.drain(Integer.MAX_VALUE, true);
			if(ChemistryRegistry.fluidStackToSubstanceStack(reactor, fs)) {
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.getItem().getContainerItem(stack));
			}
		} else if(isPotion(stack)){
			if(ChemistryRegistry.potionToSubstanceStack(reactor, PotionUtils.getPotionFromItem(stack))) {
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.getItem().getContainerItem(stack));
			}
		}
		else {
			if (ChemistryRegistry.itemStackToSubstanceStack(reactor, stack)) {
				player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.getItem().getContainerItem(stack));
			}
		}
	}
	
	private void useSeparator(){
		if(!this.hasSeparatorOut)
			return;
		if(this.separatorIn.content().isEmpty())
			return;
		this.separatorIn.dumpHeaviestTo(this.separatorOut);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		this.readFromNBT(pkt.getNbtCompound());
	}
	
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 255, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeToNBT(nbt);
		return nbt;
	}
	
	public void sendUpdatePacket() {
		for (Object player : world.playerEntities) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				if (playerMP.getDistanceSq(getPos()) < 16)
					playerMP.connection.sendPacket(this.getUpdatePacket());
			}
		}
	}
	
	public enum Actions {
		CLEAR_FLASK,
		TOGGLE_BURNER,
		ADD_FUEL,
		FILL_FILTER,
		GRAB_RESIDUUM,
		FILL_FILTER_FLASK,
		TAKE_FILTER_FLASK,
		PLACE_FILTER_FLASK,
		FILL_REACTOR,
		TAKE_REACTOR,
		PLACE_REACTOR,
		FILL_REACTOR_OUT,
		TAKE_REACTOR_OUT,
		PLACE_REACTOR_OUT,
		FILL_SEPARATOR,
		USE_SEPARATOR,
		FILL_SEPARATOR_OUT,
		TAKE_SEPARATOR_OUT,
		PLACE_SEPARATOR_OUT
	}
}