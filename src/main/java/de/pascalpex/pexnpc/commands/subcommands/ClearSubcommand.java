package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.NPCEquipment;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class ClearSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);

        NPCSender.removeNPC(placeableNPC);

        NPC npc = placeableNPC.getNpc();
        npc.setEquipment(new NPCEquipment());
        npc.setCommand("");
        npc.setMessage("");

        NPCSender.sendNpcToPlayers(placeableNPC);
        NPCData.saveNpc(placeableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>no longer has any commands, messages or items"));

        return SINGLE_SUCCESS;
    }
}
