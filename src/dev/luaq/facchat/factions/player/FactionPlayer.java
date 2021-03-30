package dev.luaq.facchat.factions.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionPlayer {
    @Getter @Setter private String factionName;
    @Getter @Setter private String ign;
    @Getter private final UUID uuid;

    @Getter private final PlayerSettings settings = new PlayerSettings();

    public FactionPlayer(UUID uuid) {
        this(uuid.toString());
    }

    public FactionPlayer(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public String getName() {
        return getPlayer().getName();
    }

    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}
