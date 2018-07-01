package iblis.client.renderer.item;

import iblis.IblisMod;
import iblis.constants.NBTTagsKeys;
import iblis.init.IblisItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CrossbowItemMeshDefinition implements ItemMeshDefinition {
	final ModelResourceLocation mCrossbowNoAmmo = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"inventory_no_ammo");
	final ModelResourceLocation mCrossbowStretched1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"inventory_stretched_upper");
	final ModelResourceLocation mCrossbowAmmo1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"inventory_ammo_1");
	final ModelResourceLocation mCrossbowStretched2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"inventory_stretched_lower");
	final ModelResourceLocation mCrossbowAmmo2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"inventory");
	final ModelResourceLocation mCrossbowAimNoAmmo = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"aiming_no_ammo");
	final ModelResourceLocation mCrossbowAimStretched1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"aiming_stretched_upper");
	final ModelResourceLocation mCrossbowAimAmmo1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"aiming_ammo_1");
	final ModelResourceLocation mCrossbowAimStretched2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"aiming_stretched_lower");
	final ModelResourceLocation mCrossbowAimAmmo2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow",
			"aiming");

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && player.isHandActive() && player.getActiveItemStack() == stack) {
			if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTTagsKeys.AMMO))
				return mCrossbowAimNoAmmo;
			int cockedBowString = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
			switch (stack.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10).tagCount()) {
			case 0:
				if(cockedBowString == 1)
					return mCrossbowAimStretched1;
				return mCrossbowAimNoAmmo;
			case 1:
				if(cockedBowString == 2)
					return mCrossbowAimStretched2;
				return mCrossbowAimAmmo1;
			default:
				return mCrossbowAimAmmo2;
			}
		}
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTTagsKeys.AMMO))
			return mCrossbowNoAmmo;
		int cockedBowString = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
		switch (stack.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10).tagCount()) {
		case 0:
			if(cockedBowString == 1)
				return mCrossbowStretched1;
			return mCrossbowNoAmmo;
		case 1:
			if(cockedBowString == 2)
				return mCrossbowStretched2;
			return mCrossbowAmmo1;
		default:
			return mCrossbowAmmo2;
		}
	}

	public void registerVariants() {
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW,
				new ResourceLocation[] { mCrossbowNoAmmo, mCrossbowStretched1, mCrossbowStretched2,
						mCrossbowAimStretched1, mCrossbowAimStretched2, mCrossbowAmmo1, mCrossbowAmmo2,
						mCrossbowAimNoAmmo, mCrossbowAimAmmo1, mCrossbowAimAmmo2 });
	}
}
