package org.aubrithehuman.entitybingo;

import org.aubrithehuman.entitybingo.command.EntityBingoCommand;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.aubrithehuman.entitybingo.listeners.EntityClearEventListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.parser.Entity;

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
        this.getServer().getPluginManager().registerEvents(new EntityClearEventListener(), this);

        DataManager.init();

        this.getLogger().info("EntityBingo loaded.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
