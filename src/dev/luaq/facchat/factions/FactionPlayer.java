package dev.luaq.facchat.factions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class FactionPlayer {
    @Getter @Setter private String factionName;
    @Getter @Setter private String ign;
    @Getter private final UUID uuid;

    public FactionPlayer(UUID uuid) {
        this(uuid.toString());
    }

    public FactionPlayer(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public String getName() {
        return getPlayer().getName();
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}
