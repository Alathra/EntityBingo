package org.aubrithehuman.entitybingo.command;

import org.aubrithehuman.entitybingo.DataManager;
import org.aubrithehuman.entitybingo.EntityBingo;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.aubrithehuman.entitybingo.util.Helper;
import org.aubrithehuman.entitybingo.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class EntityBingoCommand implements CommandExecutor, TabCompleter {

    private final List<String> adminOptions = List.of(new String[] {
            "scoreboard",
            "guesses",
            "reloadscoreboard",
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
                //check if we can do anything
                if(ChatListener.scoreboard == null) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cError loading scoreboard."));
                    return true;
                }
                if(ChatListener.scoreboard.isEmpty()) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cError loading scoreboard."));
                    return true;
                }

                int max = ChatListener.scoreboard.size();
                double pages = Math.ceil((double) max / 8.0D);

                //parse a page number
                int page = 1;
                try {
                    page = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
                    page = Math.min(1, page);
                    page = Math.max((int) pages, page);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cPage number not a value."));
                }

                int startIndex = (page - 1) * 10;

                //print
                p.sendMessage(Helper.chatLabel() + Helper.color("Entity Bingo Scoreboard:"));
                p.sendMessage(Helper.chatLabel() + Helper.color("-------------[" + page + "/" + (int) pages + "]-------------"));

                int i = 0;
                for (String s : ChatListener.scoreboard.keySet()) {
                    if (i < startIndex) continue;
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    p.sendMessage(Helper.chatLabel() + Helper.color("&7 " + (i + 1) + ". " + name + ", " + ChatListener.scoreboard.get(s)));
                    i++;
                    //page limit
                    if(i > 9 + startIndex) break;
                }


                return true;
            } else if(args[0].equalsIgnoreCase("guesses")) {
                //print off all guesses
                p.sendMessage(Helper.chatLabel() + Helper.color("List of all guesses in the " + (EntityBingo.getCurrrentEvent().isDone() ? "previous" : "current") + " event:"));
                p.sendMessage(Helper.chatLabel() + Helper.color("----------------------------------"));

                for (String s : EntityBingo.getCurrrentEvent().getEntries().keySet()) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    p.sendMessage(Helper.chatLabel() + Helper.color("&7 - " + name + ": " + EntityBingo.getCurrrentEvent().getEntries().get(s)));
                }
                return true;
            } else if(args[0].equalsIgnoreCase("purge")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear entry pool
                EntityBingo.getCurrrentEvent().getEntries().clear();
                p.sendMessage(Helper.chatLabel() + Helper.color("&cCleared all entries in the current event."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEntries cleared by an admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEntries cleared by an admin!"));
                return true;
            } else if(args[0].equalsIgnoreCase("reset")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear event
                EntityBingo.setCurrrentEvent(null);
                p.sendMessage(Helper.chatLabel() + Helper.color("&cEnded current bingo event."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEvent forcefully ended by and admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEvent forcefully ended by and admin!"));
                return true;
            } else if(args[0].equalsIgnoreCase("reloadscoreboard")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

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

                //clear event
                p.sendMessage(Helper.chatLabel() + Helper.color("&cReloaded scoreboard."));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cAdmin reloaded scoreboard!"));
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
