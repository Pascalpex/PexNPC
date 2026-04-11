package de.pascalpex.pexnpc.npc;

import net.minecraft.world.entity.Pose;

public enum NPCPose {
    STANDING("STANDING", Pose.STANDING),
    CROUCHING("CROUCHING", Pose.CROUCHING),
    SLEEPING("SLEEPING", Pose.SLEEPING),
    SWIMMING("SWIMMING", Pose.SWIMMING);

    private final String name;
    private final Pose mcPose;

    NPCPose(String name, Pose mcPose) {
        this.name = name;
        this.mcPose = mcPose;
    }

    public String getName() {
        return name;
    }

    public Pose getMcPose() {
        return mcPose;
    }

    public static NPCPose fromId(int id) {
        for (NPCPose pose : values()) {
            if (pose.getMcPose().id() == id) {
                return pose;
            }
        }
        return STANDING;
    }

}
