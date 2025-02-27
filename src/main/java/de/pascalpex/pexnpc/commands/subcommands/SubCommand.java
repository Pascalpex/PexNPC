package de.pascalpex.pexnpc.commands.subcommands;

import de.pascalpex.pexnpc.util.MessageHandler;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    private final String label;
    private final String permission;

    public SubCommand(String label, String permission) {
        this.label = label;
        this.permission = permission;
    }

    public void invoke(Player player, String[] args) {
        if(player.hasPermission(permission)) {
            execute(player, args);
        } else {
            player.sendMessage(MessageHandler.errorMessage("Dafür hast du keine Rechte"));
        }
    }

    public String getLabel() {
        return label;
    }

    abstract void execute(Player player, String[] args);
}
