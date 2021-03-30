package dev.luaq.facchat;

import dev.luaq.facchat.command.FacAdmin;
import dev.luaq.facchat.command.FacCommunication;
import dev.luaq.facchat.command.FacBase;
import dev.luaq.facchat.command.ReloadConfig;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.listener.ConnectionHandler;
import dev.luaq.facchat.util.CommandUtils;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FacPlugin extends JavaPlugin {
    @Getter private static FacPlugin instance;
    @Getter private Chat chat;

    @Override
    public void onEnable() {
        instance = this;

        if (!startVault()) {
            return; // couldn't start Vault
        }

        loadConfig();

        regCommands();
        regEvents();
    }

    @Override
    public void onDisable() {
        // save the player data
        FactionManager.getManager().savePlayers();
    }

    private boolean startVault() {
        PluginManager manager = Bukkit.getPluginManager();

        // can't continue without vault
        if (manager.getPlugin("Vault") == null) {
            getLogger().severe("Cannot continue without Vault dependency.");
            manager.disablePlugin(this);
            return false; // if the vault dependency cannot be met
        }

        regChat();
        return true;
    }

    private void loadConfig() {
        // setup the config and load it
        getConfig().options().copyDefaults(true);
        saveConfig();

        FactionManager manager = FactionManager.getManager();

        manager.loadPlayers();
        manager.reloadFactions();
    }

    private void regChat() {
        RegisteredServiceProvider<Chat> provider = getServer().getServicesManager().getRegistration(Chat.class);
        // get the provider or null
        chat = provider != null ? provider.getProvider() : null;
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
