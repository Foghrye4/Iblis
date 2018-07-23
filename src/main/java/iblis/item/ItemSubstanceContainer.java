package iblis.item;

import java.util.Collection;
import java.util.List;

import iblis.chemistry.Reactor;
import iblis.chemistry.Substance;
import iblis.chemistry.SubstanceStack;
import iblis.init.IblisSubstances;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSubstanceContainer extends Item {
	
	Reactor reactor = new Reactor();
	
	public static final int PILE = 0;
	public static final int FLASK = 1;
	public static final int REACTOR = 2;
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		switch(stack.getMetadata()){
		case PILE:
			return "item.pile";
		case FLASK:
			return "item.flask";
		case REACTOR:
			return "item.reactor";
		}
		return "iblis.item.pile";
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (!this.isInCreativeTab(tab))
			return;
       	ItemStack itemStack = new ItemStack(this,1,PILE);
       	NBTTagCompound nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.YEAST, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,PILE);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.SALTPETER, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,PILE);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.SULPHUR, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,PILE);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.MERCURY2_FULMINATE, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,FLASK);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.NITRIC_ACID, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,FLASK);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.SULPHURIC_ANHYDRIDE, 1000);
       	reactor.getSubstanceStack(IblisSubstances.SULPHURIC_ANHYDRIDE).gaseousAmount=1000;
       	reactor.getSubstanceStack(IblisSubstances.SULPHURIC_ANHYDRIDE).liquidAmount=1000;
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,FLASK);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.ETHANOLE, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
       	
       	itemStack = new ItemStack(this,1,REACTOR);
       	nbt = new NBTTagCompound();
       	reactor.clear();
       	reactor.putSubstance(IblisSubstances.MERCURY, 1000);
       	reactor.writeToNBT(nbt);
       	itemStack.setTagCompound(nbt);
       	subItems.add(itemStack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (worldIn == null)
			return;
		if (!stack.hasTagCompound())
			return;
		reactor.readFromNBT(stack.getTagCompound());
		Collection<SubstanceStack> content = reactor.content();
		if(content.isEmpty()) {
			tooltip.add(I18n.format("iblis.empty"));
		}
		for(SubstanceStack ss:content){
			tooltip.add(I18n.format(ss.substance.unlocalizedName+".amount", ss.amount()));
		}
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack){
		if(stack.getMetadata()==0)
			return ItemStack.EMPTY;
		return stack;
	} 

	public Reactor readReactor(NBTTagCompound tagCompound) {
		reactor.readFromNBT(tagCompound);
		return reactor;
	}
	public ItemStack build(Substance substanceIn, float amountIn) {
		reactor.clear();
		reactor.putSubstance(substanceIn, amountIn);
		NBTTagCompound nbt = new NBTTagCompound();
		reactor.writeToNBT(nbt);
		ItemStack stack = new ItemStack(this,1,FLASK);
		stack.setTagCompound(nbt);
		return stack;
	}
}
