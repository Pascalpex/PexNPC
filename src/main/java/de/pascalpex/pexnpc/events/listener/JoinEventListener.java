package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.events.PacketReader;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.npc.NPCSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class JoinEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Config.getUpdateChecker()) {
            if (player.hasPermission("pexnpc.update")) {
                PexNPC.getVersionChecker().playerJoin(player);
            }
        }

        PacketReader reader = new PacketReader();
        try {
            reader.inject(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            PexNPC.logger().log(Level.SEVERE, "Could not inject the PacketReader!");
        }
        NPCSender.sendNpcsToPlayer(player);
    }

}
