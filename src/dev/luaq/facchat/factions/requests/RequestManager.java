package dev.luaq.facchat.factions.requests;

import dev.luaq.facchat.FacPlugin;
import dev.luaq.facchat.factions.player.FactionPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;

public class RequestManager {
    @Getter private final int taskId;

    private final Map<Request, Long> requests = new HashMap<>();

    public RequestManager() {
        // start a task to clear all expired requests
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(FacPlugin.getInstance(), new RequestTask(this), 20L, 20L);
    }

    public void addToQueue(Request request) {
        long now = System.currentTimeMillis();

        // place the request into the queue
        requests.put(request, now);
    }

    public Request getRequest(UUID uuid) {
        return requests.keySet().stream()
                .filter(request -> request.getPlayer().getUuid().equals(uuid))
                .findAny().orElse(null);
    }

    public void removeRequest(UUID uuid) {
        // iterator that will be used to find the player
        Iterator<Map.Entry<Request, Long>> iterator = requests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Request, Long> entry = iterator.next();
            // check for the matching UUID
            if (!entry.getKey().getPlayer().getUuid().equals(uuid)) {
                continue;
            }

            // remove and stop looping
            iterator.remove();
            break;
        }
    }

    public boolean hasRequest(UUID uuid) {
        return requests.entrySet().stream()
                .anyMatch(req -> req.getKey().getPlayer().getUuid().equals(uuid));
    }

    public List<FactionPlayer> clearExpired() {
        // expired factions player list
        List<FactionPlayer> expired = new ArrayList<>();

        // get an iterator for the current requests
        Iterator<Map.Entry<Request, Long>> iterator = requests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Request, Long> entry = iterator.next();

            // wait 60 seconds
            if (System.currentTimeMillis() - entry.getValue() < 30L * 1000L) {
                continue;
            }

            // get rid of it and add to the expired list
            iterator.remove();
            expired.add(entry.getKey().getPlayer());
        }

        // send the expired requests
        return expired;
    }
}
