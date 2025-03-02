package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.files.NPCData;
import de.pascalpex.pexnpc.npc.NPCSender;
import de.pascalpex.pexnpc.npc.NPCSkin;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import de.pascalpex.pexnpc.util.SkinDownloader;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URISyntaxException;

public class SkinSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Entity executor = context.getSource().getExecutor();
        PlaceableNPC placeableNPC = context.getArgument("npc", PlaceableNPC.class);
        String skinName = StringArgumentType.getString(context, "skin");

        if (!(executor instanceof Player target)) {
            sender.sendMessage(MessageHandler.errorMessage("The command executor must be a player"));
            return SINGLE_SUCCESS;
        }

        NPCSkin newSkin;
        try {
            newSkin = SkinDownloader.downloadSkin(skinName);
        } catch (IOException | URISyntaxException e) {
            newSkin = SkinDownloader.downloadSKin(target);
        }

        PexNPC.getPlacedNpcs().remove(placeableNPC);
        NPCSender.removeNPC(placeableNPC);

        placeableNPC.getNpc().setSkin(newSkin);
        PlaceableNPC newPlaceableNPC = new PlaceableNPC(placeableNPC.getNpc());
        PexNPC.getPlacedNpcs().add(newPlaceableNPC);
        NPCSender.sendNpcToPlayers(newPlaceableNPC);
        NPCData.saveNpc(newPlaceableNPC.getNpc());

        sender.sendMessage(MessageHandler.prefixedMini("The NPC with the ID <gold>" + placeableNPC.getNpc().getId() + " <aqua>now has a new skin"));

        return SINGLE_SUCCESS;
    }
}
