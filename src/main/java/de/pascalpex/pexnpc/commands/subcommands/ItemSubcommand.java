package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPCEquipment;
import de.pascalpex.pexnpc.npc.NPCItemSlot;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ItemSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);
        NPCItemSlot slot = context.getArgument("slot", NPCItemSlot.class);

        if (!(executor instanceof Player target)) {
            sender.sendMessage(MessageHandler.errorMessage("The command executor must be a player"));
            return SINGLE_SUCCESS;
        }

        NPCSender.removeNPC(placeableNPC);

        NPCEquipment equipment = placeableNPC.getNpc().getEquipment();
        equipment.updateItem(slot, target.getInventory().getItemInMainHand());
        NPCSender.sendNpcToPlayers(placeableNPC);
        NPCData.saveNpc(placeableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>now has a new item"));

        return SINGLE_SUCCESS;
    }
}
