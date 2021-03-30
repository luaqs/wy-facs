package dev.luaq.facchat.factions;

import dev.luaq.facchat.util.LangUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Faction {
    @Getter private final String abbr;

    @Getter @Setter private String name;
    @Getter @Setter private int maxMembers;
    @Setter @Getter private UUID leader;

    public Faction(ConfigurationSection config, String key) {
        // abbreviated term
        this.abbr = key;

        // section for the faction in the config file
        ConfigurationSection section = config.getConfigurationSection(String.format("factions.%s", key));

        // set all fields
        this.name = section.getString("name");
        this.maxMembers = section.getInt("max");
        this.leader = UUID.fromString(section.getString("leader"));
    }

    public void requestJoin(Player player) {
        // TODO: 2021-03-29 send message to leader
    }

    @Deprecated
    public void removePlayer(UUID player) {
        FactionManager.getManager().clearPlayerFaction(player);
    }

    public void chat(Player player, String message) {
        broadcastLang("faction.broadcast.chat", name, player.getName(), message);
    }

    public void broadcastLang(String lang, Object... format) {
        List<FactionPlayer> members = getMembers();
        for (FactionPlayer member : members) {
            Player player = member.getPlayer().getPlayer();
            // ignore players that could not be fetched
            if (player == null) {
                continue;
            }

            player.sendMessage(LangUtils.langf(lang, format));
        }
    }

    public void broadcast(String message, Object... format) {
        List<FactionPlayer> members = getMembers();
        for (FactionPlayer member : members) {
            Player player = member.getPlayer().getPlayer();
            // ignore players that could not be fetched
            if (player == null) {
                continue;
            }

            player.sendMessage(LangUtils.colorf(message, format));
        }
    }

    public boolean isLeader(Player player) {
        return getLeader().equals(player.getUniqueId());
    }

    public boolean hasMember(UUID uuid) {
        return getMembers().stream()
                .anyMatch(player -> player.getUuid().equals(uuid));
    }

    public List<FactionPlayer> getMembers() {
        return FactionManager.getManager().getPlayers().stream()
                .filter(player -> player.getFactionName().equals(abbr))
                .collect(Collectors.toList());
    }
}
