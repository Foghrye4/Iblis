package iblis.tileentity;

import iblis.chemistry.Reactor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLabTable extends TileEntity {
	Reactor hotReactor = new Reactor();
	Reactor coldReactor = new Reactor();
	Reactor separatorIn = new Reactor();
	Reactor separatorOut = new Reactor();
	Reactor filterIn = new Reactor();
	Reactor filterOut = new Reactor();

	public void tick() {
		hotReactor.tick();
		hotReactor.exhaustGasesTo(coldReactor);
		coldReactor.tick();
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
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		hotReactor.readFromNBT(tag.getTag("hotReactor"));
		coldReactor.readFromNBT(tag.getTag("coldReactor"));
		separatorIn.readFromNBT(tag.getTag("separatorIn"));
		separatorOut.readFromNBT(tag.getTag("separatorOut"));
		filterIn.readFromNBT(tag.getTag("filterIn"));
		filterOut.readFromNBT(tag.getTag("filterOut"));
	}

}