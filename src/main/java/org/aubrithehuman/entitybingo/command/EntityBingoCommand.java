package org.aubrithehuman.entitybingo.command;

import org.aubrithehuman.entitybingo.DataManager;
import org.aubrithehuman.entitybingo.EntityBingo;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.aubrithehuman.entitybingo.util.Helper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EntityBingoCommand implements CommandExecutor, TabCompleter {

    private final List<String> adminOptions = List.of(new String[]{
            "scoreboard",
            "guesses",
            "reloadscoreboard",
            "purge",
            "reset"
    });

    private final List<String> options = List.of(new String[]{
            "scoreboard",
            "guesses"
    });


    public EntityBingoCommand(EntityBingo entityBingo) {
        entityBingo.getCommand("eb").setExecutor(this);
        entityBingo.getCommand("entb").setExecutor(this);
        entityBingo.getCommand("entitybingo").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        final Player p = (Player) sender;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("scoreboard")) {
                //check if we can do anything
                if (ChatListener.scoreboard == null) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cError loading scoreboard."));
                    return true;
                }
                if (ChatListener.scoreboard.isEmpty()) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cError loading scoreboard."));
                    return true;
                }

                int max = ChatListener.scoreboard.size();
                double pages = Math.ceil((double) max / 8.0D);

                //parse a page number
                int page = 1;
                try {
                    page = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
                    page = Math.max(1, page);
                    page = Math.min((int) pages, page);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cPage number not a value."));
                }

                int startIndex = (page - 1) * 8;

                //print
                p.sendMessage(Helper.chatLabel() + Helper.color("Entity Bingo Scoreboard:"));
                p.sendMessage(Helper.chatLabel() + Helper.color("-------------[" + page + "/" + (int) pages + "]-------------"));

                int i = 0;
                for (String s : ChatListener.scoreboard.keySet()) {
                    //if were past page 1 this will kick into effect
                    if (i < startIndex) {
                        i++;
                        continue;
                    }
                    //format and push to player
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    p.sendMessage(Helper.chatLabel() + Helper.color("&7" + (i + 1) + ". " + name + ", " + ChatListener.scoreboard.get(s)));
                    i++;
                    //page limit
                    if (i > 7 + startIndex) break;
                }


                return true;
            } else if (args[0].equalsIgnoreCase("guesses")) {

                if (EntityBingo.getCurrentEvent() == null) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("No games have occurred recently."));
                }

                //print off all guesses
                p.sendMessage(Helper.chatLabel() + Helper.color("List of all guesses in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event:"));
                p.sendMessage(Helper.chatLabel() + Helper.color("----------------------------------"));

                for (String s : EntityBingo.getCurrentEvent().getEntries().keySet()) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    p.sendMessage(Helper.chatLabel() + Helper.color("&7 - " + name + ": " + EntityBingo.getCurrentEvent().getEntries().get(s)));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("purge")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                if (EntityBingo.getCurrentEvent() == null) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cCleared all entries in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event. This won't effect the scoreboard."));
                    Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEntries cleared by an admin!"));
                    EntityBingo.getInstance().getLogger().info(Helper.color("&cEntries cleared by an admin!"));
                }
                //clear entry pool
                EntityBingo.getCurrentEvent().getEntries().clear();
                p.sendMessage(Helper.chatLabel() + Helper.color("&cCleared all entries in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event. This won't effect the scoreboard."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEntries cleared by an admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEntries cleared by an admin!"));
                return true;
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear event
                EntityBingo.setCurrentEvent(null);
                p.sendMessage(Helper.chatLabel() + Helper.color("&cEnded current bingo event."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEvent forcefully ended by and admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEvent forcefully ended by and admin!"));
                return true;
            } else if (args[0].equalsIgnoreCase("reloadscoreboard")) {
                if (!p.hasPermission("EntityBingo.admin")) {
                    p.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                HashMap<String, Object> raw = DataManager.getData("scoreboard.yml");
                try {
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
                } catch (ClassCastException ex) {
                    EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is scoreboard.yml broken?");
                }
                return true;
            } else {
                p.sendMessage(Helper.chatLabel() + Helper.color("&cError. Subcommand not found"));
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        final Player p = (Player) sender;
        if (args.length >= 1) {
            if (args.length >= 2) {
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
