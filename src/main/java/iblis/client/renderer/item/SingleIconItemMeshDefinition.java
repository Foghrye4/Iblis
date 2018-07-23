package iblis.client.renderer.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SingleIconItemMeshDefinition implements ItemMeshDefinition {

	private final ModelResourceLocation modelResourceLocation;

	public SingleIconItemMeshDefinition(String domain, String path, String variant) {
		modelResourceLocation = new ModelResourceLocation(new ResourceLocation(domain, path), variant);
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		return modelResourceLocation;
	}
}
