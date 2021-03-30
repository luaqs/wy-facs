package dev.luaq.facchat;

import dev.luaq.facchat.command.FacAdmin;
import dev.luaq.facchat.command.FacBase;
import dev.luaq.facchat.command.FacCommunication;
import dev.luaq.facchat.command.ReloadConfig;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.listener.ConnectionHandler;
import dev.luaq.facchat.listener.PrefixHandler;
import dev.luaq.facchat.util.CommandUtils;
import dev.luaq.facchat.util.LangUtils;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FacPlugin extends JavaPlugin {
    @Getter private static FacPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        regPlaceholder();
        regCommands();
        regEvents();
    }

    private void regPlaceholder() {
        // make sure PlaceholderAPI is present
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Missing PlaceholderAPI, the prefixing will not be able to show up.");
            return;
        }

        // register the handler
        PrefixHandler handler = new PrefixHandler();
        handler.register();
    }

    @Override
    public void onDisable() {
        // save the player data
        FactionManager.getManager().savePlayers();
    }

    private void loadConfig() {
        // setup the config and load it
        getConfig().options().copyDefaults(true);
        saveConfig();

        FactionManager manager = FactionManager.getManager();

        manager.loadPlayers();
        manager.reloadFactions();

        // set the config section for the LangUtils
        LangUtils.setLangSect(getConfig().getConfigurationSection("lang"));
    }

    private void regCommands() {
        CommandUtils.registerCommand("faction", new FacBase());
        CommandUtils.registerCommand("factionadmin", new FacAdmin());
        CommandUtils.registerCommand("factionchat", new FacCommunication());
        CommandUtils.registerCommand("factionreload", new ReloadConfig());
    }

    private void regEvents() {
        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new ConnectionHandler(), this);
    }
}
