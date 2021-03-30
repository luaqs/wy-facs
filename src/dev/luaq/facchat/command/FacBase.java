package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.FactionPlayer;
import dev.luaq.facchat.util.LangUtils;
import dev.luaq.facchat.util.CommandUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
                player.sendMessage(LangUtils.langf("error.lazy"));
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
            player.sendMessage(LangUtils.langf("permission.leader"));
            return;
        }

        FactionPlayer target;
        if (args.length < 2 || (target = FactionManager.getManager().getFactionPlayer(args[1])) == null) {
            player.sendMessage(LangUtils.langf("error.noplayer"));
            return;
        }

        if (!faction.hasMember(target.getUuid())) {
            player.sendMessage(LangUtils.langf("permission.cantkick", target.getName()));
            return;
        }

        // remove their faction
        manager.clearPlayerFaction(target.getUuid());
        faction.broadcastLang("faction.broadcast.playerkicked", player.getName(), target.getName());

        OfflinePlayer offlineTarget = target.getPlayer();

        // send a message to them if they are online
        if (offlineTarget.isOnline()) {
            offlineTarget.getPlayer().sendMessage(LangUtils.langf("faction.kicked"));
        }
    }

    private void joinFaction(Command command, String[] args, FactionManager manager, Player player, Faction faction) {
        if (faction != null) {
            player.sendMessage(LangUtils.langf("faction.error.nojoin"));
            return;
        }

        if (args.length < 2) {
            CommandUtils.sendUsage(player, command);
            return;
        }

        Faction requested = manager.getFaction(args[1]);
        if (requested == null) {
            player.sendMessage(LangUtils.langf("faction.error.noexisting", args[1]));
            return;
        }

        // TODO: 2021-03-30 send request

        // request to join the faction
        requested.requestJoin(player);
        player.sendMessage(LangUtils.langf("faction.join.requested", requested.getName()));
    }

    private void quitFaction(FactionManager manager, Player player, Faction faction) {
        if (faction == null) {
            player.sendMessage(LangUtils.langf("faction.error.nofaction"));
            return;
        }

        if (faction.isLeader(player)) {
            player.sendMessage(LangUtils.langf("faction.error.cantleave"));
            return;
        }

        manager.clearPlayerFaction(player.getUniqueId());
        player.sendMessage(LangUtils.langf("faction.left", faction.getName()));
        faction.broadcastLang("faction.broadcast.playerquit", player.getName());
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
