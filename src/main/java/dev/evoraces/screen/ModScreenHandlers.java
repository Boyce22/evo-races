package dev.evoraces.screen;

import dev.evoraces.EvoRaces;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    // Registra o nosso novo ScreenHandler
    public static final ScreenHandlerType<SteamBoilerScreenHandler> STEAM_BOILER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(EvoRaces.MOD_ID, "steam_boiler"),
                    new ScreenHandlerType<>(SteamBoilerScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    public static void registerScreenHandlers() {
        EvoRaces.LOGGER.info("Registrando Screen Handlers do EvoRaces...");
    }
}