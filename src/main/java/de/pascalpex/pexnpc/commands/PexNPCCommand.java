package de.pascalpex.pexnpc.commands;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.commands.subcommands.SubCommands;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PexNPCCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender sender = commandSourceStack.getSender();
        if(sender != commandSourceStack.getExecutor()) {
            sender.sendMessage(MessageHandler.errorMessage("Different senders and executors are currently not supported"));
        }
        if(sender instanceof Player player) {
            if(args.length == 0) {
                SubCommands.HELP.getSubCommand().invoke(player, new String[0]);
            }
            for(SubCommands subCommand : SubCommands.values()) {
                if(subCommand.getSubCommand().getLabel().equalsIgnoreCase(args[0])) {
                    subCommand.getSubCommand().invoke(player, Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        } else {
            sender.sendMessage(MessageHandler.errorMessage("Dieser Befehl ist nur für Spieler geeignet"));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        CommandSender sender = commandSourceStack.getSender();
        final List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {
            if (sender.hasPermission("pexnpc.command")) {
                if (args.length == 1) {
                    for(SubCommands subCommand : SubCommands.values()) {
                        completions.add(subCommand.getSubCommand().getLabel());
                    }
                    completions.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("movehere") || args[0].equalsIgnoreCase("skin") || args[0].equalsIgnoreCase("cmd") || args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("item")) {
                        for (int i = 1; i <= PexNPC.getPlacedNpcs().size(); i++) {
                            completions.add(String.valueOf(i));
                        }
                        completions.removeIf(s -> !s.startsWith(args[1]));
                    }
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("item")) {
                        completions.add("HAND");
                        completions.add("OFFHAND");
                        completions.add("HELMET");
                        completions.add("CHESTPLATE");
                        completions.add("LEGGINGS");
                        completions.add("BOOTS");
                        completions.removeIf(s -> !s.startsWith(args[2].toUpperCase()));
                    }
                    if (args[0].equalsIgnoreCase("skin")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completions.add(player.getName());
                        }
                        completions.removeIf(s -> !s.toLowerCase().startsWith(args[2].toLowerCase()));
                    }
                }
                Collections.sort(completions);
                return completions;
            }
        }
        return Collections.emptyList();
    }
}
