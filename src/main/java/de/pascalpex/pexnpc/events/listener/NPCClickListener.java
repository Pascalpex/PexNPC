package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.events.RightClickNPC;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.external.BungeeMessageSender;
import de.pascalpex.pexnpc.util.external.PlaceholderAPIAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCClickListener implements Listener {

    @EventHandler
    public void onNPCClick(RightClickNPC event) {
        Player player = event.getPlayer();
        PlaceableNPC placeableNPC = PexNPC.findNPCbyMinecraftID(event.getNpc().getId());
        if(placeableNPC == null) {
            return;
        }

        String msg = placeableNPC.getNpc().getMessage();
        if (msg != null && !msg.isBlank()) {
            msg = PlaceholderAPIAdapter.replace(player, msg);
            player.sendMessage(MessageHandler.parseAmpersand(msg));
        }

        String cmd = placeableNPC.getNpc().getCommand();
        if (cmd != null && !cmd.isBlank()) {
            if (Bukkit.getPluginCommand("server") == null && cmd.toLowerCase().startsWith("server")) {
                BungeeMessageSender bungeeMessageSender = new BungeeMessageSender();
                bungeeMessageSender.sendMessage("Connect", cmd.split(" ")[1], player);
            } else {
                Bukkit.dispatchCommand(player, cmd);
            }
            if (Config.getLogCommands()) {
                Bukkit.getConsoleSender().sendMessage(MessageHandler.prefixedMini("<aqua>Player <gold>" + player.getName() + " <aqua>used NPC with ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>to dispatch command: <gold>" + cmd));
            }
        }
    }

}
