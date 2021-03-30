package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.FactionPlayer;
import dev.luaq.facchat.util.ChatUtils;
import dev.luaq.facchat.util.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FacBase implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        FactionManager manager = FactionManager.getManager();

        Player player = (Player) sender;
        Faction faction = manager.getPlayerFaction(player.getUniqueId());

        // send the usage if they provided no arguments
        if (args.length == 0) {
            CommandUtils.sendUsage(player, command);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                joinFaction(command, args, manager, player, faction);
                break;

            case "leave":
                quitFaction(manager, player, faction);
                break;

            case "chat":
            case "info":
                player.sendMessage(ChatUtils.color("&cI don't want to implement it, but if you find this then @ me with a screenshot."));
                // TODO: 2021-03-29 dsiahdu
                break;

            case "kick":
                kickPlayer(args, manager, player, faction);
                break;

            default: // send usage if no arguments are met
                CommandUtils.sendUsage(player, command);
                break;
        }

        return true;
    }

    private void kickPlayer(String[] args, FactionManager manager, Player player, Faction faction) {
        if (faction == null || !faction.isLeader(player)) {
            player.sendMessage(ChatUtils.color("&cYou have to be the leader of a faction to use this."));
            return;
        }

        FactionPlayer target;
        if (args.length < 2 || (target = FactionManager.getManager().getFactionPlayer(args[1])) == null) {
            player.sendMessage(ChatUtils.color("&cSpecify a valid player to kick."));
            return;
        }

        if (!faction.hasMember(target.getUuid())) {
            player.sendMessage(ChatUtils.colorf("&cYou cannot kick %s, they are not in your faction.", target.getName()));
            return;
        }

        // remove their faction
        manager.clearPlayerFaction(target.getUuid());
        faction.broadcast("&f%s &ahas kicked &f%s &afrom the faction.", player.getName(), target.getName());

        OfflinePlayer offlineTarget = target.getPlayer();

        // send a message to them if they are online
        if (offlineTarget.isOnline()) {
            offlineTarget.getPlayer().sendMessage(ChatUtils.color("&cYou have been kicked from the faction."));
        }
    }

    private void joinFaction(Command command, String[] args, FactionManager manager, Player player, Faction faction) {
        if (faction != null) {
            player.sendMessage(ChatUtils.color("&cYou cannot join a faction, you're in one."));
            return;
        }

        if (args.length < 2) {
            CommandUtils.sendUsage(player, command);
            return;
        }

        Faction requested = manager.getFaction(args[1]);
        if (requested == null) {
            player.sendMessage(ChatUtils.colorf("&cCould not find the faction with abbreviation: %s", args[1]));
            return;
        }

        // request to join the faction
        requested.requestJoin(player);
        player.sendMessage(ChatUtils.colorf("&aSuccessfully requested to join the '%s' faction, wait for the leader to accept.", requested.getName()));
    }

    private void quitFaction(FactionManager manager, Player player, Faction faction) {
        if (faction == null) {
            player.sendMessage(ChatUtils.color("&cYou aren't in a faction right now."));
            return;
        }

        if (faction.isLeader(player)) {
            player.sendMessage(ChatUtils.color("&cYou cannot leave your own faction, you're the leader. Talk to the admins if you'd like a change."));
            return;
        }

        manager.clearPlayerFaction(player.getUniqueId());
        player.sendMessage(ChatUtils.color("&aYou have successfully left the faction, farewell."));
        faction.broadcast("&f%s &ahas quit the faction.", player.getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        Player player = (Player) sender;
        Faction fac = FactionManager.getManager().getPlayerFaction(player.getUniqueId());
        boolean isLeader = fac != null && fac.isLeader(player);

        if (args.length <= 1 && args[0].isEmpty()) {
            List<String> tips = new ArrayList<>();
            tips.add("info");
            tips.add("chat");

            // if the user is the leader of a faction, show them the kick command
            if (fac == null) {
                tips.add("join");
                return tips;
            }

            if (isLeader) {
                tips.add("kick");
                return tips;
            }

            tips.add("leave");

            return tips;
        }

        if (args.length < 2 || !args[1].isEmpty()) {
            return Collections.emptyList();
        }

        // if they're a leader of a faction then offer the list of members to kick
        if (args[0].equalsIgnoreCase("kick") && isLeader) {
            return fac.getMembers().stream()
                    .filter(facPlayer -> !facPlayer.getUuid().equals(player.getUniqueId()))
                    .map(FactionPlayer::getName)
                    .collect(Collectors.toList());
        }

        if (args[0].equalsIgnoreCase("join")) {
            // show suggestions for all available factions
            return FactionManager.getManager().getFactions().stream()
                    .map(Faction::getAbbr).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
