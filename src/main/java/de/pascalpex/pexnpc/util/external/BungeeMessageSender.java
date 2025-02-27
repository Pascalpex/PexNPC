package de.pascalpex.pexnpc.util.external;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pascalpex.pexnpc.PexNPC;
import org.bukkit.entity.Player;

public class BungeeMessageSender {

    public void sendMessage(String channel, String argument, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        out.writeUTF(argument);

        player.sendPluginMessage(PexNPC.getInstance(), "BungeeCord", out.toByteArray());
    }

}
