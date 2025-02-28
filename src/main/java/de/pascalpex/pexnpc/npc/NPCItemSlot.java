package de.pascalpex.pexnpc.npc;

public enum NPCItemSlot {
    HAND("HAND"),
    OFFHAND("OFFHAND"),
    HELMET("HELMET"),
    CHESTPLATE("CHESTPLATE"),
    LEGGINGS("LEGGINGS"),
    BOOTS("BOOTS");

    private final String name;

    NPCItemSlot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
