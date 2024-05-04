package net.pascalpex.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerPlayer;
import net.pascalpex.npc.util.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NpcData {

    public static File configFile = new File("plugins/PexNPC","npcData.yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    public static ItemStack nullStack = new ItemStack(Material.AIR);

    public static void load() {
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.exists()) {
                configFile.createNewFile();
                save();
            }
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static void save() {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTask(Main.getInstance());
    }

    public static void saveNPC(Location loc, String name, String[] skin, int id) {
        config.set("npcs" + "." + id + ".name", name);
        config.set("npcs" + "." + id + ".command", "");
        config.set("npcs" + "." + id + ".location" + ".x", loc.getX());
        config.set("npcs" + "." + id + ".location" + ".y", loc.getY());
        config.set("npcs" + "." + id + ".location" + ".z", loc.getZ());
        config.set("npcs" + "." + id + ".location" + ".pitch", loc.getPitch());
        config.set("npcs" + "." + id + ".location" + ".yaw", loc.getYaw());
        config.set("npcs" + "." + id + ".location" + ".world", loc.getWorld().getName());
        config.set("npcs" + "." + id + ".items" + ".HAND", nullStack);
        config.set("npcs" + "." + id + ".items" + ".OFFHAND", nullStack);
        config.set("npcs" + "." + id + ".items" + ".HELMET", nullStack);
        config.set("npcs" + "." + id + ".items" + ".CHESTPLATE", nullStack);
        config.set("npcs" + "." + id + ".items" + ".LEGGINGS", nullStack);
        config.set("npcs" + "." + id + ".items" + ".BOOTS", nullStack);
        config.set("npcs" + "." + id + ".skin" + ".texture", skin[0]);
        config.set("npcs" + "." + id + ".skin" + ".signature", skin[1]);
        save();
    }

    public static int getNPCs() {
        if(!config.contains("npcs")) {
            return 0;
        }
        return config.getConfigurationSection("npcs").getKeys(false).size();
    }

    public static String getName(int id) {
        return config.getString("npcs" + "." + id + ".name");
    }

    public static Location getLocation(int id) {
        World world = Bukkit.getWorld(config.getString("npcs" + "." + id + ".location" + ".world"));
        double x = config.getDouble("npcs" + "." + id + ".location" + ".x");
        double y = config.getDouble("npcs" + "." + id + ".location" + ".y");
        double z = config.getDouble("npcs" + "." + id + ".location" + ".z");
        float pitch = (float) config.getDouble("npcs" + "." + id + ".location" + ".pitch");
        float yaw = (float) config.getDouble("npcs" + "." + id + ".location" + ".yaw");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void removeNPC(int id) {
        if(!config.contains("npcs")) {
            return;
        }

        config.set("npcs" + "." + id, null);
        int idCount = 1;
        for(String section : config.getConfigurationSection("npcs").getKeys(false)) {

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

            config.set("npcs" + "." + idCount + ".name", name);
            config.set("npcs" + "." + idCount + ".command", cmd);
            config.set("npcs" + "." + idCount + ".message", msg);
            config.set("npcs" + "." + idCount + ".location" + ".x", x);
            config.set("npcs" + "." + idCount + ".location" + ".y", y);
            config.set("npcs" + "." + idCount + ".location" + ".z", z);
            config.set("npcs" + "." + idCount + ".location" + ".pitch", pitch);
            config.set("npcs" + "." + idCount + ".location" + ".yaw", yaw);
            config.set("npcs" + "." + idCount + ".location" + ".world", world.getName());

            config.set("npcs" + "." + idCount + ".items" + ".HAND", handItem);
            config.set("npcs" + "." + idCount + ".items" + ".OFFHAND", offhandItem);
            config.set("npcs" + "." + idCount + ".items" + ".HELMET", helmetItem);
            config.set("npcs" + "." + idCount + ".items" + ".CHESTPLATE", chestplateItem);
            config.set("npcs" + "." + idCount + ".items" + ".LEGGINGS", leggingsItem);
            config.set("npcs" + "." + idCount + ".items" + ".BOOTS", bootsItem);

            config.set("npcs" + "." + idCount + ".skin" + ".texture", skinTexture);
            config.set("npcs" + "." + idCount + ".skin" + ".signature", skinSignature);

            idCount++;
        }

        save();

        for(Player player : Bukkit.getOnlinePlayers()) {
            for(ServerPlayer npc : NPC.getNPCs()) {
                NPC.removeNPC(player, npc);
            }
        }

        NPC.clear();

        NpcData.loadNPCs();

        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(NPC.getNPCs() == null) {
                    return;
                }
                if(NPC.getNPCs().isEmpty()) {
                    return;
                }
                NPC.addJoinPacket(player);
            }
        }
    }

    public static void loadNPCs() {

        if(!config.contains("npcs")) {
            return;
        }

        config.getConfigurationSection("npcs").getKeys(false).forEach(npc -> {
            World world = Bukkit.getWorld(config.getString("npcs" + "." + npc + ".location" + ".world"));
            double x = config.getDouble("npcs" + "." + npc + ".location" + ".x");
            double y = config.getDouble("npcs" + "." + npc + ".location" + ".y");
            double z = config.getDouble("npcs" + "." + npc + ".location" + ".z");
            float pitch = (float) config.getDouble("npcs" + "." + npc + ".location" + ".pitch");
            float yaw = (float) config.getDouble("npcs" + "." + npc + ".location" + ".yaw");
            Location loc = new Location(world, x, y, z, yaw, pitch);

            String cmd = config.getString("npcs" + "." + npc + ".command");
            String msg = config.getString("npcs" + "." + npc + ".message");

            ItemStack handItem = config.getItemStack("npcs" + "." + npc + ".items" + ".HAND");
            ItemStack offhandItem = config.getItemStack("npcs" + "." + npc + ".items" + ".OFFHAND");
            ItemStack helmetItem = config.getItemStack("npcs" + "." + npc + ".items" + ".HELMET");
            ItemStack chestplateItem = config.getItemStack("npcs" + "." + npc + ".items" + ".CHESTPLATE");
            ItemStack leggingsItem = config.getItemStack("npcs" + "." + npc + ".items" + ".LEGGINGS");
            ItemStack bootsItem = config.getItemStack("npcs" + "." + npc + ".items" + ".BOOTS");

            String name = config.getString("npcs" + "." + npc + ".name");
            String trimmedName = name.substring(0, Math.min(name.length(), 16));
            String suffix = name.length() > 16 ? name.substring(16) : "";
            if(trimmedName.endsWith("§")) {
                suffix = "§" + suffix;
            }
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), trimmedName);

            String skinTexture = config.getString("npcs" + "." + npc + ".skin" + ".texture");
            String skinSignature = config.getString("npcs" + "." + npc + ".skin" + ".signature");
            gameProfile.getProperties().put("textures", new Property("textures", skinTexture, skinSignature));

            try {
                NPC.loadNPC(loc, gameProfile, Integer.parseInt(npc), cmd, msg, handItem, offhandItem, helmetItem, chestplateItem, leggingsItem, bootsItem, suffix);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void setCMD(int id, String cmd) {
        config.set("npcs" + "." + id + ".command", cmd);
        save();
    }

    public static void setMSG(int id, String msg) {
        config.set("npcs" + "." + id + ".message", msg);
        save();
    }

    public static void changeName(int id, String name) {
        config.set("npcs" + "." + id + ".name", name);
        save();
    }

    public static void equipNPC(int id, String slot, ItemStack item) {
        config.set("npcs" + "." + id + ".items." + slot, item);
        save();
    }

    public static void moveNPC(int id, Location loc) {
        config.set("npcs" + "." + id + ".location" + ".x", loc.getX());
        config.set("npcs" + "." + id + ".location" + ".y", loc.getY());
        config.set("npcs" + "." + id + ".location" + ".z", loc.getZ());
        config.set("npcs" + "." + id + ".location" + ".pitch", loc.getPitch());
        config.set("npcs" + "." + id + ".location" + ".yaw", loc.getYaw());
        config.set("npcs" + "." + id + ".location" + ".world", loc.getWorld().getName());
        save();
    }

    public static void changeSkin(Player player, int id, String skinName) {
        String[] skin = NPC.getSkin(player, skinName);

        config.set("npcs" + "." + id + ".skin" + ".texture", skin[0]);
        config.set("npcs" + "." + id + ".skin" + ".signature", skin[1]);
        save();
    }
}
