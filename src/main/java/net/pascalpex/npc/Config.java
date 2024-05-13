package net.pascalpex.npc;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static File configFile = new File("plugins/PexNPC", "config.yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                config.options().header("Skin Mode 1: No second layer, no cape     2: Only second layer     3: Second layer and cape");
                config.set("skinMode", 2);
                config.set("prefix", "&7[&ePexNPC&7]");
                config.set("skinTimeout", 40);
                save();
            }
            config.load(configFile);
            if (!config.contains("updateChecker")) {
                config.set("updateChecker", true);
            }
            if (!config.contains("logNPCClickedCommands")) {
                config.set("logNPCClickedCommands", false);
            }
            save();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static boolean getUpdateChecker() {
        return config.getBoolean("updateChecker");
    }

    public static boolean getLogCommands() {
        return config.getBoolean("logNPCClickedCommands");
    }

    public static int getSkinMode() {
        return config.getInt("skinMode");
    }

    public static String getPrefix() {
        return config.getString("prefix").replace("&", "§");
    }

    public static int getSkinTimeout() {
        return config.getInt("skinTimeout");
    }

}
