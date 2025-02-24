package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.Main;
import de.pascalpex.pexnpc.files.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Config.getUpdateChecker()) {
            if (player.hasPermission("pexnpc.update")) {
                Main.getVersionChecker().playerJoin(player);
            }
        }
    }

}
