package dev.luaq.facchat.util;

import dev.luaq.facchat.factions.Faction;
import dev.luaq.facchat.factions.FactionManager;
import dev.luaq.facchat.factions.player.FactionPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class FactionUtils {
    public static List<String> getMemberNames(Faction faction) {
        return faction.getMembers().stream()
                .map(FactionPlayer::getName).collect(Collectors.toList());
    }

    public static List<String> getFactionAbbrs() {
        return FactionManager.getManager().getFactions().stream()
                .map(Faction::getAbbr).collect(Collectors.toList());
    }
}
