package de.pascalpex.pexnpc.events.listener;

import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.events.ClickNPCEvent;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCItemSlot;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.external.BungeeMessageSender;
import de.pascalpex.pexnpc.util.external.PlaceholderAPIAdapter;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class NPCClickListener implements Listener {

    public static final Set<Player> inspectors = new HashSet<>();

    @EventHandler
    public void onNPCClick(ClickNPCEvent event) {
        Player player = event.getPlayer();
        PlaceableNPC placeableNPC = PexNPC.findNPCbyMinecraftID(event.getNpc().getId());
        if (placeableNPC == null) {
            return;
        }

        if (inspectors.contains(player)) {
            NPC npc = placeableNPC.getNpc();
            Location loc = npc.getLocation();
            player.sendMessage(MessageHandler.prefixedMini("Showing details for NPC with ID <gold>" + npc.getId()));
            player.sendMessage(MessageHandler.parse("<aqua>Name: ").append(MessageHandler.parseSection(npc.getName())));
            player.sendMessage(MessageHandler.parse("<aqua>Location: <gold>X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + " <aqua>in world <gold>" + loc.getWorld().getName()).clickEvent(ClickEvent.runCommand("/pexnpc tp " + npc.getId())).hoverEvent(HoverEvent.showText(MessageHandler.parse("<aqua>Click to teleport"))));
            if (npc.getCommand() != null && !npc.getCommand().isBlank()) {
                player.sendMessage(MessageHandler.parse("<aqua>Command: <gold>/" + npc.getCommand()).clickEvent(ClickEvent.suggestCommand("/" + npc.getCommand())).hoverEvent(HoverEvent.showText(MessageHandler.parse("<aqua>Click to execute"))));
            }
            if (npc.getMessage() != null && !npc.getMessage().isBlank()) {
                player.sendMessage(MessageHandler.parse("<aqua>Message: <gold>" + npc.getMessage()));
            }
            if (npc.hasItems()) {
                player.sendMessage(MessageHandler.parse("<aqua>Items:"));
                for (NPCItemSlot slot : NPCItemSlot.values()) {
                    ItemStack item = npc.getEquipment().getItem(slot);
                    if (item.getType() != Material.AIR) {
                        player.sendMessage(MessageHandler.parse("    <aqua>- " + slot.getName() + ": <gold>" + item.getAmount() + "x " + item.getType()).hoverEvent(HoverEvent.showText(MessageHandler.parse("<aqua>Click to get"))).clickEvent(ClickEvent.callback(audience -> {
                            player.give(item);
                            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        }, ClickCallback.Options.builder().uses(Integer.MAX_VALUE).build())));
                    }
                }
            } else {
                player.sendMessage(MessageHandler.parse("<aqua>Items: <gold>none"));
            }
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
