package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.files.Config;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TpSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        NPC npc = context.getArgument("npc", PlaceableNPC.class).getNpc();

        if (Config.getSpectatorModeOnTeleport() && executor instanceof Player player) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        if (executor != null) {
            executor.teleport(npc.getLocation());
            executor.teleportAsync(npc.getLocation()).thenAccept(success -> {
                if (success) {
                    if (executor instanceof Player player) {
                        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }

                    sender.sendMessage(MessageHandler.prefixedMini("Teleported to the NPC with the ID <gold>" + npc.getId()));
                } else {
                    sender.sendMessage(MessageHandler.errorMessage("Asynchronous teleportation failed"));
                }
            });
        }

        return SINGLE_SUCCESS;
    }
}
