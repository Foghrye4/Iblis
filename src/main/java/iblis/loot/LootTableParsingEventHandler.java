package iblis.loot;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootTableParsingEventHandler {

	private final static int MAX_LOOT_LEVEL = 8;
	private final ResourceLocation libraryLootTable = new ResourceLocation(IblisMod.MODID, "library_loot");
	/** An array of loot tables which will be adjusted by event handler**/
	private final String[] lootTablesPath = new String[] {"pyramid","city","jungle_temple","simple_dungeon","library","mansion"};
	
	@SubscribeEvent
	public void onLootTableParseEvent(LootTableLoadEvent event) {
		String lootTableDomain = event.getName().getResourceDomain();
		if(lootTableDomain.equals(IblisMod.MODID))
			return;
		String lootTableName = event.getName().getResourcePath();
		boolean isLibrary = false;
		for(String sequenceInPath: lootTablesPath) {
			if(lootTableName.contains(sequenceInPath)) {
				isLibrary = true;
				break;
			}
		}
		if(!isLibrary)
			return;
		LootTable table = event.getTable();
		int lootLevel = 0;
		if (lootTableName.contains("level")) {
			String intInput = trimNonNumericCharacters(lootTableName);
			if (!intInput.isEmpty())
				lootLevel = Integer.parseInt(intInput);
			if (lootLevel > MAX_LOOT_LEVEL)
				lootLevel = MAX_LOOT_LEVEL;
		}
		if(isLibrary) {
			LootTable iblisLootTable = event.getLootTableManager().getLootTableFromLocation(libraryLootTable);
			for (PlayerSkills skill : PlayerSkills.values()) {
				String skillName = skill.name();
				LootPool pool = iblisLootTable.getPool(skillName + "_level_" + lootLevel);
				if (pool != null)
					table.addPool(pool);
				else
					IblisMod.log.error("Error gaining pool for " + skillName + " and level " + lootLevel);
			}
		}
	}

	private String trimNonNumericCharacters(String s) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
				if (buffer.length() == 0 && i > 0) {
					if (s.charAt(i - 1) == '-')
						buffer.append('-');
				}
				buffer.append(s.charAt(i));
			} else if (buffer.length() != 0) {
				break;
			}
		}
		return buffer.toString();
	}
}
