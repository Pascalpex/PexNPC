package net.pascalpex.npc.util;

import net.pascalpex.npc.NpcData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TabCompletion implements org.bukkit.command.TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final List<String> completions = new ArrayList<>();
        if (label.equalsIgnoreCase("pexnpc")) {
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
                        completions.add("skin");
                        completions.add("cmd");
                        completions.add("msg");
                        completions.add("item");
                        completions.add("clear");
                        completions.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
                    }
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("name") || args[0].equalsIgnoreCase("movehere") || args[0].equalsIgnoreCase("skin") || args[0].equalsIgnoreCase("cmd") || args[0].equalsIgnoreCase("msg") || args[0].equalsIgnoreCase("item")) {
                            for (int i = 1; i <= NpcData.getNPCs(); i++) {
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
        }
        return null;
    }
}
