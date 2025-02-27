package de.pascalpex.pexnpc.commands.subcommands;

public enum SubCommands {
    HELP(new HelpSubcommand("help", "")),
    LIST(new ListSubcommand("list", "pexnpc.list"));

    private final SubCommand subCommand;

    SubCommands(SubCommand subCommand) {
        this.subCommand = subCommand;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }
}
