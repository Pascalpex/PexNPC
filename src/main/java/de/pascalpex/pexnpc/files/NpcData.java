package de.pascalpex.pexnpc.files;

import de.pascalpex.pexnpc.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class NpcData {

    public static File configFile = new File("plugins/PexNPC", "npcData.yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);


    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                save();
            }
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Main.logger().log(Level.SEVERE, e.toString());
        }

    }

    public static void save() {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    Main.logger().log(Level.SEVERE, e.toString());
                }
            }
        }.runTask(Main.getInstance());
    }

}
