package dev.luaq.facchat.util;

import dev.luaq.facchat.FacPlugin;
import org.bukkit.command.*;

public class CommandUtils {
    public static void sendUsage(CommandSender sender, Command cmd) {
        sender.sendMessage(LangUtils.langf("error.usage", cmd.getUsage()));
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
