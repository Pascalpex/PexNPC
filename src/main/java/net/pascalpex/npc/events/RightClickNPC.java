package net.pascalpex.npc.events;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RightClickNPC extends Event implements Cancellable {

    private final Player player;
    private final ServerPlayer npc;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public RightClickNPC(Player player, ServerPlayer npc) {
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
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
