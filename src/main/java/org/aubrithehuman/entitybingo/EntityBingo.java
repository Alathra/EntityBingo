package org.aubrithehuman.entitybingo;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.text.html.parser.Entity;

public final class EntityBingo extends JavaPlugin implements Listener {

    static EntityBingo instance;


    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        DataManager.init();

        this.getLogger().info("EntityBingo loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EntityBingo getInstance() {
        return instance;
    }
}
