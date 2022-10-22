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
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thisisnico.lolz.common.network.Sync;
import net.thisisnico.lolz.proxy.commands.ClanCommand;
import org.slf4j.Logger;

import java.io.*;
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

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

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

        Sync.registerPointsUpdate((clan, delta) -> {
            logger.info("Received points update for clan " + clan.getTag() + " with delta " + delta);

            var s = String.valueOf(delta);
            if (delta >= 0) s = "+" + s;

            for (String member : clan.getMembers()) {
                var player = server.getPlayer(member).orElse(null);
                if (player == null) continue;

                player.sendMessage(Component.text("§6" + s + " очков!"));
            }
        });

        annotationParser.parse(new ClanCommand());

        banwords.add("пидор");
        banwords.add("pidor");
        banwords.add("пидар");
        banwords.add("pidar");
        banwords.add("пидоp");
        banwords.add("рidor");

        banwords.add("nigg");
        banwords.add("niger");
        banwords.add("negr");
        banwords.add("негр");
        banwords.add("нигг");

        banwords.add("симп");
        banwords.add("simp");
        banwords.add("cимп");
        banwords.add("simр");

        try {
            var file = new File("/home/container/_banwords.txt");
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedReader(new FileReader(file))) {
                var line = stream.readLine();
                if (!banwords.contains(line)) banwords.add(line);
            }

        } catch (IOException ignored) {}

        try {
            var file = new File("/home/container/_whitelist.txt");
            if (!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.createNewFile();

            try (var stream = new BufferedReader(new FileReader(file))) {
                var line = stream.readLine();
                if (!whitelist.contains(line)) whitelist.add(line);
            }

        } catch (IOException ignored) {}

    }

    @Subscribe
    public void onChat(PlayerChatEvent e) {
        var message = e.getMessage();

        if (message.startsWith("/")) return;

        var nigger = message + "";

        var words = message.toLowerCase().split("\\s+");
        for (String word : words) {
            for (String banword : banwords) {
                var check = word
                        .replaceAll("a", "а")
                        .replaceAll("o", "о")
                        .replaceAll("c", "с")
                        .replaceAll("p", "р")
                        .replaceAll("e", "е")
                        .replaceAll("x", "х")
                        .replaceAll("b", "в")
                        .replaceAll("t", "т")
                        .replaceAll("ь", "b")
                        .replaceAll("h", "н")
                        .replaceAll("n", "п")
                        .replaceAll("m", "м");
                if (check.contains(banword.toLowerCase())) {
                    nigger = message.replace(word, "*".repeat(word.length()));
                    break;
                }
            }
        }

        e.setResult(PlayerChatEvent.ChatResult.message(nigger));
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent e) {
        if (!whitelist.contains(e.getPlayer().getUsername())) {
            e.getPlayer().disconnect(Component.text("У вас нет доступа на ивент!").color(NamedTextColor.RED));
        }
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
