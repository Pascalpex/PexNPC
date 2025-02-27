package de.pascalpex.pexnpc.util;

import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.files.NPCData;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
        int npcSize = NPCData.getNpcCount();
        String prename = name.length() > 16 ? name.substring(0, 16) : name;
        for (int i = 1; i <= npcSize; i++) {
            String currentName = NPCData.getNpc(i).getName();
            if (currentName.startsWith(prename)) {
                return false;
            }
        }
        return true;
    }

}
