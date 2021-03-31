package dev.luaq.facchat.command;

import dev.luaq.facchat.FacPlugin;
import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.player.FactionPlayer;
import dev.luaq.facchat.util.CommandUtils;
import dev.luaq.facchat.util.FactionUtils;
import dev.luaq.facchat.util.LangUtils;
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
        // no need to check if sender is a Player, console can use these

        // no arguments, send the usage
        if (args.length == 0) {
            CommandUtils.sendUsage(sender, command);
            return true;
        }

        // get the faction here, because why not
        Faction faction = null;
        if (args.length >= 1) {
            faction = FactionManager.getManager().getFaction(args[1]);
        }

        // go through the sub commands
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "add":
            case "remove":
                // woah, a hardcoded message
                sender.sendMessage("Let's not do this, use the config if you want to change existing factions.");
                break;

            case "mod":
                handleMod(sender, command, args, faction);
                break;

            default: // invalid subcommand
                CommandUtils.sendUsage(sender, command);
                break;
        }

        return true;
    }

    private void handleMod(CommandSender sender, Command command, String[] args, Faction faction) {
        if (faction == null) {
            sender.sendMessage(LangUtils.langf("faction.error.noexisting", args.length >= 1 ? args[1] : ""));
            return;
        }

        String sub, value;
        if (args.length < 4) {
            CommandUtils.sendUsage(sender, command);
            return;
        }

        // the subcommand and the value of it
        sub = args[2].toLowerCase();
        value = args[3];

        FactionManager manager = FactionManager.getManager();
        // parse the value as a player if possible
        FactionPlayer player = manager.getFactionPlayer(value);

        switch (sub) {
            case "leader":
                // the new leader of the faction
                faction.setLeader(player.getUuid());
                break;

            case "max":
                int newMax;
                try {
                    newMax = Integer.parseInt(value);
                } catch (NumberFormatException e) {  break; }

                faction.setMaxMembers(newMax);
                break;

            case "name":
                faction.setName(value);
                break;

            case "addmember":
                if (player == null) {
                    break;
                }

                // set the player's faction
                player.setFactionName(faction.getAbbr());
                break;

            case "removemember":
                if (player == null) {
                    break;
                }

                // remove their faction name
                manager.clearPlayerFaction(player.getUuid());

                break;
        }

        sender.sendMessage(LangUtils.colorf("&aRan &6%s &awith resulting value being &6%s&a.", sub, value));

        // saving the faction
        faction.setConfigValues();
        FacPlugin.getInstance().saveConfig();
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
