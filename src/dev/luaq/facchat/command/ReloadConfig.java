package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.util.LangUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // reload the config
        FactionManager manager = FactionManager.getManager();
        manager.reloadFactions();
//        manager.loadPlayers();

        sender.sendMessage(LangUtils.color("&aReloaded the factions & players."));

        return true;
    }
}
