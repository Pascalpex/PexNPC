package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class VersionChecker {

    private final String versionUrl = "https://pascalpex.de/files/pexnpc/version.yml";
    private String newestVersion = "";
    private final Set<String> updateNotified;

    public VersionChecker() {
        updateNotified = new HashSet<>();
    }

    public void playerJoin(Player player) {
        if (!updateNotified.contains(player.getUniqueId().toString())) {
            if (!newestVersion.isEmpty()) {
                if (!newestVersion.equals(Main.getVersion())) {
                    player.sendMessage(MessageHandler.basicMessage("Eine neue Version von PexNPC ist verfügbar: " + newestVersion));
                    player.sendMessage(MessageHandler.basicMessage("Download hier: https://pascalpex.de/files/pexnpc/PexNPC.jar"));
                }
            }
        }
        updateNotified.add(player.getUniqueId().toString());
    }

    public void fetchNewestVersion() {
        try {
            URL url = new URI(versionUrl).toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            if ((str = in.readLine()) != null) {
                newestVersion = str.toLowerCase();
                if (!newestVersion.equals(Main.getVersion())) {
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
