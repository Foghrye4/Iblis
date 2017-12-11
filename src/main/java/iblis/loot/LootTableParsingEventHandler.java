package iblis.loot;

import iblis.IblisMod;
import iblis.player.PlayerSkills;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootTableParsingEventHandler {

	private final static int MAX_LOOT_LEVEL = 16;
	private final ResourceLocation libraryLootTable = new ResourceLocation(IblisMod.MODID, "library_loot");
	private final ResourceLocation dungeonLootTable = new ResourceLocation(IblisMod.MODID, "dungeon_loot");
	/** An array of loot tables which will be adjusted by event handler **/
	private final String[] lootTablesPath = new String[] { "pyramid", "city", "jungle_temple", "simple_dungeon",
			"library", "mansion" };

	@SubscribeEvent
	public void onLootTableParseEvent(LootTableLoadEvent event) {
		String lootTableDomain = event.getName().getResourceDomain();
		if (lootTableDomain.equals(IblisMod.MODID))
			return;
		String lootTableName = event.getName().getResourcePath();
		if (lootTableDomain.equals("labyrinth")) {
			this.handleLabyrinthLootTables(event.getLootTableManager(), event.getTable(), lootTableName);
			return;
		}
		boolean skipLoadLoot = true;
		for(String allowedLoot:lootTablesPath){
			if(lootTableName.contains(allowedLoot)) {
				skipLoadLoot = false;
				break;
			}
		}
		if(skipLoadLoot)
			return;
		LootTable table = event.getTable();
		LootTable iblisLootTable = event.getLootTableManager().getLootTableFromLocation(libraryLootTable);
		for (PlayerSkills skill : PlayerSkills.values()) {
			String skillName = skill.name();
			LootPool pool = iblisLootTable.getPool(skillName + "_level_" + 0);
			if (pool != null)
				table.addPool(pool);
		}
	}

	private void handleLabyrinthLootTables(LootTableManager lootTableManager, LootTable lootTable,
			String lootTableName) {
		int lootLevel = 0;
		String intInput = trimNonNumericCharacters(lootTableName);
		if (!intInput.isEmpty())
			lootLevel = Integer.parseInt(intInput);
		if (lootLevel > MAX_LOOT_LEVEL)
			lootLevel = MAX_LOOT_LEVEL;
		if (lootTableName.contains("library_loot_tables"))
			handleLabyrinthLibraryLootTable(lootTableManager, lootTable, lootLevel);
		else if (lootTableName.contains("dungeon_loot_tables"))
			handleLabyrinthDungeonLootTable(lootTableManager, lootTable, lootLevel);
	}

	private void handleLabyrinthDungeonLootTable(LootTableManager lootTableManager, LootTable lootTable,
			int lootLevel) {
		LootTable iblisLootTable = lootTableManager.getLootTableFromLocation(dungeonLootTable);
		LootPool pool = iblisLootTable.getPool("level_" + lootLevel);
		if (pool != null)
			lootTable.addPool(pool);
	}

	private void handleLabyrinthLibraryLootTable(LootTableManager lootTableManager, LootTable lootTable,
			int lootLevel) {
		LootTable iblisLootTable = lootTableManager.getLootTableFromLocation(libraryLootTable);
		for (PlayerSkills skill : PlayerSkills.values()) {
			String skillName = skill.name();
			LootPool pool = iblisLootTable.getPool(skillName + "_level_" + lootLevel);
			if (pool != null)
				lootTable.addPool(pool);
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
