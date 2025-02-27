package de.pascalpex.pexnpc.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.pascalpex.pexnpc.files.Config;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.UUID;

/**
 * Represents a NPC that is currently placed in the world
 */
public class PlaceableNPC {

    private NPC npc;
    private ServerPlayer serverPlayer;
    private String actualName;
    private String suffix;

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public PlaceableNPC(NPC npc) {
        this.npc = npc;

        String name = npc.getName();
        actualName = name.substring(0, Math.min(name.length(), 16));
        suffix = name.length() > 16 ? name.substring(16) : "";
        if (actualName.endsWith("§")) {
            suffix = "§" + suffix;
        }
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), actualName);
        NPCSkin skin = npc.getSkin();
        gameProfile.getProperties().put("textures", new Property("textures", skin.texture(), skin.signature()));

        Location loc = npc.getLocation();
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        ServerPlayer serverPlayer = new ServerPlayer(server, world, gameProfile, ClientInformation.createDefault());
        serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());
        serverPlayer.setYRot(loc.getYaw());
        serverPlayer.setXRot(loc.getPitch());

        SynchedEntityData watcher = serverPlayer.getEntityData();
        int skinMode = Config.getSkinMode();
        switch (skinMode) {
            case 2:
                watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 126);
                break;
            case 3:
                watcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
                break;
            default:
                break;
        }

        this.serverPlayer = serverPlayer;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public void setServerPlayer(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    public String getActualName() {
        return actualName;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
