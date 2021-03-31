package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.util.FactionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FacAdmin implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // TODO: 2021-03-31 god fucking dammit, look at onTabComplete, this is gonna suck LOL
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            return null;
        }

        // return the base parameters
        if (args.length == 1 && args[0].isEmpty()) {
            return Arrays.asList("add", "mod", "remove");
        }

        if (args.length == 2 && args[1].isEmpty()) {
            // get the first argument and suggest new ones
            switch (args[0].toLowerCase()) {
                case "remove":
                case "mod":
                    return FactionUtils.getFactionAbbrs();

                default:
                    break;
            }
        }

        // basic arguments for modification of faction
        // /fa mod <fac> <one_of_these>
        if (args.length == 3 && args[0].equalsIgnoreCase("mod")) {
            return Arrays.asList("leader", "max", "name", "addmember", "removemember");
        }

        // returning proper player list
        if (args.length == 4) {
            switch (args[2].toLowerCase()) {
                case "addmember":
                    return null; // will be made a playerlist

                case "removemember":
                    String facName = args[1];
                    Faction faction = FactionManager.getManager().getFaction(facName);

                    if (faction == null) {
                        break; // will end up returning empty list
                    }

                    return FactionUtils.getMemberNames(faction);

                default:
                    break;
            }
        }

        return Collections.emptyList();
    }
}
