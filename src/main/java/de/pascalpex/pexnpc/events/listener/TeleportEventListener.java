package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.NPCSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportEventListener implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                NPCSender.sendNpcsToPlayer(player);
            }
        }.runTaskLater(PexNPC.getInstance(), 1);
    }

}
