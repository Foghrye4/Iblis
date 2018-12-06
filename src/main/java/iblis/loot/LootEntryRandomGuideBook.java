package iblis.loot;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import iblis.IblisMod;
import iblis.init.IblisItems;
import iblis.player.PlayerSkills;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryRandomGuideBook extends LootEntry {

	protected LootEntryRandomGuideBook(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
		super(weightIn, qualityIn, conditionsIn, entryName);
	}

	@Override
	public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
		PlayerSkills skill = PlayerSkills.values().get(rand.nextInt(PlayerSkills.values().size()));
		ItemStack guideStack = new ItemStack(IblisItems.GUIDE, 1, 1);
		NBTTagCompound guideNBT = new NBTTagCompound();
		NBTTagList skills = new NBTTagList();
		NBTTagCompound skillNBT = new NBTTagCompound();
		skillNBT.setString("name", skill.name());
		skillNBT.setDouble("value", rand.nextFloat() * rand.nextFloat() + 0.1f);
		skills.appendTag(skillNBT);
		guideNBT.setTag("skills", skills);
		if (skill == PlayerSkills.CHEMISTRY)
			guideNBT.setString("resourceLocation", IblisMod.MODID + ":books/chemistry_guide.json");
		guideStack.setTagCompound(guideNBT);
		stacks.add(guideStack);
	}

	@Override
	protected void serialize(JsonObject json, JsonSerializationContext context) {
	}
}
