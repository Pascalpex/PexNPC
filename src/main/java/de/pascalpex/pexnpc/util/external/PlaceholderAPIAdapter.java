package de.pascalpex.pexnpc.util.external;

import de.pascalpex.pexnpc.util.MessageHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderAPIAdapter {

    public static boolean placeholdersEnabled = false;

    public static void startup() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("PlaceholderAPI konnte nicht gefunden werden!"));
            Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("Placeholder werden nicht funktionieren"));
        } else {
            placeholdersEnabled = true;
            Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("PlaceholderAPI wurde gefunden!"));
            Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("Placeholder werden genutzt"));
        }
    }

    public static String replace(Player player, String message) {
        if(!placeholdersEnabled) {
            return message;
        }
        return PlaceholderAPI.setPlaceholders(player, message);
    }

}
