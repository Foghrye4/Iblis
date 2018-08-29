package iblis.client.renderer.item;

import iblis.IblisMod;
import iblis.chemistry.Reactor;
import iblis.init.IblisItems;
import iblis.item.ItemSubstanceContainer;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SubstanceContainerItemMeshDefinition implements ItemMeshDefinition, IItemColor {
	final ModelResourceLocation pile = new ModelResourceLocation(IblisMod.MODID + ":" + "substance_container",
			"pile");
	final ModelResourceLocation flask_empty = new ModelResourceLocation(IblisMod.MODID + ":" + "substance_container",
			"flask_empty");
	final ModelResourceLocation flask_full = new ModelResourceLocation(IblisMod.MODID + ":" + "substance_container",
			"flask_full");
	final ModelResourceLocation reactor_empty = new ModelResourceLocation(IblisMod.MODID + ":" + "substance_container",
			"reactor_empty");
	final ModelResourceLocation reactor_full = new ModelResourceLocation(IblisMod.MODID + ":" + "substance_container",
			"reactor_full");

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		Reactor reactor = null;
		switch(stack.getMetadata()) {
			case ItemSubstanceContainer.PILE:
				return pile;
			case ItemSubstanceContainer.FLASK:
				if (!stack.hasTagCompound())
					return flask_empty;
				reactor = IblisItems.SUBSTANCE_CONTAINER.readReactor(stack.getTagCompound());
				if(!reactor.content().isEmpty())
					return flask_full;
				return flask_empty;
			case ItemSubstanceContainer.REACTOR:
				if (!stack.hasTagCompound())
					return reactor_empty;
				reactor = IblisItems.SUBSTANCE_CONTAINER.readReactor(stack.getTagCompound());
				if(!reactor.content().isEmpty())
					return reactor_full;
				return reactor_empty;
		}
		return pile;
	}

	public void registerVariants() {
		ModelBakery.registerItemVariants(IblisItems.SUBSTANCE_CONTAINER,
				new ResourceLocation[] { pile, flask_empty, flask_full, reactor_empty, reactor_full});
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		if (!stack.hasTagCompound()){
			return 0xFFFFFF;
		}
		Reactor reactor = IblisItems.SUBSTANCE_CONTAINER.readReactor(stack.getTagCompound());
		if(reactor.content().isEmpty())
			return 0xFFFFFF;
		int color = reactor.content().iterator().next().substance.getColor();
		if(stack.getMetadata() == 0)
			return color;
		if(tintIndex==0)
			return 0xFFFFFF;
		return color;
	}
}
