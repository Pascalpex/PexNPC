package de.pascalpex.pexnpc;

import de.pascalpex.pexnpc.events.PacketReader;
import de.pascalpex.pexnpc.events.listener.*;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.external.Metrics;
import de.pascalpex.pexnpc.util.VersionChecker;
import de.pascalpex.pexnpc.util.external.PlaceholderAPIAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PexNPC extends JavaPlugin {

    private static VersionChecker versionChecker;
    private static PexNPC instance;
    private static Logger logger;
    private static List<PlaceableNPC> placedNPCs;
    private static PacketReader packetReader;
    private static String pluginVersion;

    @Override
    public void onEnable() {
        instance = this;
        pluginVersion = getPluginMeta().getVersion();
        logger = getLogger();
        placedNPCs = new ArrayList<>();
        packetReader = new PacketReader();
        versionChecker = new VersionChecker(pluginVersion);

        new Metrics(this, 14923);

        Config.load();
        NPCData.load();

        MessageHandler.prefix = MessageHandler.parse(Config.getPrefix());
        PlaceholderAPIAdapter.startup();

        versionChecker.clearUpdateNotified();
        if (Config.getUpdateChecker()) {
            versionChecker.fetchNewestVersion();
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        loadAllNPCs();

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                packetReader.inject(player);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "Could not inject the PacketReader!");
            }
        }

        // Listeners
        this.getServer().getPluginManager().registerEvents(new JoinEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new QuitEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new NPCClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new TeleportEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new RespawnEventListener(), this);

        Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<green>PexNPC " + pluginVersion + " von Pascalpex wurde aktiviert."));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            packetReader.uninject(player);
        }
        NPCSender.removeEverything();
        placedNPCs.clear();

        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<red>PexNPC " + pluginVersion + " von Pascalpex wurde deaktiviert."));
    }

    public static PexNPC getInstance() {
        return instance;
    }

    public static Logger logger() {
        return logger;
    }

    public static List<PlaceableNPC> getPlacedNpcs() {
        return placedNPCs;
    }

    public static VersionChecker getVersionChecker() {
        return versionChecker;
    }

    public static PacketReader getPacketReader() {
        return packetReader;
    }

    public static PlaceableNPC findNPCbyMinecraftID(int id) {
        for(PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
            if(placeableNPC.getServerPlayer().getId() == id) {
                return placeableNPC;
            }
        }
        return null;
    }

    private static void loadAllNPCs() {
        List<NPC> npcs = NPCData.getAllNpcs();
        for(NPC npc : npcs) {
            PlaceableNPC placeableNPC = new PlaceableNPC(npc);
            NPCSender.sendNpcToPlayers(placeableNPC);
            placedNPCs.add(placeableNPC);
        }
    }
}