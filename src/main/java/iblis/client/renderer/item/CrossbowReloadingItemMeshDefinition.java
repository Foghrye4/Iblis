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

public class CrossbowReloadingItemMeshDefinition implements ItemMeshDefinition {
	final ModelResourceLocation m1_f0 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"inventory");
	final ModelResourceLocation m1_f1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_upper_1");
	final ModelResourceLocation m1_f2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_upper_2");
	final ModelResourceLocation m1_f3 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_upper_3");
	final ModelResourceLocation m1_f4 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_upper_4");
	final ModelResourceLocation m1_f5 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_upper_5");
	final ModelResourceLocation m1_f6 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"arming_upper_1");
	final ModelResourceLocation m1_f7 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"arming_upper_2");
	final ModelResourceLocation m2_f1 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_lower_1");
	final ModelResourceLocation m2_f2 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_lower_2");
	final ModelResourceLocation m2_f3 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_lower_3");
	final ModelResourceLocation m2_f4 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_lower_4");
	final ModelResourceLocation m2_f5 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"stretching_lower_5");
	final ModelResourceLocation m2_f6 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"arming_lower_1");
	final ModelResourceLocation m2_f7 = new ModelResourceLocation(IblisMod.MODID + ":" + "double_crossbow_reloading",
			"arming_lower_2");

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) {
		int cockedBowstring = 0;
		int ammo = 0;
		if (stack.hasTagCompound()) {
			cockedBowstring = stack.getTagCompound().getInteger(NBTTagsKeys.COCKED_STATE);
			ammo = stack.getTagCompound().getTagList(NBTTagsKeys.AMMO, 10).tagCount();
		}
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null && player.isHandActive() && player.getActiveItemStack() == stack) {
			int current = player.getItemInUseCount();
			if (ammo == 0 && cockedBowstring == 0) {
				if (current <= 2)
					return m1_f5;
				else if (current <= 3)
					return m1_f4;
				else if (current <= 4)
					return m1_f3;
				else if (current <= 5)
					return m1_f2;
				else if (current <= 6)
					return m1_f1;
				else
					return m1_f0;
			}
			if (ammo == 0 && cockedBowstring == 1) {
				if (current <= 2)
					return m1_f7;
				else if (current <= 4)
					return m1_f6;
				else
					return m1_f5;
			}
			if (ammo == 1 && cockedBowstring == 1) {
				if (current <= 2)
					return m2_f5;
				else if (current <= 3)
					return m2_f4;
				else if (current <= 4)
					return m2_f3;
				else if (current <= 5)
					return m2_f2;
				else if (current <= 6)
					return m2_f1;
				else
					return m1_f7;
			}
			if (ammo == 1 && cockedBowstring == 2) {
				if (current <= 2)
					return m2_f7;
				else if (current <= 4)
					return m2_f6;
				else
					return m2_f5;
			}
		}
		switch (ammo) {
		case 0:
			if (cockedBowstring == 1)
				return m1_f5;
			return m1_f0;
		case 1:
			if (cockedBowstring == 2)
				return m2_f5;
			return m1_f7;
		default:
			return m2_f7;
		}
	}

	public void registerVariants() {
		ModelBakery.registerItemVariants(IblisItems.CROSSBOW_RELOADING, new ResourceLocation[] { m1_f0, m1_f1, m1_f2,
				m1_f3, m1_f4, m1_f5, m1_f6, m1_f7, m2_f1, m2_f2, m2_f3, m2_f4, m2_f5, m2_f6, m2_f7 });
	}
}
