package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.PexNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class VersionChecker {

    private String newestVersion = "";
    private final String pluginVersion;
    private final Set<String> updateNotified;

    public VersionChecker(String pluginVersion) {
        this.pluginVersion = pluginVersion;
        updateNotified = new HashSet<>();
    }

    public void playerJoin(Player player) {
        if (!updateNotified.contains(player.getUniqueId().toString())) {
            if (!newestVersion.isEmpty()) {
                if (!newestVersion.equals(pluginVersion)) {
                    player.sendMessage(MessageHandler.basicMessage("Eine neue Version von PexNPC ist verfügbar: " + newestVersion));
                    player.sendMessage(MessageHandler.basicMessage("Download hier: https://pascalpex.de/files/pexnpc/PexNPC.jar"));
                }
            }
        }
        updateNotified.add(player.getUniqueId().toString());
    }

    public void clearUpdateNotified() {
        updateNotified.clear();
    }

    public void fetchNewestVersion() {
        try {
            String versionUrl = "https://pascalpex.de/files/pexnpc/version.yml";
            URL url = new URI(versionUrl).toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            if ((str = in.readLine()) != null) {
                newestVersion = str.toLowerCase();
                if (!newestVersion.equals(pluginVersion)) {
                    Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("Eine neue Version von PexNPC ist verfügbar: " + newestVersion));
                    Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("Download hier: https://pascalpex.de/files/pexnpc/PexNPC.jar"));
                } else {
                    Bukkit.getConsoleSender().sendMessage(MessageHandler.basicMessage("Du verwendest die neuste Version von PexNPC: " + newestVersion));
                }
            }
            in.close();
        } catch (Exception ignored) {
        }
    }

}
