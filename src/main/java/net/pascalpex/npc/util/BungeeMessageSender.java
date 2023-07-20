package net.pascalpex.npc.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.pascalpex.npc.Main;
import org.bukkit.entity.Player;

public class BungeeMessageSender {

    public void sendMessage(String channel, String argument, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF(argument);

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

}
