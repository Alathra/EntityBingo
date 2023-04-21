package org.aubrithehuman.entitybingo;

import org.aubrithehuman.entitybingo.command.EntityBingoCommand;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.aubrithehuman.entitybingo.util.Helper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class EntityBingo extends JavaPlugin {

    static EntityBingo instance;

    static BingoEvent currrentEvent;

    @Override
    public void onEnable() {
        instance = this;


        EntityBingoCommand c = new EntityBingoCommand(this);
        getCommand("eb").setTabCompleter(c);
        getCommand("entb").setTabCompleter(c);
        getCommand("entitybingo").setTabCompleter(c);

        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);

        DataManager.init();

        //Reload scoreboard
        HashMap<String, Object> raw = DataManager.getData("scoreboard.yml");
        HashMap<String, Object> data = (HashMap<String, Object>) raw.get("scores");
        //grab only entries with integer values, should be all, but we need to check anyway
        Map<String, Integer> filtered = data.entrySet()
                .stream()
                .filter(v -> v.getValue() instanceof Integer)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> (int) v.getValue()));

        //Save to static scoreboard reference
        ChatListener.scoreboard = Helper.sortData(filtered);

        this.getLogger().info("EntityBingo loaded.");
    }

    @Override
    public void onDisable() {
        if(currrentEvent != null) {
            if(!currrentEvent.isDone()) {
                Bukkit.broadcastMessage(Helper.chatLabel() + "Server Shutting down, clearing Bingo Event without results!");
                currrentEvent.setDone();
                currrentEvent = null;
            }
        }
    }

    public static EntityBingo getInstance() {
        return instance;
    }

    public static BingoEvent getCurrrentEvent() {
        return currrentEvent;
    }

    public static void setCurrrentEvent(BingoEvent currrentEvent) {
        EntityBingo.currrentEvent = currrentEvent;
    }
}
