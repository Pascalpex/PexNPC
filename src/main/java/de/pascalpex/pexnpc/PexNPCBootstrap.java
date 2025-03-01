package de.pascalpex.pexnpc;

import com.mojang.brigadier.arguments.LongArgumentType;
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
                            .then(Commands.argument("npc", idArgument)))
                    .then(Commands.literal("name")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("name", StringArgumentType.greedyString()))))
                    .then(Commands.literal("movehere")
                            .then(Commands.argument("npc", idArgument)))
                    .then(Commands.literal("tp")
                            .then(Commands.argument("npc", idArgument)
                                    .executes(new TpSubcommand())))
                    .then(Commands.literal("skin")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("skin", StringArgumentType.greedyString())
                                            .suggests((cmdContext, builder) -> {
                                                for (Player player : Bukkit.getOnlinePlayers()) {
                                                    builder.suggest(player.getName());
                                                }
                                                return builder.buildFuture();
                                            }))))
                    .then(Commands.literal("cmd")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("cmd", StringArgumentType.greedyString()))))
                    .then(Commands.literal("msg")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("msg", StringArgumentType.greedyString()))))
                    .then(Commands.literal("item")
                            .then(Commands.argument("npc", idArgument)
                                    .then(Commands.argument("slot", new NPCSlotArgument()))))
                    .then(Commands.literal("clear")
                            .then(Commands.argument("npc", idArgument)))
                    .executes(helpSubcommand)
                    .build();

            commands.registrar().register(advancedCommandRoot);
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new PexNPC();
    }
}
