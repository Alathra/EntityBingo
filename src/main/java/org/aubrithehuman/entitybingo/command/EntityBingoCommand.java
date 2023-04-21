package org.aubrithehuman.entitybingo.command;

import org.aubrithehuman.entitybingo.EntityBingo;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class EntityBingoCommand implements CommandExecutor, TabCompleter {

    private final List<String> adminOptions = List.of(new String[] {
            "scoreboard",
            "guesses",
            "purge",
            "reset"
    });

    private final List<String> options = List.of(new String[] {
            "scoreboard",
            "guesses"
    });

    public EntityBingoCommand(EntityBingo entityBingo) {
        entityBingo.getCommand("eb").setExecutor((CommandExecutor) this);
        entityBingo.getCommand("entb").setExecutor((CommandExecutor) this);
        entityBingo.getCommand("entitybingo").setExecutor((CommandExecutor) this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player p = (Player) sender;
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("scoreboard")) {

                return true;
            } else if(args[0].equalsIgnoreCase("guesses")) {
                //print off all guesses
                p.sendMessage(ChatListener.chatLabel() + ChatListener.color("List of all guesses in the " + (EntityBingo.getCurrrentEvent().isDone() ? "previous" : "current") + " event:"));
                p.sendMessage(ChatListener.chatLabel() + ChatListener.color("----------------------------------"));

                for (String s : EntityBingo.getCurrrentEvent().getEntries().keySet()) {
                    p.sendMessage(ChatListener.chatLabel() + ChatListener.color("&7 - " + s + ": " + EntityBingo.getCurrrentEvent().getEntries().get(s)));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("purge")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(ChatListener.chatLabel() + ChatListener.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear entry pool
                EntityBingo.getCurrrentEvent().getEntries().clear();
                p.sendMessage(ChatListener.chatLabel() + ChatListener.color("&cCleared all entries in the current event."));
                Bukkit.broadcastMessage(ChatListener.chatLabel() + ChatListener.color("&cEntries cleared by an admin!"));
                EntityBingo.getInstance().getLogger().info(ChatListener.color("&cEntries cleared by an admin!"));
                return true;
            } else if(args[0].equalsIgnoreCase("reset")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(ChatListener.chatLabel() + ChatListener.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear event
                EntityBingo.setCurrrentEvent(null);
                p.sendMessage(ChatListener.chatLabel() + ChatListener.color("&cEnded current bingo event."));
                Bukkit.broadcastMessage(ChatListener.chatLabel() + ChatListener.color("&cEvent forcefully ended by and admin!"));
                EntityBingo.getInstance().getLogger().info(ChatListener.color("&cEvent forcefully ended by and admin!"));
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final Player p = (Player) sender;
        if(args.length >= 1) {
            if(args.length >= 2) {
                return null;
            } else {
                if (p.hasPermission("EntityBingo.admin")) {
                    return adminOptions;
                }
                return options;
            }
        }
        return null;
    }
}
