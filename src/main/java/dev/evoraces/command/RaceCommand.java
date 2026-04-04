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
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .then(
                                CommandManager.argument("id", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            RaceRegistry.getInstance().getAllRaces().keySet()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(RaceCommand::executeSelectRace))
                        .then(CommandManager.literal("clear").executes(RaceCommand::executeClearRace))
                        .then(CommandManager.literal("info").executes(RaceCommand::executeRaceInfo))
                        .then(
                                CommandManager.literal("set")
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .then(
                                                CommandManager.argument("id", StringArgumentType.word())
                                                        .suggests((ctx, builder) -> {
                                                            RaceRegistry.getInstance().getAllRaces().keySet()
                                                                    .forEach(builder::suggest);
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(RaceCommand::executeAdminSetRace))));
    }

    private static int executeSelectRace(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String raceId = StringArgumentType.getString(ctx, "id");
        RaceRegistry registry = RaceRegistry.getInstance();

        Race race = registry.getRace(raceId);
        if (race == null) {
            sendError(ctx, "§cRaça não encontrada: " + raceId);
            sendError(ctx, "§7Disponíveis: " + String.join(", ", registry.getAllRaces().keySet()));
            return 0;
        }

        if (PlayerData.hasRace(player)) {
            sendError(ctx, "§cVocê já tem uma raça: " + PlayerData.getRaceId(player));
            sendError(ctx, "§7Use §e/race clear §7para removê-la antes.");
            return 0;
        }

        applyRace(player, race);
        sendFeedback(ctx, "§aRaça selecionada com sucesso!");
        sendFeedback(ctx, "§7Você agora é um: §e" + race.getName());
        sendFeedback(ctx, "§7" + race.getAttributes().toDisplayString());
        EvoRaces.LOGGER.info("{} selecionou a raça: {}", player.getName().getString(), raceId);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeClearRace(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        if (!PlayerData.hasRace(player)) {
            sendError(ctx, "§cVocê não tem uma raça selecionada.");
            return 0;
        }

        String currentRaceId = PlayerData.getRaceId(player);
        PlayerData.clearRace(player);
        AttributeSystem.forceUpdatePlayerAttributes(player);

        sendFeedback(ctx, "§aRaça removida com sucesso!");
        sendFeedback(ctx, "§7Você não é mais: §e" + currentRaceId);
        EvoRaces.LOGGER.info("{} removeu a raça: {}", player.getName().getString(), currentRaceId);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRaceInfo(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

        if (!PlayerData.hasRace(player)) {
            sendError(ctx, "§cVocê não tem uma raça selecionada.");
            sendError(ctx, "§7Use §e/race <id> §7para selecionar.");
            return 0;
        }

        Race race = RaceRegistry.getInstance().getRace(PlayerData.getRaceId(player));
        if (race == null) {
            sendError(ctx, "§cSua raça não existe mais no sistema.");
            return 0;
        }

        sendFeedback(ctx, "§6=== Informações da Raça ===");
        sendFeedback(ctx, "§eNome: §7" + race.getName());
        sendFeedback(ctx, "§eID: §7" + race.getId());
        sendFeedback(ctx, "§e" + race.getAttributes().toDisplayString());

        if (!race.getAbilities().isEmpty())
            sendFeedback(ctx, "§eHabilidades: §7" + String.join(", ", race.getAbilities()));
        if (!race.getWeaknesses().isEmpty())
            sendFeedback(ctx, "§eFraquezas: §7" + String.join(", ", race.getWeaknesses()));
        if (!race.getEvolutionPaths().isEmpty())
            sendFeedback(ctx, "§eCaminhos de Evolução: §7" + String.join(", ", race.getEvolutionPaths()));

        return Command.SINGLE_SUCCESS;
    }

    private static int executeAdminSetRace(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        String raceId = StringArgumentType.getString(ctx, "id");

        Race race = RaceRegistry.getInstance().getRace(raceId);
        if (race == null) {
            sendError(ctx, "§cRaça não encontrada: " + raceId);
            return 0;
        }

        applyRace(player, race);
        sendFeedback(ctx, "§a[Admin] Raça definida: §e" + race.getName());
        EvoRaces.LOGGER.info("[Admin] Raça de {} definida para: {}", player.getName().getString(), raceId);
        return Command.SINGLE_SUCCESS;
    }

    private static void applyRace(ServerPlayerEntity player, Race race) {
        PlayerData.setRace(player, race.getId());
        AttributeSystem.forceUpdatePlayerAttributes(player);
    }

    private static void sendFeedback(CommandContext<ServerCommandSource> ctx, String message) {
        ctx.getSource().sendFeedback(() -> Text.literal(message), false);
    }

    private static void sendError(CommandContext<ServerCommandSource> ctx, String message) {
        ctx.getSource().sendError(Text.literal(message));
    }
}