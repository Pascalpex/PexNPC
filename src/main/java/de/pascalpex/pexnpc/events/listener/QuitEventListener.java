package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.events.PacketReader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEventListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PacketReader reader = new PacketReader();
        reader.uninject(player);
        NPCClickListener.inspectors.remove(player);
    }
}
