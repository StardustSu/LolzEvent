package net.thisisnico.lolz.proxy;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.velocity.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thisisnico.lolz.common.network.Sync;
import net.thisisnico.lolz.proxy.commands.ClanCommand;
import net.thisisnico.lolz.proxy.commands.WhitelistCommand;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

@Plugin(
        id = "proxy",
        name = "Proxy",
        version = "1.0-SNAPSHOT",
        description = "no",
        authors = {"thisisnico"}
)
public class Proxy {

    @Getter
    private static Proxy instance;

    @Getter
    private final Logger logger;

    @Getter
    private final ProxyServer server;

    private final Path dataDirectory;

    @Inject
    public Proxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Getter
    private static VelocityCommandManager<Player> commandManager;

    @Getter
    private static AnnotationParser<Player> annotationParser;

    @Getter
    private static final List<String> banwords = new ArrayList<>();

    @Getter
    private static final List<String> whitelist = new ArrayList<>();

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        registerCommandManager();

        Sync.registerPointsUpdateAsync((clan, delta) -> {
            logger.info("Received points update for clan " + clan.getTag() + " with delta " + delta);

            var s = String.valueOf(delta);
            if (delta >= 0) s = "+" + s;

            for (String member : clan.getMembers()) {
                var player = server.getPlayer(member).orElse(null);
                if (player == null) continue;

                player.sendMessage(Component.text("§6" + s + " очков команды!"));
            }
        });

        Sync.registerClanRequestAsync((adminAndCount, clan) -> {
            // separate admin and count
            var adminName = adminAndCount.split(" ")[0];
            var count = Integer.parseInt(adminAndCount.split(" ")[1]);

            // get admin player
            var admin = server.getPlayer(adminName).orElseThrow();

            logger.info("Received clan request for clan " + clan.getTag() + " with count " + count);

            // collect online players
            var players = new ArrayList<Player>();
            for (String member : clan.getMembers()) {
                var player = server.getPlayer(member).orElse(null);
                if (player == null) continue;

                players.add(player);
            }

            if (players.size() < count) {
                admin.sendMessage(Component.text("§cНедостаточно игроков клана "+clan.getTag()+" для варпа!"));
                return;
            }

            // teleport *count* random players to admin's server
            var server = admin.getCurrentServer().orElseThrow().getServer();
            for (int i = 0; i < count; i++) {
                var player = players.remove((int) (Math.random() * players.size()));
                player.createConnectionRequest(server).fireAndForget();
            }
        });

        Sync.startThread();

        logger.info("Started Points listener");

        annotationParser.parse(new ClanCommand());
        annotationParser.parse(new WhitelistCommand());

        try {
            var file = new File(dataDirectory.toFile(), "_banlist.txt");
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedReader(new FileReader(file))) {
                stream.lines().forEach(banwords::add);
            }

            logger.info("Loaded ban word list");

        } catch (IOException ignored) {}

        try {
            var file = new File(dataDirectory.toFile(), "_whitelist.txt");
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedReader(new FileReader(file))) {
                stream.lines().forEach(whitelist::add);
            }

            logger.info("Loaded whitelist");

        } catch (IOException ignored) {}

    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent e) {
        logger.info("Saving and shutting down");

        try {
            var file = new File(dataDirectory.toFile(), "_banlist.txt");
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedWriter(new FileWriter(file))) {
                for (String word : banwords) {
                    stream.write(word + "\n");
                }
            }

            logger.info("Saved ban word list");

        } catch (IOException ignored) {}

        try {
            var file = new File(dataDirectory.toFile(), "_whitelist.txt");
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedWriter(new FileWriter(file))) {
                for (String word : whitelist) {
                    stream.write(word + "\n");
                }
            }

            logger.info("Saved whitelist");

        } catch (IOException ignored) {}

        logger.info("Goodbye!");
    }

    @Subscribe
    public void onChat(PlayerChatEvent e) {
        var message = e.getMessage();

        if (message.startsWith("/")) return;

        var check = message
                .replaceAll("\\s", "")
                .replaceAll("a", "а")
                .replaceAll("о", "0")
                .replaceAll("o", "0")
                .replaceAll("c", "с")
                .replaceAll("p", "р")
                .replaceAll("e", "е")
                .replaceAll("x", "х")
                .replaceAll("ь", "b")
                .replaceAll("[1-9]+", "");

        for (String banword : banwords) {
            if (banword == null) continue;
            var b1 = banword.toLowerCase()
                    .replaceAll("a", "а")
                    .replaceAll("о", "0")
                    .replaceAll("o", "0")
                    .replaceAll("c", "с")
                    .replaceAll("p", "р")
                    .replaceAll("e", "е")
                    .replaceAll("x", "х")
                    .replaceAll("ь", "b");
            if (check.contains(b1)) {
                message = "Нико - лучший программист на этой планете!";
                break;
            }
        }

        e.setResult(PlayerChatEvent.ChatResult.message(message));
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent e) {
        if (!whitelist.contains(e.getPlayer().getUsername())) {
            e.getPlayer().disconnect(Component.text("У вас нет доступа на ивент!").color(NamedTextColor.RED));
        }

        e.getPlayer().sendPlayerListHeaderAndFooter(Component.text("§f§lКлан§1§lовый §c§lивент"),
                Component.text("§fСоздано при поддержке §alolz.guru"));
    }

    private void registerCommandManager() {
        final Function<CommandTree<Player>, CommandExecutionCoordinator<Player>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();

        final Function<CommandSource, Player> mapperFunction = (a) -> {
            if (a instanceof Player p) return p;
            return null;
        };
        final Function<Player, CommandSource> mapperFunctionBackwards = (a) -> a;

        try {
            commandManager = new VelocityCommandManager<>(
                    /* Owning plugin */ server.getPluginManager().fromInstance(this).get(),
                    /* Server */ server,
                    /* Coordinator function */ executionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunctionBackwards
            );
        } catch (final Exception e) {
            return;
        }

        final Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple()
                        // This will allow you to decorate commands with descriptions
                        .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();
        annotationParser = new AnnotationParser<>(
                /* Manager */ commandManager,
                /* Command sender type */ Player.class,
                /* Mapper for command meta instances */ commandMetaFunction
        );

        new MinecraftExceptionHandler<Player>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(
                        component -> text()
                                .append(text("(", NamedTextColor.GRAY))
                                .append(text("LZT", NamedTextColor.GREEN))
                                .append(text(") ", NamedTextColor.GRAY))
                                .append(component).build()
                ).apply(commandManager, c -> c);
    }
}
