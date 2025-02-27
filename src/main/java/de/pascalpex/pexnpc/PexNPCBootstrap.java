package de.pascalpex.pexnpc;

import de.pascalpex.pexnpc.commands.PexNPCCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class PexNPCBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
           commands.registrar().register("pexnpc", new PexNPCCommand());
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new PexNPC();
    }
}
