package dev.luaq.facchat.command;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.player.FactionPlayer;
import dev.luaq.facchat.factions.player.PlayerSettings;
import dev.luaq.facchat.util.LangUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class FacCommunication implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return false; // stupid admins
        }

        FactionManager manager = FactionManager.getManager();
        Player player = (Player) sender;
        Faction fac = manager.getPlayerFaction(player.getUniqueId());

        if (fac == null) {
            player.sendMessage(LangUtils.langf("faction.error.nofaction"));
            return true;
        }

        if (args.length == 0) {
            FactionPlayer facPlayer = manager.getFactionPlayer(player.getUniqueId());
            PlayerSettings settings = facPlayer.getSettings();

            boolean primary = !settings.isFactionChatPrimary();
            settings.setFactionChatPrimary(primary);

            player.sendMessage(LangUtils.langf("faction.chat.focustoggle", primary ? "FACTION" : "PUBLIC"));

            return true;
        }

        // send chat message as the player
        fac.chat(player, String.join(" ", args));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
