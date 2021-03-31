package dev.luaq.facchat.factions.requests;

import dev.luaq.facchat.factions.player.FactionPlayer;
import dev.luaq.facchat.util.LangUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public class RequestTask implements Runnable {
    private final RequestManager manager;

    @Override
    public void run() {
        List<FactionPlayer> expired = manager.clearExpired();

        for (FactionPlayer player : expired) {
            Player onlinePlayer = player.getOnlinePlayer();
            // they rage quit or something idk
            if (onlinePlayer == null) {
                continue;
            }

            // let them know it's expired
            onlinePlayer.sendMessage(LangUtils.langf("faction.join.expired"));
        }
    }
}
