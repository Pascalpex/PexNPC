package de.pascalpex.pexnpc;

import de.pascalpex.pexnpc.events.listener.JoinEventListener;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.files.NpcData;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.util.Metrics;
import de.pascalpex.pexnpc.util.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private static final VersionChecker versionChecker = new VersionChecker();

    private static Main instance;
    private static Logger logger;
    private static List<NPC> npcs;
    private static String version = "";

    @Override
    public void onEnable() {
        version = getPlugin(this.getClass()).getDescription().getVersion();
        instance = this;
        logger = getLogger();
        npcs = new ArrayList<>();

        Config.load();
        NpcData.load();

        new Metrics(this, 14923);

        if (Config.getUpdateChecker()) {
            versionChecker.fetchNewestVersion();
        }

        // Listeners
        this.getServer().getPluginManager().registerEvents(new JoinEventListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public static Main getInstance() {
        return instance;
    }

    public static Logger logger() {
        return logger;
    }

    public static List<NPC> getNpcs() {
        return npcs;
    }

    public static String getVersion() {
        return version;
    }

    public static VersionChecker getVersionChecker() {
        return versionChecker;
    }
}