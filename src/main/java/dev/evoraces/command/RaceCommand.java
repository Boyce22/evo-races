package dev.evoraces.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.evoraces.EvoRaces;
import dev.evoraces.attribute.AttributeSystem;
import dev.evoraces.player.PlayerData;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class RaceCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("race")
                        // Acessível a todos os jogadores (nível 0)
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .then(
                                CommandManager.argument("id", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            RaceRegistry.getInstance().getAllRaces().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(RaceCommand::executeSelectRace)
                        )
                        .then(CommandManager.literal("clear").executes(RaceCommand::executeClearRace))
                        .then(CommandManager.literal("info").executes(RaceCommand::executeRaceInfo))
                        // Subcomando admin para forçar raça em outro jogador
                        .then(
                                CommandManager.literal("set")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .then(
                                                CommandManager.argument("id", StringArgumentType.word())
                                                        .suggests((context, builder) -> {
                                                            RaceRegistry.getInstance().getAllRaces().keySet()
                                                                    .forEach(builder::suggest);
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(RaceCommand::executeAdminSetRace)
                                        )
                        )
        );
    }

    private static int executeSelectRace(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String raceId = StringArgumentType.getString(context, "id");

        RaceRegistry registry = RaceRegistry.getInstance();

        if (!registry.hasRace(raceId)) {
            source.sendError(Text.literal("§cRaça não encontrada: " + raceId));
            source.sendError(Text.literal("§7Disponíveis: " + String.join(", ", registry.getAllRaces().keySet())));
            return 0;
        }

        if (PlayerData.playerHasRace(player)) {
            source.sendError(Text.literal("§cVocê já tem uma raça: " + PlayerData.getPlayerRaceId(player)));
            source.sendError(Text.literal("§7Use §e/race clear §7para removê-la antes."));
            return 0;
        }

        PlayerData.setPlayerRace(player, raceId);
        AttributeSystem.forceUpdatePlayerAttributes(player);

        Race race = registry.getRace(raceId);
        source.sendFeedback(() -> Text.literal("§aRaça selecionada com sucesso!"), false);
        source.sendFeedback(() -> Text.literal("§7Você agora é um: §e" + race.getName()), false);
        source.sendFeedback(() -> Text.literal("§7" + race.getAttributes().toDisplayString()), false);

        EvoRaces.LOGGER.info("Jogador {} selecionou a raça: {}", player.getName().getString(), raceId);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeClearRace(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();

        if (!PlayerData.playerHasRace(player)) {
            source.sendError(Text.literal("§cVocê não tem uma raça selecionada."));
            return 0;
        }

        String currentRaceId = PlayerData.getPlayerRaceId(player);
        PlayerData data = PlayerData.get(player);
        data.clearRace();
        PlayerData.save(player, data);
        AttributeSystem.forceUpdatePlayerAttributes(player);

        source.sendFeedback(() -> Text.literal("§aRaça removida com sucesso!"), false);
        source.sendFeedback(() -> Text.literal("§7Você não é mais: §e" + currentRaceId), false);

        EvoRaces.LOGGER.info("Jogador {} removeu a raça: {}", player.getName().getString(), currentRaceId);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRaceInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();

        if (!PlayerData.playerHasRace(player)) {
            source.sendError(Text.literal("§cVocê não tem uma raça selecionada."));
            source.sendError(Text.literal("§7Use §e/race <id> §7para selecionar."));
            return 0;
        }

        String raceId = PlayerData.getPlayerRaceId(player);
        RaceRegistry registry = RaceRegistry.getInstance();

        if (!registry.hasRace(raceId)) {
            source.sendError(Text.literal("§cSua raça não existe mais no sistema: " + raceId));
            return 0;
        }

        Race race = registry.getRace(raceId);
        source.sendFeedback(() -> Text.literal("§6=== Informações da Raça ==="), false);
        source.sendFeedback(() -> Text.literal("§eNome: §7" + race.getName()), false);
        source.sendFeedback(() -> Text.literal("§eID: §7" + race.getId()), false);
        source.sendFeedback(() -> Text.literal("§e" + race.getAttributes().toDisplayString()), false);

        if (!race.getAbilities().isEmpty())
            source.sendFeedback(() -> Text.literal("§eHabilidades: §7" + String.join(", ", race.getAbilities())), false);
        if (!race.getWeaknesses().isEmpty())
            source.sendFeedback(() -> Text.literal("§eFraquezas: §7" + String.join(", ", race.getWeaknesses())), false);
        if (!race.getEvolutionPaths().isEmpty())
            source.sendFeedback(() -> Text.literal("§eCaminhos de Evolução: §7" + String.join(", ", race.getEvolutionPaths())), false);

        return Command.SINGLE_SUCCESS;
    }

    // Comando admin: /race set <id> — força raça mesmo que o jogador já tenha uma
    private static int executeAdminSetRace(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String raceId = StringArgumentType.getString(context, "id");

        RaceRegistry registry = RaceRegistry.getInstance();
        if (!registry.hasRace(raceId)) {
            source.sendError(Text.literal("§cRaça não encontrada: " + raceId));
            return 0;
        }

        PlayerData.setPlayerRace(player, raceId);
        AttributeSystem.forceUpdatePlayerAttributes(player);

        Race race = registry.getRace(raceId);
        source.sendFeedback(() -> Text.literal("§a[Admin] Raça definida: §e" + race.getName()), false);

        EvoRaces.LOGGER.info("[Admin] Raça de {} definida para: {}", player.getName().getString(), raceId);
        return Command.SINGLE_SUCCESS;
    }
}