package dev.evoraces.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.evoraces.EvoRaces;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceAttributes;
import dev.evoraces.race.RaceRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return new Identifier(EvoRaces.MOD_ID, "data_loader");
                }

                @Override
                public void reload(ResourceManager manager) {
                    EvoRaces.LOGGER.info("Carregando dados do EvoRaces...");
                    loadRaces(manager);
                    EvoRaces.LOGGER.info("Dados do EvoRaces carregados com sucesso!");
                }
            }
        );
    }

    private static void loadRaces(ResourceManager manager) {
        RaceRegistry registry = RaceRegistry.getInstance();
        registry.clear();

        Identifier racesId = new Identifier(EvoRaces.MOD_ID, "races.json");

        manager.getResource(racesId).ifPresentOrElse(resource -> {
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);

                if (!root.has("races") || !root.get("races").isJsonArray()) {
                    EvoRaces.LOGGER.warn("races.json não contém um array 'races' válido.");
                    return;
                }

                JsonArray racesArray = root.getAsJsonArray("races");

                for (int i = 0; i < racesArray.size(); i++) {
                    JsonObject raceObj = racesArray.get(i).getAsJsonObject();

                    String raceId = raceObj.get("id").getAsString();
                    String name   = raceObj.get("name").getAsString();
                    String description = raceObj.has("description") ? raceObj.get("description").getAsString() : "";

                    JsonObject attributesObj = raceObj.getAsJsonObject("attributes");
                    RaceAttributes attributes = new RaceAttributes(
                        attributesObj.get("vitality").getAsInt(),
                        attributesObj.get("strength").getAsInt(),
                        attributesObj.get("agility").getAsInt(),
                        attributesObj.get("intellect").getAsInt(),
                        attributesObj.get("resistance").getAsInt()
                    );

                    List<String> abilities      = loadStringList(raceObj, "abilities");
                    List<String> weaknesses     = loadStringList(raceObj, "weaknesses");
                    List<String> evolutionPaths = loadStringList(raceObj, "evolution_paths");

                    Race race = new Race(raceId, name, description, attributes, abilities, weaknesses, evolutionPaths);
                    registry.registerRace(race);

                    EvoRaces.LOGGER.debug("Raça carregada: {} ({})", name, raceId);
                }

                EvoRaces.LOGGER.info("Total de raças carregadas: {}", registry.size());

            } catch (Exception e) {
                EvoRaces.LOGGER.error("Erro ao carregar races.json: {}", e.getMessage(), e);
            }
        }, () -> EvoRaces.LOGGER.warn("races.json não encontrado em data/evoraces/races.json!"));
    }

    private static List<String> loadStringList(JsonObject parent, String key) {
        List<String> list = new ArrayList<>();
        if (parent.has(key) && parent.get(key).isJsonArray()) {
            JsonArray array = parent.getAsJsonArray(key);
            for (int i = 0; i < array.size(); i++) {
                list.add(array.get(i).getAsString());
            }
        }
        return list;
    }
}