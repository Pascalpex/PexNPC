package de.pascalpex.pexnpc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.pascalpex.pexnpc.commands.IDArgument;
import de.pascalpex.pexnpc.commands.NPCSlotArgument;
import de.pascalpex.pexnpc.commands.subcommands.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PexNPCBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            IDArgument idArgument = new IDArgument();
            HelpSubcommand helpSubcommand = new HelpSubcommand();

            LiteralCommandNode<CommandSourceStack> advancedCommandRoot = Commands.literal("pexnpc")
                    .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("pexnpc.command"))
                    .then(Commands.literal("help")
                            .executes(helpSubcommand))
                    .then(Commands.literal("reload")
                            .executes(new ReloadSubcommand()))
                    .then(Commands.literal("create")
                            .then(Commands.argument("name", StringArgumentType.greedyString())
                                    .executes(new CreateSubcommand())))
                    .then(Commands.literal("list")
                            .executes(new ListSubcommand()))
                    .then(Commands.literal("delete")
                            .then(Commands.argument("npc", idArgument)
                                    .executes(new DeleteSubcommand())))
                    .then(Commands.literal("name")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("name", StringArgumentType.greedyString())
                                            .executes(new NameSubcommand()))))
                    .then(Commands.literal("movehere")
                            .then(Commands.argument("npc", idArgument)
                                    .executes(new MovehereSubcommand())))
                    .then(Commands.literal("tp")
                            .then(Commands.argument("npc", idArgument)
                                    .executes(new TpSubcommand())))
                    .then(Commands.literal("skin")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("skin", StringArgumentType.word())
                                            .suggests((cmdContext, builder) -> {
                                                String currentInput = "";
                                                try {
                                                    currentInput = StringArgumentType.getString(cmdContext, "skin").toLowerCase();
                                                } catch (IllegalArgumentException ignored) {
                                                } // Command does not contain a skin argument yet
                                                for (Player player : Bukkit.getOnlinePlayers()) {
                                                    String playerName = player.getName();
                                                    if (playerName.toLowerCase().startsWith(currentInput)) {
                                                        builder.suggest(playerName);
                                                    }
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(new SkinSubcommand()))))
                    .then(Commands.literal("cmd")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("cmd", StringArgumentType.greedyString())
                                            .executes(new CmdSubcommand()))))
                    .then(Commands.literal("msg")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("msg", StringArgumentType.greedyString())
                                            .executes(new MsgSubcommand()))))
                    .then(Commands.literal("item")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("slot", new NPCSlotArgument())
                                            .executes(new ItemSubcommand()))))
                    .then(Commands.literal("clear")
                            .then(Commands.argument("npc", idArgument)
                                    .executes(new ClearSubcommand())))
                    .then(Commands.literal("inspect")
                            .executes(new InspectSubcommand()))
                    .executes(helpSubcommand)
                    .build();

            commands.registrar().register(advancedCommandRoot);
        });
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new PexNPC();
    }
}
