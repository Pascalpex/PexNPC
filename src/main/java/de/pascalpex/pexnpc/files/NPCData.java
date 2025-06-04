package de.pascalpex.pexnpc.files;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCEquipment;
import de.pascalpex.pexnpc.npc.NPCItemSlot;
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

    public static final File configFile = new File("plugins/PexNPC", "npcData.yml");
    public static final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);


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
        config.set("npcs" + "." + id + ".command", npc.getCommand());
        config.set("npcs" + "." + id + ".message", npc.getMessage());
        config.set("npcs" + "." + id + ".location" + ".x", loc.getX());
        config.set("npcs" + "." + id + ".location" + ".y", loc.getY());
        config.set("npcs" + "." + id + ".location" + ".z", loc.getZ());
        config.set("npcs" + "." + id + ".location" + ".pitch", loc.getPitch());
        config.set("npcs" + "." + id + ".location" + ".yaw", loc.getYaw());
        config.set("npcs" + "." + id + ".location" + ".world", loc.getWorld().getName());
        config.set("npcs" + "." + id + ".items" + ".HAND", npc.getEquipment().getItem(NPCItemSlot.HAND));
        config.set("npcs" + "." + id + ".items" + ".OFFHAND", npc.getEquipment().getItem(NPCItemSlot.OFFHAND));
        config.set("npcs" + "." + id + ".items" + ".HELMET", npc.getEquipment().getItem(NPCItemSlot.HELMET));
        config.set("npcs" + "." + id + ".items" + ".CHESTPLATE", npc.getEquipment().getItem(NPCItemSlot.CHESTPLATE));
        config.set("npcs" + "." + id + ".items" + ".LEGGINGS", npc.getEquipment().getItem(NPCItemSlot.LEGGINGS));
        config.set("npcs" + "." + id + ".items" + ".BOOTS", npc.getEquipment().getItem(NPCItemSlot.BOOTS));
        config.set("npcs" + "." + id + ".skin" + ".texture", npc.getSkin().texture());
        config.set("npcs" + "." + id + ".skin" + ".signature", npc.getSkin().signature());
        save();
    }

    public static long getNextID() {
        if (!config.contains("npcs") || config.getConfigurationSection("npcs").getKeys(false).isEmpty()) {
            return 1;
        }
        long maxId = 1;
        for (String section : config.getConfigurationSection("npcs").getKeys(false)) {
            long id = Long.parseLong(section);
            maxId = Math.max(id, maxId);
        }
        return maxId + 1;
    }

    public static NPC getNpc(long id) {
        String worldName = config.getString("npcs" + "." + id + ".location" + ".world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        double x = config.getDouble("npcs" + "." + id + ".location" + ".x");
        double y = config.getDouble("npcs" + "." + id + ".location" + ".y");
        double z = config.getDouble("npcs" + "." + id + ".location" + ".z");
        float pitch = (float) config.getDouble("npcs" + "." + id + ".location" + ".pitch");
        float yaw = (float) config.getDouble("npcs" + "." + id + ".location" + ".yaw");
        Location loc = new Location(world, x, y, z, yaw, pitch);

        if (world == null) {
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
        NPCEquipment equipment = new NPCEquipment(handItem, offhandItem, helmetItem, chestplateItem, leggingsItem, bootsItem);

        return new NPC(id, loc, name, skin, cmd, msg, equipment);
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
        save();
    }

}
