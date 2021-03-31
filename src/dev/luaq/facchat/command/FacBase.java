package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.player.FactionPlayer;
import dev.luaq.facchat.factions.player.PlayerSettings;
import dev.luaq.facchat.factions.requests.Request;
import dev.luaq.facchat.factions.requests.RequestManager;
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
        RequestManager requestManager = manager.getRequestManager();

        // send the usage if they provided no arguments
        if (args.length == 0) {
            CommandUtils.sendUsage(player, command);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                acceptIntoFaction(args, manager, player, faction, requestManager);
                break;

            case "join":
                joinFaction(command, args, manager, player, faction, requestManager);
                break;

            case "leave":
                quitFaction(manager, player, faction);
                break;

            case "chat":
                FactionPlayer facPlayer = manager.getFactionPlayer(player.getUniqueId());
                PlayerSettings settings = facPlayer.getSettings();

                // toggle it
                boolean seeFactionChat = !settings.isSeeFactionChat();
                settings.setSeeFactionChat(seeFactionChat);

                player.sendMessage(LangUtils.langf("faction.chat.toggle", seeFactionChat ? "on" : "off"));

                break;

            case "info":
                sendInfo(player, faction);
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

    private void acceptIntoFaction(String[] args, FactionManager manager, Player player, Faction faction, RequestManager requestManager) {
        if (faction == null || !faction.isLeader(player.getUniqueId())) {
            player.sendMessage(LangUtils.langf("permission.leader"));
            return;
        }

        FactionPlayer target;
        if (args.length < 2 || (target = manager.getFactionPlayer(args[1])) == null || !requestManager.hasRequest(target.getUuid())) {
            player.sendMessage(LangUtils.langf("error.noplayer"));
            return;
        }

        Request request = requestManager.getRequest(target.getUuid());

        // accept the request
        request.accept(requestManager);
        faction.broadcastLang("faction.join.joined", player.getName(), target.getName());
    }

    private void kickPlayer(String[] args, FactionManager manager, Player player, Faction faction) {
        if (faction == null || !faction.isLeader(player.getUniqueId())) {
            player.sendMessage(LangUtils.langf("permission.leader"));
            return;
        }

        FactionPlayer target;
        if (args.length < 2 || (target = FactionManager.getManager().getFactionPlayer(args[1])) == null) {
            player.sendMessage(LangUtils.langf("error.noplayer"));
            return;
        }

        // make sure the player cannot kick themselves
        if (target.getUuid().equals(player.getUniqueId())) {
            player.sendMessage(LangUtils.langf("faction.error.kickself"));
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

    private void joinFaction(Command command, String[] args, FactionManager manager, Player player, Faction faction, RequestManager requestManager) {
        if (faction != null) {
            player.sendMessage(LangUtils.langf("faction.error.nojoin"));
            return;
        }

        if (args.length < 2) {
            CommandUtils.sendUsage(player, command);
            return;
        }

        // if they have a request already
        if (requestManager.hasRequest(player.getUniqueId())) {
            player.sendMessage(LangUtils.langf("error.outgoing"));
            return;
        }

        Faction requested = manager.getFaction(args[1]);
        if (requested == null) {
            player.sendMessage(LangUtils.langf("faction.error.noexisting", args[1]));
            return;
        }

        // check if the leader is online
        Player leader = manager.getFactionPlayer(requested.getLeader()).getOnlinePlayer();
        if (leader == null) {
            player.sendMessage(LangUtils.langf("faction.error.leaderoff"));
            return;
        }

        // request to join the faction
        requested.requestJoin(player);

        // let the leader and player know
        leader.sendMessage(LangUtils.langf("faction.join.request", player.getName()));
        player.sendMessage(LangUtils.langf("faction.join.requested", requested.getName()));
    }

    private void sendInfo(Player player, Faction faction) {
        // cannot see info for a faction you aren't in
        if (faction == null) {
            player.sendMessage(LangUtils.langf("faction.error.nofaction"));
            return;
        }

        List<FactionPlayer> members = faction.getMembers();
        // create the memberList, the faction members separated bt commas
        String memberList = members.stream().map(member -> {
            boolean isOnline = member.getOnlinePlayer() == null;
            // send the name back with green for online, red for offline
            return String.format("%s%s", isOnline ? "&a" : "&c", member.getName());
        }).collect(Collectors.joining("&7, "));

        // send the info message
        player.sendMessage(LangUtils.langf("faction.info", faction.getName(), faction.getAbbr(), members.size(), faction.getMaxMembers(), memberList));
    }

    private void quitFaction(FactionManager manager, Player player, Faction faction) {
        if (faction == null) {
            player.sendMessage(LangUtils.langf("faction.error.nofaction"));
            return;
        }

        if (faction.isLeader(player.getUniqueId())) {
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
        boolean isLeader = fac != null && fac.isLeader(player.getUniqueId());

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
                tips.add("accept");

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
