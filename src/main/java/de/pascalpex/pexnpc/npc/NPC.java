package de.pascalpex.pexnpc.npc;

import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.util.SkinDownloader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Represents an NPC that is savable / loadable from the config
 */
public class NPC {

    private long id;
    private Location location;
    private String name;
    private NPCSkin skin;

    private String command;
    private String message;

    private NPCEquipment equipment;

    private double scale;
    private boolean burning;
    private boolean glowing;
    private NPCPose pose;

    public NPC(Location location, String name, String skinName, Player fallbackSkin) {
        this.location = location;
        this.name = name;

        this.id = NPCData.getNextID();

        command = "";
        message = "";
        scale = 1.0;
        burning = false;
        glowing = false;
        pose = NPCPose.STANDING;

        equipment = new NPCEquipment();

        try {
            this.skin = SkinDownloader.downloadSkin(skinName);
        } catch (IOException | URISyntaxException e) {
            this.skin = SkinDownloader.downloadSKin(fallbackSkin);
        }
    }

    public NPC(long id, Location location, String name, NPCSkin skin, String command, String message, NPCEquipment equipment, double scale, boolean burning, boolean glowing, NPCPose pose) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.skin = skin;
        this.command = command;
        this.message = message;
        this.equipment = equipment;
        this.scale = scale;
        this.burning = burning;
        this.glowing = glowing;
        this.pose = pose;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NPCSkin getSkin() {
        return skin;
    }

    public void setSkin(NPCSkin skin) {
        this.skin = skin;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NPCEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(NPCEquipment equipment) {
        this.equipment = equipment;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public NPCPose getPose() {
        return pose;
    }

    public void setPose(NPCPose pose) {
        this.pose = pose;
    }

    public boolean hasItems() {
        boolean isEmpty = equipment.getItem(NPCItemSlot.HAND).getType() == Material.AIR
                && equipment.getItem(NPCItemSlot.OFFHAND).getType() == Material.AIR
                && equipment.getItem(NPCItemSlot.HELMET).getType() == Material.AIR
                && equipment.getItem(NPCItemSlot.CHESTPLATE).getType() == Material.AIR
                && equipment.getItem(NPCItemSlot.LEGGINGS).getType() == Material.AIR
                && equipment.getItem(NPCItemSlot.BOOTS).getType() == Material.AIR;
        return !isEmpty;
    }
}
