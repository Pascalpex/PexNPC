package de.pascalpex.pexnpc.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.pascalpex.pexnpc.PexNPC;
import de.pascalpex.pexnpc.npc.NPC;
import de.pascalpex.pexnpc.npc.PlaceableNPC;
import de.pascalpex.pexnpc.util.MessageHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListSubcommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        sender.sendMessage(MessageHandler.basicMessage("All loaded NPCs:"));
        for (PlaceableNPC placeableNPC : PexNPC.getPlacedNpcs()) {
            NPC npc = placeableNPC.getNpc();
            String name = npc.getName();
            Location loc = npc.getLocation();
            Component component = MessageHandler.parse("<aqua>- ID:" + npc.getId() + " <gold>Name: <white>").append(MessageHandler.parseSection(name)).append(MessageHandler.parse(" <red>World: " + (loc.getWorld() == null ? "INVALID" : loc.getWorld().getName()) + " <green>X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ()));

            if (sender instanceof Player) {
                component = component
                        .hoverEvent(HoverEvent.showText(MessageHandler.parse("<aqua>Click to teleport\nID: <gold>" + npc.getId())))
                        .clickEvent(ClickEvent.runCommand("/pexnpc tp " + npc.getId()));
            }
            sender.sendMessage(component);
        }

        return SINGLE_SUCCESS;
    }
}
