package dev.luaq.facchat.listener;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.player.FactionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatHandler implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        FactionManager manager = FactionManager.getManager();
        FactionPlayer facPlayer = manager.getFactionPlayer(player.getUniqueId());

        Faction fac = manager.getPlayerFaction(player.getUniqueId());

        // we only care if the faction chat is primary
        if (!facPlayer.getSettings().isFactionChatPrimary() || fac == null) {
            return;
        }

        // redirect their chat to faction chat
        fac.chat(player, event.getMessage());
        event.setCancelled(true);
    }
}
