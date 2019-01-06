package iblis_headshots.item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.stream.JsonReader;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class HelmetsConfig {
	public static Object2FloatMap<ResourceLocation> HELMETS_REGISTRY = new Object2FloatOpenHashMap<ResourceLocation>();

	public static void load() {
		File folder = new File(".", "config");
		folder.mkdirs();
		File configFile = new File(folder, "iblis_headshots_helmets_config.json");
		try {
			if (configFile.exists())
				readConfigFromJson(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void readConfigFromJson(File configFile) throws IOException {
		JsonReader reader = new JsonReader(new FileReader(configFile));
		reader.setLenient(true);
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			{
				String item = null;
				int protectionPercents = 0;
				while (reader.hasNext()) {
					String key = reader.nextName();
					if (key.equals("item")) {
						item = reader.nextString();
					} else if (key.equals("protection")) {
						protectionPercents = reader.nextInt();
					} else {
						reader.skipValue();
					}
				}
				if(item!=null)
					HELMETS_REGISTRY.put(new ResourceLocation(item), 1.0f-protectionPercents/100.0f);
			}
			reader.endObject();
		}
		reader.endArray();
		reader.close();
	}
}
