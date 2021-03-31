package dev.luaq.facchat.factions;

import dev.luaq.facchat.factions.player.FactionPlayer;
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
        for (FactionPlayer member : onlineMembers()) {
            if (!member.getSettings().isSeeFactionChat()) {
                continue; // they don't wanna see faction chat
            }

            member.getOnlinePlayer().sendMessage(LangUtils.langf("faction.broadcast.chat", name, player.getName(), message));
        }
    }

    public void broadcastLang(String lang, Object... format) {
        onlineMembers().forEach(player -> player.getOnlinePlayer().sendMessage(LangUtils.langf(lang, format)));
    }

    public void broadcast(String message, Object... format) {
        onlineMembers().forEach(player -> player.getOnlinePlayer().sendMessage(LangUtils.colorf(message, format)));
    }

    public boolean isLeader(UUID uuid) {
        return getLeader().equals(uuid);
    }

    public boolean hasMember(UUID uuid) {
        return getMembers().stream()
                .anyMatch(player -> player.getUuid().equals(uuid));
    }

    public List<FactionPlayer> onlineMembers() {
        return getMembers().stream()
                .filter(member -> member.getPlayer().isOnline()).collect(Collectors.toList());
    }

    public List<FactionPlayer> getMembers() {
        return FactionManager.getManager().getPlayers().stream()
                .filter(player -> player.getFactionName().equals(abbr))
                .collect(Collectors.toList());
    }
}
