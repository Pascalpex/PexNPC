package de.pascalpex.pexnpc.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageHandler {
    public static Component prefix = parse("");
    private static final TextColor defaultColor = NamedTextColor.AQUA;
    private static final TextColor errorColor = NamedTextColor.RED;
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component parseSection(String legacyMessage) {
        return LegacyComponentSerializer.legacySection().deserialize(legacyMessage);
    }

    public static Component parseAmpersand(String legacyMessage) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(legacyMessage);
    }

    public static Component parse(String miniMessage)  {
        if(mm == null) {
            return Component.text("");
        }
        return mm.deserialize(miniMessage);
    }

    public static Component basicMessage(String message) {
        return prefix.append(Component.text(" " + message).color(defaultColor));
    }

    public static Component errorMessage(String message) {
        return prefix.append(Component.text(" " + message).color(errorColor));
    }

    public static Component prefixedMini(String miniMessage) {
        Component miniComponent = parse("<aqua>" + miniMessage);
        return prefix.append(Component.text(" ")).append(miniComponent);
    }

}
