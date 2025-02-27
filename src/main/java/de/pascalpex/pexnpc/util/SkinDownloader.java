package de.pascalpex.pexnpc.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.pascalpex.pexnpc.npc.NPCSkin;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SkinDownloader {

    public static NPCSkin downloadSkin(String playerName) throws IOException, URISyntaxException {
        URL url = new URI("https://api.mojang.com/users/profiles/minecraft/" + playerName).toURL();
        InputStreamReader profileReader = new InputStreamReader(url.openStream());
        String uuid = JsonParser.parseReader(profileReader).getAsJsonObject().get("id").getAsString();

        URL url2 = new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").toURL();
        InputStreamReader skinReader = new InputStreamReader(url2.openStream());
        JsonObject property = JsonParser.parseReader(skinReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String texture = property.get("value").getAsString();
        String signature = property.get("signature").getAsString();

        return new NPCSkin(texture, signature);
    }

    public static NPCSkin downloadSKin(Player player) {
        ServerPlayer p = ((CraftPlayer) player).getHandle();
        GameProfile profile = p.getGameProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.value();
        String signature = property.signature();

        return new NPCSkin(texture, signature);
    }

}
