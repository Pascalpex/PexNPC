package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class Util {

    public static void teleportPlayerToNpc(Player player, long id) {
        if(Config.getSpectatorModeOnTeleport()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        player.teleport(NPCData.getNpc(id).getLocation());
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 1);
        player.sendMessage(MessageHandler.prefixedMini("<aqua>Du wurdest zum NPC mit der ID <gold>" + id + " <aqua>teleportiert."));
    }

    public boolean checkName(String name) {
        String prename = name.length() > 16 ? name.substring(0, 16) : name;
        for (NPC npc : NPCData.getAllNpcs()) {
            if (npc.getName().startsWith(prename)) {
                return false;
            }
        }
        return true;
    }

}
