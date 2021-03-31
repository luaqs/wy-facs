package dev.luaq.facchat.factions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.luaq.facchat.FacPlugin;
import dev.luaq.facchat.factions.player.FactionPlayer;
import dev.luaq.facchat.factions.requests.RequestManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FactionManager {
    private static FactionManager manager;

    @Getter private final List<Faction> factions;

    @Getter private final Set<FactionPlayer> players;
    @Getter private final File playerFile;

    @Getter private final RequestManager requestManager;

    private final Gson gson = new Gson();

    private FactionManager() {
        FacPlugin plugin = FacPlugin.getInstance();

        this.requestManager = new RequestManager();

        this.players = new HashSet<>();
        this.factions = new ArrayList<>();

        this.playerFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("playerfile"));
    }

    public static FactionManager getManager() {
        if (manager == null) {
            manager = new FactionManager();
        }

        return manager;
    }

    public Faction getPlayerFaction(UUID uuid) {
        FactionPlayer player = getFactionPlayer(uuid);
        return player != null ? getFaction(player.getFactionName()) : null;
    }

    public FactionPlayer getFactionPlayer(String name) {
        return players.stream()
                .filter(player -> player.getIgn().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public FactionPlayer getFactionPlayer(UUID uuid) {
        return players.stream()
                .filter(player -> player.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }

    public Faction getFaction(final String name) {
        return factions.stream()
                .filter(fac -> fac.getAbbr().equals(name) || fac.getName().equals(name))
                .findAny().orElse(null);
    }

    public void clearPlayerFaction(UUID uuid) {
        FactionPlayer player = getFactionPlayer(uuid);
        if (player == null) {
            return;
        }

        // clear their faction
        player.setFactionName("");
    }

    public void loadPlayers() {
        if (!playerFile.exists()) {
            savePlayers();
            return;
        }

        try {
            FileReader reader = new FileReader(playerFile);

            // get the player list from the file
            List<FactionPlayer> playersList = gson.fromJson(reader, TypeToken.getParameterized(List.class, FactionPlayer.class).getType());

            // clear the players stored in memory
            // and replace it with the parsed json
            players.clear();
            players.addAll(playersList);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayers() {
        // convert
        String jsonConverted = gson.toJson(players);

        // write the json file with the list of players
        try {
            Files.write(playerFile.toPath(), jsonConverted.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadFactions() {
        FacPlugin plugin = FacPlugin.getInstance();
        plugin.reloadConfig(); // reload before checking

        FileConfiguration config = plugin.getConfig();
        factions.clear();

        // load all of the factions
        for (String key : config.getConfigurationSection("factions").getKeys(false)) {
            // saving all factions to a list
            factions.add(new Faction(config, key));
        }
    }
}
