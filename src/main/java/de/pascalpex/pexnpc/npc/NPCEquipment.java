package de.pascalpex.pexnpc.npc;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCEquipment {

    private final Map<NPCItemSlot, ItemStack> equipment;
    public static final ItemStack EMPTY_STACK = new ItemStack(Material.AIR);

    public NPCEquipment() {
        equipment = new HashMap<>();

        for (NPCItemSlot slot : NPCItemSlot.values()) {
            equipment.put(slot, EMPTY_STACK);
        }
    }

    public NPCEquipment(ItemStack handItem, ItemStack offhandItem, ItemStack helmetItem, ItemStack chestplateItem, ItemStack leggingsItem, ItemStack bootsItem) {
        equipment = new HashMap<>();

        equipment.put(NPCItemSlot.HAND, handItem);
        equipment.put(NPCItemSlot.OFFHAND, offhandItem);
        equipment.put(NPCItemSlot.HELMET, helmetItem);
        equipment.put(NPCItemSlot.CHESTPLATE, chestplateItem);
        equipment.put(NPCItemSlot.LEGGINGS, leggingsItem);
        equipment.put(NPCItemSlot.BOOTS, bootsItem);
    }

    public List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> getAsMCList() {
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipmentList = new ArrayList<>();
        for (Map.Entry<NPCItemSlot, ItemStack> item : equipment.entrySet()) {
            equipmentList.add(new Pair<>(item.getKey().getMcSlot(), CraftItemStack.asNMSCopy(item.getValue())));
        }
        return equipmentList;
    }

    public void updateItem(NPCItemSlot slot, ItemStack item) {
        equipment.put(slot, item);
    }

    public ItemStack getItem(NPCItemSlot slot) {
        return equipment.get(slot);
    }
}
