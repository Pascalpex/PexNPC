package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class VersionChecker {

    private String newestVersion = "";
    private final String pluginVersion;
    private final Set<String> updateNotified;

    private static final String DOWNLOAD_LINK = "https://pascalpex.de/files/pexnpc/PexNPC.jar";

    private final Component newVersionMessage;
    private final Component downloadLinkMessage;

    public VersionChecker(String pluginVersion) {
        this.pluginVersion = pluginVersion;
        updateNotified = new HashSet<>();

        if (Config.getUpdateChecker()) {
            fetchNewestVersion();
        }

        newVersionMessage = MessageHandler.prefixedMini("A new version of PexNPC is available: <gold>" + newestVersion);
        downloadLinkMessage = MessageHandler.prefixedMini("Download here: <gold>" + DOWNLOAD_LINK).clickEvent(ClickEvent.openUrl(DOWNLOAD_LINK)).hoverEvent(HoverEvent.showText(MessageHandler.parse("<aqua>Click to download")));

        if (Config.getUpdateChecker()) {
            if (!newestVersion.equals(pluginVersion)) {
                Bukkit.getConsoleSender().sendMessage(newVersionMessage);
                Bukkit.getConsoleSender().sendMessage(downloadLinkMessage);
            } else {
                Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("You are using the newest version: <gold>" + newestVersion));
            }
        }
    }

    public void notifyPlayer(Player player) {
        if (!updateNotified.contains(player.getUniqueId().toString())) {
            if (!newestVersion.isBlank()) {
                if (!newestVersion.equals(pluginVersion)) {
                    player.sendMessage(newVersionMessage);
                    player.sendMessage(downloadLinkMessage);
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
            }
            in.close();
        } catch (IOException | URISyntaxException e) {
            PexNPC.logger().log(Level.WARNING, "PexNPC was unable to fetch newest version");
        }
    }
}
