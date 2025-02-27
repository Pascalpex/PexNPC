package de.pascalpex.pexnpc.commands;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PexNPCCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        final List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {
            if (sender.hasPermission("pexnpc.command")) {
                if (args.length == 1) {
                    completions.add("help");
                    completions.add("reload");
                    completions.add("create");
                    completions.add("list");
                    completions.add("delete");
                    completions.add("name");
                    completions.add("movehere");
                    completions.add("tp");
                    completions.add("skin");
                    completions.add("cmd");
                    completions.add("msg");
                    completions.add("item");
                    completions.add("clear");
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
