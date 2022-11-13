package net.thisisnico.lolz.bukkit;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thisisnico.lolz.bukkit.commands.DebugCommand;
import net.thisisnico.lolz.bukkit.commands.WarpCommand;
import net.thisisnico.lolz.bukkit.handlers.SystemHandler;
import net.thisisnico.lolz.bukkit.utils.ScoreboardUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

public class BukkitUtils {

    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static PaperCommandManager<Player> commandManager;

    @Getter
    private static AnnotationParser<Player> annotationParser;

    public static void instantiate(JavaPlugin plugin) {
        if (BukkitUtils.plugin != null)
            throw new IllegalStateException("BukkitUtils is already instantiated!");
        BukkitUtils.plugin = plugin;

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        ScoreboardUtils.runUpdater();

        plugin.getServer().getPluginManager().registerEvents(new SystemHandler(), plugin);

        final Function<CommandTree<Player>, CommandExecutionCoordinator<Player>> executionCoordinatorFunction =
                CommandExecutionCoordinator.simpleCoordinator();

        final Function<CommandSender, Player> mapperFunction = (a) -> {
            if (a instanceof Player p) return p;
            return null;
        };
        final Function<Player, CommandSender> mapperFunctionBackwards = (a) -> a;

        try {
            commandManager = new PaperCommandManager<>(
                    /* Owning plugin */ plugin,
                    /* Coordinator function */ executionCoordinatorFunction,
                    /* Command Sender -> C */ mapperFunction,
                    /* C -> Command Sender */ mapperFunctionBackwards
            );
        } catch (final Exception e) {
            plugin.getLogger().severe("Failed to initialize the command manager");
            /* Disable the plugin */
            System.out.println("Failed to initialize the command manager");
            System.out.println("Failed to initialize the command manager");
            System.out.println("Failed to initialize the command manager");
            System.out.println("Failed to initialize the command manager");
            System.out.println("Failed to initialize the command manager");
            System.out.println("Failed to initialize the command manager");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier();
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
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

        annotationParser.parse(new DebugCommand());
        annotationParser.parse(new WarpCommand());
    }

    public static <T> void registerCommand(final T command) {
        annotationParser.parse(command);
    }

    public static void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

}
