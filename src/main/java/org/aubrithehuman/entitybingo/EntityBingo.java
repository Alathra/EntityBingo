package org.aubrithehuman.entitybingo;

import org.aubrithehuman.entitybingo.command.EntityBingoCommand;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

        this.getLogger().info("EntityBingo loaded.");
    }

    @Override
    public void onDisable() {
        if(currrentEvent != null) {
            Bukkit.broadcastMessage(ChatListener.chatLabel() + "Server Shutting down, clearing Bingo Event without results!");
            currrentEvent = null;
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
