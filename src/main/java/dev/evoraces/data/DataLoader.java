package dev.evoraces.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.evoraces.EvoRaces;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceAttributes;
import dev.evoraces.race.RaceRegistry;
import dev.evoraces.race.buffs.CaveSpeedBuff;
import dev.evoraces.race.buffs.RacialBuff;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataLoader {

    private static final Gson GSON = new Gson();
    private static final String LEGACY_FILE = "races.json";
    private static final String NBT_KEY_RACES = "races";
    private static final Identifier FABRIC_ID = new Identifier(EvoRaces.MOD_ID, "data_loader");

    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(new Listener());
    }

    private static final class Listener implements SimpleSynchronousResourceReloadListener {

        @Override
        public Identifier getFabricId() {
            return FABRIC_ID;
        }

        @Override
        public void reload(ResourceManager manager) {
            EvoRaces.LOGGER.info("EvoRaces - 'Data loader' Iniciando carregamento de raças...");
            RaceRegistry registry = RaceRegistry.getInstance();
            registry.clear();

            loadRaces(manager, registry);

            EvoRaces.LOGGER.info("EvoRaces - 'Data loader' {} raça(s) carregada(s).", registry.size());
        }
    }

    private static void loadRaces(ResourceManager manager, RaceRegistry registry) {
        Identifier legacyId = new Identifier(EvoRaces.MOD_ID, LEGACY_FILE);

        manager.getResource(legacyId).ifPresent(resource -> {
            try (InputStreamReader reader = newReader(resource)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);

                if (!hasArray(root, NBT_KEY_RACES))
                    return;

                JsonArray races = root.getAsJsonArray(NBT_KEY_RACES);
                for (int i = 0; i < races.size(); i++) {
                    parseAndRegister(races.get(i).getAsJsonObject(), registry);
                }
            } catch (Exception e) {
                EvoRaces.LOGGER.error("EvoRaces - 'Data loader' Falha ao carregar {}: {}", LEGACY_FILE, e.getMessage());
            }
        });
    }

    private static void parseAndRegister(JsonObject obj, RaceRegistry registry) {
        try {
            String raceId = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();

            RaceAttributes attributes = parseAttributes(obj.getAsJsonObject("attributes"));
            List<String> abilities = parseStringList(obj, "abilities");
            List<String> weaknesses = parseStringList(obj, "weaknesses");
            List<String> evolutionPaths = parseStringList(obj, "evolution_paths");
            List<RacialBuff> buffs = parseBuffs(obj);

            registry.registerRace(new Race(raceId, name, attributes, abilities, weaknesses, evolutionPaths, buffs));
            EvoRaces.LOGGER.debug("EvoRaces - 'Data loader' Raça registrada: {} ({})", name, raceId);

        } catch (Exception e) {
            EvoRaces.LOGGER.error("EvoRaces - 'Data loader' Objeto de raça inválido: {}", e.getMessage());
        }
    }

    private static RaceAttributes parseAttributes(JsonObject obj) {

        int vitality = obj.get("vitality").getAsInt();
        int strength = obj.get("strength").getAsInt();
        int agility = obj.get("agility").getAsInt();
        int intellect = obj.get("intellect").getAsInt();
        int resistance = obj.get("resistance").getAsInt();

        // Atributos opcionais
        int dexterity = obj.has("dexterity") ? obj.get("dexterity").getAsInt() : 100;
        int luck = obj.has("luck") ? obj.get("luck").getAsInt() : 100;

        return new RaceAttributes(vitality, strength, agility, intellect, resistance, dexterity, luck);
    }

    private static List<RacialBuff> parseBuffs(JsonObject raceObj) {
        if (!hasArray(raceObj, "buffs"))
            return Collections.emptyList();

        JsonArray array = raceObj.getAsJsonArray("buffs");
        List<RacialBuff> buffs = new ArrayList<>(array.size());

        for (int i = 0; i < array.size(); i++) {
            JsonObject effectObj = array.get(i).getAsJsonObject();
            RacialBuff buff = parseBuff(effectObj);
            if (buff != null)
                buffs.add(buff);
        }

        return buffs;
    }

    private static RacialBuff parseBuff(JsonObject obj) {
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "cave_bonus" -> new CaveSpeedBuff(
                    obj.get("speed").getAsDouble(),
                    obj.get("threshold_y").getAsInt(),
                    obj.get("haste").getAsInt());
            default -> {
                EvoRaces.LOGGER.warn("EvoRaces - 'Data loader' Tipo de buff desconhecido: '{}'", type);
                yield null;
            }
        };
    }

    private static List<String> parseStringList(JsonObject parent, String key) {
        if (!hasArray(parent, key))
            return Collections.emptyList();

        JsonArray array = parent.getAsJsonArray(key);
        List<String> list = new ArrayList<>(array.size());

        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).getAsString());
        }

        return list;
    }

    private static boolean hasArray(JsonObject obj, String key) {
        return obj.has(key) && obj.get(key).isJsonArray();
    }

    private static InputStreamReader newReader(Resource resource) throws Exception {
        return new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}