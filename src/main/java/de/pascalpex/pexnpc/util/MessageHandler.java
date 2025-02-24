package de.pascalpex.pexnpc.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageHandler {
    public static final Component prefix = parse("");
    private static final TextColor defaultColor = NamedTextColor.GOLD;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component parse(String miniMessage)  {
        if(mm == null) {
            return Component.text("");
        }
        return mm.deserialize(miniMessage);
    }

    public static Component basicMessage(String message) {
        return prefix.append(Component.text(message).color(defaultColor));
    }

}
