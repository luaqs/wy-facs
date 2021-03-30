package dev.luaq.facchat.listener;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.FactionPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        FactionManager manager = FactionManager.getManager();
        Player player = event.getPlayer();
        FactionPlayer facPlayer = manager.getFactionPlayer(player.getUniqueId());

        if (facPlayer != null) {
            // update their IGN
            facPlayer.setIgn(player.getName());
            return; // they have been initialized
        }

        // create the faction player and add it to the list of them
        facPlayer = new FactionPlayer(player.getUniqueId());
        facPlayer.setIgn(player.getName());

        Faction leadFac = manager.getFactions().stream()
                .filter(fac -> fac.getLeader().equals(player.getUniqueId())).findFirst().orElse(null);

        manager.getPlayers().add(facPlayer);

        if (leadFac == null) {
            return;
        }

        // if they lead a faction, set their faction to be that
        facPlayer.setFactionName(leadFac.getAbbr());
    }
}
