package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FacCommunication implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return false; // stupid admins
        }

        FactionManager manager = FactionManager.getManager();
        Player player = (Player) sender;
        Faction fac = manager.getPlayerFaction(player.getUniqueId());

        if (fac == null) {
            player.sendMessage(ChatUtils.color("&cYou are not in a faction, stop talking to your imaginary friends."));
            return true;
        }

        if (args.length == 0) {
            // TODO: 2021-03-29 toggle chat
            return true;
        }

        // send chat message as the player
        fac.chat(player, String.join(" ", args));

        return true;
    }
}
