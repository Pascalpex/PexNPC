package de.pascalpex.pexnpc.events;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClickNPCEvent extends Event {

    private final Player player;
    private final ServerPlayer npc;
    private static final HandlerList HANDLERS = new HandlerList();

    public ClickNPCEvent(Player player, ServerPlayer npc) {
        this.player = player;
        this.npc = npc;
    }

    public Player getPlayer() {
        return player;
    }

    public ServerPlayer getNpc() {
        return npc;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
