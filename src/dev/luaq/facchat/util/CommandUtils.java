package dev.luaq.facchat.util;

import dev.luaq.facchat.FacPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandUtils {
    public static void sendUsage(Player player, Command cmd) {
        player.sendMessage(ChatUtils.colorf("&cUsage: %s", cmd.getUsage()));
    }

    public static void registerCommand(String name, CommandExecutor executor) {
        FacPlugin facChat = FacPlugin.getInstance();
        PluginCommand command = facChat.getCommand(name);

        // ignore null command
        if (command == null) {
            return;
        }

        // set the executor properly
        command.setExecutor(executor);

        // ignore when the executor is not
        if (!(executor instanceof TabCompleter)) {
            return;
        }

        // if the executor is also a tabcompleter
        command.setTabCompleter((TabCompleter) executor);
    }
}
