package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.NPCSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnEventListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                NPCSender.sendNpcsToPlayer(player);
            }
        }.runTaskLater(PexNPC.getInstance(), 1);
    }

}
