package de.pascalpex.pexnpc.npc;

import net.minecraft.world.entity.EquipmentSlot;

public enum NPCItemSlot {
    HAND("HAND", EquipmentSlot.MAINHAND),
    OFFHAND("OFFHAND", EquipmentSlot.OFFHAND),
    HELMET("HELMET", EquipmentSlot.HEAD),
    CHESTPLATE("CHESTPLATE", EquipmentSlot.CHEST),
    LEGGINGS("LEGGINGS", EquipmentSlot.LEGS),
    BOOTS("BOOTS", EquipmentSlot.FEET);

    private final String name;
    private final EquipmentSlot mcSlot;

    NPCItemSlot(String name, EquipmentSlot mcSlot) {
        this.name = name;
        this.mcSlot = mcSlot;
    }

    public String getName() {
        return name;
    }

    public EquipmentSlot getMcSlot() {
        return mcSlot;
    }
}
