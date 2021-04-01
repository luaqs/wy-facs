package dev.luaq.facchat.listener;

import dev.luaq.facchat.factions.FactionManager;

public class SavingTask implements Runnable {
    @Override
    public void run() {
        // save the players
        FactionManager.getManager().savePlayers();
    }
}
