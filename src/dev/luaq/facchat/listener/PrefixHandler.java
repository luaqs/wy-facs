package dev.luaq.facchat.listener;

import dev.luaq.facchat.FacPlugin;
import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.util.LangUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PrefixHandler extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "facchat";
    }

    @Override
    public String getAuthor() {
        return "luaq";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params == null || !params.equals("prefix")) {
            return super.onPlaceholderRequest(player, params);
        }

        FacPlugin plugin = FacPlugin.getInstance();
        String prefix = plugin.getConfig().getString("prefixstyle");

        FactionManager manager = FactionManager.getManager();
        Faction faction = manager.getPlayerFaction(player.getUniqueId());

        // if the player has no faction
        // then return an empty string
        if (faction == null) {
            return "";
        }

        // set the prefix, providing two different options for the string
        prefix = LangUtils.colorf(prefix, faction.getAbbr().toUpperCase(), faction.getName());

        // return the formatted prefix for the specified player
        return prefix;
    }
}
