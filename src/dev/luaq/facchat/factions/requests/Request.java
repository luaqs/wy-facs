package dev.luaq.facchat.factions.requests;

import dev.luaq.facchat.factions.player.FactionPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Request {
    private final FactionPlayer player;
    private final String requested;

    public void accept(RequestManager manager) {
        // set the player faction and remove from queue
        player.setFactionName(requested);
        manager.removeRequest(player.getUuid());
    }
}
