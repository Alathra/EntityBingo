package org.aubrithehuman.entitybingo.command;

import org.aubrithehuman.entitybingo.DataManager;
import org.aubrithehuman.entitybingo.EntityBingo;
import org.aubrithehuman.entitybingo.listeners.ChatListener;
import org.aubrithehuman.entitybingo.util.Helper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
            "reset",
            "addscore",
            "setscore"
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
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("scoreboard")) {
                //check if we can do anything
                if (ChatListener.scoreboard == null) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cError loading scoreboard."));
                    return true;
                }
                if (ChatListener.scoreboard.isEmpty()) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cScoreboard empty! Try again later!."));
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
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cPage number not a value."));
                }

                int startIndex = (page - 1) * 8;

                //print
                sender.sendMessage(Helper.chatLabel() + Helper.color("Entity Bingo Scoreboard:"));
                sender.sendMessage(Helper.chatLabel() + Helper.color("-------------[" + page + "/" + (int) pages + "]-------------"));

                int i = 0;
                for (String s : ChatListener.scoreboard.keySet()) {
                    //if were past page 1 this will kick into effect
                    if (i < startIndex) {
                        i++;
                        continue;
                    }
                    //format and push to player
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&7" + (i + 1) + ". " + name + ", " + ChatListener.scoreboard.get(s)));
                    i++;
                    //page limit
                    if (i > 7 + startIndex) break;
                }


                return true;
            } else if (args[0].equalsIgnoreCase("guesses")) {
                if (EntityBingo.getCurrentEvent() == null) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("No games have occurred recently."));
                    return true;
                }

                //print off all guesses
                sender.sendMessage(Helper.chatLabel() + Helper.color("List of all guesses in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event:"));
                sender.sendMessage(Helper.chatLabel() + Helper.color("----------------------------------"));

                for (String s : EntityBingo.getCurrentEvent().getEntries().keySet()) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(s)).getName();
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&7 - " + name + ": " + EntityBingo.getCurrentEvent().getEntries().get(s)));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("purge")) {
                if (!sender.hasPermission("EntityBingo.admin")) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                if (EntityBingo.getCurrentEvent() == null) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cCleared all entries in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event. This won't effect the scoreboard."));
                    Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEntries cleared by an admin!"));
                    EntityBingo.getInstance().getLogger().info(Helper.color("&cEntries cleared by an admin!"));
                    return true;
                }
                //clear entry pool
                EntityBingo.getCurrentEvent().getEntries().clear();
                sender.sendMessage(Helper.chatLabel() + Helper.color("&cCleared all entries in the " + (EntityBingo.getCurrentEvent().isDone() ? "previous" : "current") + " event. This won't effect the scoreboard."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEntries cleared by an admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEntries cleared by an admin!"));
                return true;
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!sender.hasPermission("EntityBingo.admin")) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
                    return true;
                }

                //clear event
                EntityBingo.setCurrentEvent(null);
                sender.sendMessage(Helper.chatLabel() + Helper.color("&cEnded current bingo event."));
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cEvent forcefully ended by and admin!"));
                EntityBingo.getInstance().getLogger().info(Helper.color("&cEvent forcefully ended by and admin!"));
                return true;
            } else if (args[0].equalsIgnoreCase("reloadscoreboard")) {
                if (!sender.hasPermission("EntityBingo.admin")) {
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cYou do not have permissions to run this command."));
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
                    sender.sendMessage(Helper.chatLabel() + Helper.color("&cReloaded scoreboard."));
                    EntityBingo.getInstance().getLogger().info(Helper.color("&cAdmin reloaded scoreboard!"));
                } catch (ClassCastException ex) {
                    EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is scoreboard.yml broken?");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("addscore")) {
                if(args.length >= 3) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                    HashMap<String, Object> raw = DataManager.getData("scoreboard.yml");
                    try {
                        HashMap<String, Object> data = (HashMap<String, Object>) raw.get("scores");

                        try {
                            int old = (int) data.get(p.getUniqueId().toString());
                            data.put(p.getUniqueId().toString(), old + Integer.parseInt(args[2]));
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(Helper.chatLabel() + "Failed to parse value");
                            return true;
                        }

                        //grab only entries with integer values, should be all, but we need to check anyway
                        Map<String, Integer> filtered = data.entrySet()
                                .stream()
                                .filter(v -> v.getValue() instanceof Integer)
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        v -> (int) v.getValue()));


                        //Save to static scoreboard reference
                        ChatListener.scoreboard = Helper.sortData(filtered);

                        //save scoreboard
                        HashMap<String, Object> out = new HashMap<>();
                        out.put("scores", filtered);
                        DataManager.saveData("scoreboard.yml", out);

                        sender.sendMessage(Helper.chatLabel() + Helper.color("&EB score for player " + args[1] + " added " + args[2]));
                        EntityBingo.getInstance().getLogger().info(Helper.color("&EB score for player " + args[1] + " added " + args[2]));

                        //clear event
                        sender.sendMessage(Helper.chatLabel() + Helper.color("&cReloaded scoreboard."));
                        EntityBingo.getInstance().getLogger().info(Helper.color("&cAdmin reloaded scoreboard!"));
                    } catch (ClassCastException ex) {
                        EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is scoreboard.yml broken?");
                    }

                    reloadScoreboard(sender);
                }
            } else if (args[0].equalsIgnoreCase("setscore")) {
                if(args.length >= 3) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
                    HashMap<String, Object> raw = DataManager.getData("scoreboard.yml");
                    try {
                        HashMap<String, Object> data = (HashMap<String, Object>) raw.get("scores");

                        try {
                            data.put(p.getUniqueId().toString(), Integer.parseInt(args[2]));
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(Helper.chatLabel() + "Failed to parse value");
                            return true;
                        }

                        //grab only entries with integer values, should be all, but we need to check anyway
                        Map<String, Integer> filtered = data.entrySet()
                                .stream()
                                .filter(v -> v.getValue() instanceof Integer)
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        v -> (int) v.getValue()));


                        //Save to static scoreboard reference
                        ChatListener.scoreboard = Helper.sortData(filtered);

                        //save scoreboard
                        HashMap<String, Object> out = new HashMap<>();
                        out.put("scores", filtered);
                        DataManager.saveData("scoreboard.yml", out);

                        sender.sendMessage(Helper.chatLabel() + Helper.color("&cSet EB score for player " + args[1] + " to " + args[2]));
                        EntityBingo.getInstance().getLogger().info(Helper.color("&cSet EB score for player " + args[1] + " to " + args[2]));

                        //clear event
                        sender.sendMessage(Helper.chatLabel() + Helper.color("&cReloaded scoreboard."));
                        EntityBingo.getInstance().getLogger().info(Helper.color("&cAdmin reloaded scoreboard!"));
                    } catch (ClassCastException ex) {
                        EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is scoreboard.yml broken?");
                    }

                    reloadScoreboard(sender);
                }
            }  else {
                sender.sendMessage(Helper.chatLabel() + Helper.color("&cError. Subcommand not found"));
            }
        }
        return true;
    }

    private static void reloadScoreboard(CommandSender sender) {
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
            sender.sendMessage(Helper.chatLabel() + Helper.color("&cReloaded scoreboard."));
            EntityBingo.getInstance().getLogger().info(Helper.color("&cAdmin reloaded scoreboard!"));
        } catch (ClassCastException ex) {
            EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is scoreboard.yml broken?");
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length >= 1) {
            if (args.length >= 2) {
                return null;
            } else {
                if (sender.hasPermission("EntityBingo.admin")) {
                    return adminOptions;
                }
                return options;
            }
        }
        return null;
    }
}
