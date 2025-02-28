package de.pascalpex.pexnpc.npc;

import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.util.SkinDownloader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Represents a NPC that is savable / loadable from the config
 */
public class NPC {

    public static ItemStack nullStack = new ItemStack(Material.AIR);

    private long id;
    private Location location;
    private String name;
    private NPCSkin skin;

    private String command;
    private String message;

    private ItemStack handItem;
    private ItemStack offhandItem;
    private ItemStack helmetItem;
    private ItemStack chestplateItem;
    private ItemStack leggingsItem;
    private ItemStack bootsItem;

    public NPC(Location location, String name, String skinName, Player fallbackSkin) {
        this.location = location;
        this.name = name;

        this.id = NPCData.getNextID();

        command = "";
        message = "";

        handItem = nullStack;
        offhandItem = nullStack;
        helmetItem = nullStack;
        chestplateItem = nullStack;
        leggingsItem = nullStack;
        bootsItem = nullStack;

        try {
            this.skin = SkinDownloader.downloadSkin(skinName);
        } catch (IOException | URISyntaxException e) {
            this.skin = SkinDownloader.downloadSKin(fallbackSkin);
        }
    }

    public NPC(long id, Location location, String name, NPCSkin skin, String command, String message, ItemStack handItem, ItemStack offhandItem, ItemStack helmetItem, ItemStack chestplateItem, ItemStack leggingsItem, ItemStack bootsItem) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.skin = skin;
        this.command = command;
        this.message = message;
        this.handItem = handItem;
        this.offhandItem = offhandItem;
        this.helmetItem = helmetItem;
        this.chestplateItem = chestplateItem;
        this.leggingsItem = leggingsItem;
        this.bootsItem = bootsItem;
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

    public ItemStack getHandItem() {
        return handItem;
    }

    public void setHandItem(ItemStack handItem) {
        this.handItem = handItem;
    }

    public ItemStack getOffhandItem() {
        return offhandItem;
    }

    public void setOffhandItem(ItemStack offhandItem) {
        this.offhandItem = offhandItem;
    }

    public ItemStack getHelmetItem() {
        return helmetItem;
    }

    public void setHelmetItem(ItemStack helmetItem) {
        this.helmetItem = helmetItem;
    }

    public ItemStack getChestplateItem() {
        return chestplateItem;
    }

    public void setChestplateItem(ItemStack chestplateItem) {
        this.chestplateItem = chestplateItem;
    }

    public ItemStack getLeggingsItem() {
        return leggingsItem;
    }

    public void setLeggingsItem(ItemStack leggingsItem) {
        this.leggingsItem = leggingsItem;
    }

    public ItemStack getBootsItem() {
        return bootsItem;
    }

    public void setBootsItem(ItemStack bootsItem) {
        this.bootsItem = bootsItem;
    }
}
