package de.pascalpex.pexnpc.files;

import de.pascalpex.pexnpc.PexNPC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Config {

    public static File configFile = new File("plugins/PexNPC", "config.yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                config.options().setHeader(List.of("Skin Mode 1: No second layer, no cape     2: Only second layer     3: Second layer and cape"));
                config.set("skinMode", 2);
                config.set("prefix", "<gray>[<yellow>PexNPC<gray>]");
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
            if (!config.contains("spectatorModeOnTeleport")) {
                config.set("spectatorModeOnTeleport", true);
            }
            save();
        } catch (IOException | InvalidConfigurationException e) {
            PexNPC.logger().log(Level.SEVERE, "PexNPC was unable to load the config file");
        }

    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            PexNPC.logger().log(Level.SEVERE, "PexNPC was unable to save the config file");
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
        return config.getString("prefix");
    }

    public static int getSkinTimeout() {
        return config.getInt("skinTimeout");
    }

    public static boolean getSpectatorModeOnTeleport() {
        return config.getBoolean("spectatorModeOnTeleport");
    }

}