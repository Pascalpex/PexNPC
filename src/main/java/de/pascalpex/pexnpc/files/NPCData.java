package de.pascalpex.pexnpc.files;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCSkin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class NPCData {

    public static File configFile = new File("plugins/PexNPC", "npcData.yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);


    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                config.save(configFile);
            }
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            PexNPC.logger().log(Level.SEVERE, e.toString());
        }

    }

    public static void save() {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    PexNPC.logger().log(Level.SEVERE, e.toString());
                }
            }
        }.runTaskAsynchronously(PexNPC.getInstance());
    }

    public static void saveNpc(NPC npc) {
        Location loc = npc.getLocation();
        long id = npc.getId();

        config.set("npcs" + "." + id + ".name", npc.getName());
        config.set("npcs" + "." + id + ".command", "");
        config.set("npcs" + "." + id + ".location" + ".x", loc.getX());
        config.set("npcs" + "." + id + ".location" + ".y", loc.getY());
        config.set("npcs" + "." + id + ".location" + ".z", loc.getZ());
        config.set("npcs" + "." + id + ".location" + ".pitch", loc.getPitch());
        config.set("npcs" + "." + id + ".location" + ".yaw", loc.getYaw());
        config.set("npcs" + "." + id + ".location" + ".world", loc.getWorld().getName());
        config.set("npcs" + "." + id + ".items" + ".HAND", NPC.nullStack);
        config.set("npcs" + "." + id + ".items" + ".OFFHAND", NPC.nullStack);
        config.set("npcs" + "." + id + ".items" + ".HELMET", NPC.nullStack);
        config.set("npcs" + "." + id + ".items" + ".CHESTPLATE", NPC.nullStack);
        config.set("npcs" + "." + id + ".items" + ".LEGGINGS", NPC.nullStack);
        config.set("npcs" + "." + id + ".items" + ".BOOTS", NPC.nullStack);
        config.set("npcs" + "." + id + ".skin" + ".texture", npc.getSkin().texture());
        config.set("npcs" + "." + id + ".skin" + ".signature", npc.getSkin().signature());
        save();
    }

    public static int getNpcCount() {
        if (!config.contains("npcs")) {
            return 0;
        }
        return config.getConfigurationSection("npcs").getKeys(false).size();
    }

    public static NPC getNpc(long id) {
        World world = Bukkit.getWorld(config.getString("npcs" + "." + id + ".location" + ".world"));
        double x = config.getDouble("npcs" + "." + id + ".location" + ".x");
        double y = config.getDouble("npcs" + "." + id + ".location" + ".y");
        double z = config.getDouble("npcs" + "." + id + ".location" + ".z");
        float pitch = (float) config.getDouble("npcs" + "." + id + ".location" + ".pitch");
        float yaw = (float) config.getDouble("npcs" + "." + id + ".location" + ".yaw");
        Location loc = new Location(world, x, y, z, yaw, pitch);

        if(world == null) {
            return null;
        }

        String cmd = config.getString("npcs" + "." + id + ".command");
        String msg = config.getString("npcs" + "." + id + ".message");

        ItemStack handItem = config.getItemStack("npcs" + "." + id + ".items" + ".HAND");
        ItemStack offhandItem = config.getItemStack("npcs" + "." + id + ".items" + ".OFFHAND");
        ItemStack helmetItem = config.getItemStack("npcs" + "." + id + ".items" + ".HELMET");
        ItemStack chestplateItem = config.getItemStack("npcs" + "." + id + ".items" + ".CHESTPLATE");
        ItemStack leggingsItem = config.getItemStack("npcs" + "." + id + ".items" + ".LEGGINGS");
        ItemStack bootsItem = config.getItemStack("npcs" + "." + id + ".items" + ".BOOTS");

        String name = config.getString("npcs" + "." + id + ".name");

        String skinTexture = config.getString("npcs" + "." + id + ".skin" + ".texture");
        String skinSignature = config.getString("npcs" + "." + id + ".skin" + ".signature");

        NPCSkin skin = new NPCSkin(skinTexture, skinSignature);

        return new NPC(id, loc, name, skin, cmd, msg, handItem, offhandItem, helmetItem, chestplateItem, leggingsItem, bootsItem);
    }

    public static List<NPC> getAllNpcs() {
        if (!config.contains("npcs")) {
            return Collections.emptyList();
        }

        return config.getConfigurationSection("npcs").getKeys(false).stream().map(id -> getNpc(Long.parseLong(id))).filter(Objects::nonNull).toList();
    }

    public static void deleteNpc(NPC npc) {
        if (!config.contains("npcs")) {
            return;
        }

        config.set("npcs" + "." + npc.getId(), null);
        long newID = 1;
        for (String section : config.getConfigurationSection("npcs").getKeys(false)) {

            World world = Bukkit.getWorld(config.getString("npcs" + "." + section + ".location" + ".world"));
            double x = config.getDouble("npcs" + "." + section + ".location" + ".x");
            double y = config.getDouble("npcs" + "." + section + ".location" + ".y");
            double z = config.getDouble("npcs" + "." + section + ".location" + ".z");
            float pitch = (float) config.getDouble("npcs" + "." + section + ".location" + ".pitch");
            float yaw = (float) config.getDouble("npcs" + "." + section + ".location" + ".yaw");

            ItemStack handItem = config.getItemStack("npcs" + "." + section + ".items" + ".HAND");
            ItemStack offhandItem = config.getItemStack("npcs" + "." + section + ".items" + ".OFFHAND");
            ItemStack helmetItem = config.getItemStack("npcs" + "." + section + ".items" + ".HELMET");
            ItemStack chestplateItem = config.getItemStack("npcs" + "." + section + ".items" + ".CHESTPLATE");
            ItemStack leggingsItem = config.getItemStack("npcs" + "." + section + ".items" + ".LEGGINGS");
            ItemStack bootsItem = config.getItemStack("npcs" + "." + section + ".items" + ".BOOTS");

            String cmd = config.getString("npcs" + "." + section + ".command");
            String msg = config.getString("npcs" + "." + section + ".message");
            String name = config.getString("npcs" + "." + section + ".name");
            String skinTexture = config.getString("npcs" + "." + section + ".skin" + ".texture");
            String skinSignature = config.getString("npcs" + "." + section + ".skin" + ".signature");

            config.set("npcs." + section, null);

            config.set("npcs" + "." + newID + ".name", name);
            config.set("npcs" + "." + newID + ".command", cmd);
            config.set("npcs" + "." + newID + ".message", msg);
            config.set("npcs" + "." + newID + ".location" + ".x", x);
            config.set("npcs" + "." + newID + ".location" + ".y", y);
            config.set("npcs" + "." + newID + ".location" + ".z", z);
            config.set("npcs" + "." + newID + ".location" + ".pitch", pitch);
            config.set("npcs" + "." + newID + ".location" + ".yaw", yaw);
            config.set("npcs" + "." + newID + ".location" + ".world", world.getName());

            config.set("npcs" + "." + newID + ".items" + ".HAND", handItem);
            config.set("npcs" + "." + newID + ".items" + ".OFFHAND", offhandItem);
            config.set("npcs" + "." + newID + ".items" + ".HELMET", helmetItem);
            config.set("npcs" + "." + newID + ".items" + ".CHESTPLATE", chestplateItem);
            config.set("npcs" + "." + newID + ".items" + ".LEGGINGS", leggingsItem);
            config.set("npcs" + "." + newID + ".items" + ".BOOTS", bootsItem);

            config.set("npcs" + "." + newID + ".skin" + ".texture", skinTexture);
            config.set("npcs" + "." + newID + ".skin" + ".signature", skinSignature);

            newID++;
        }
        save();
    }

}
