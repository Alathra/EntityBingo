package org.aubrithehuman.entitybingo.listeners;

import org.aubrithehuman.entitybingo.BingoEvent;
import org.aubrithehuman.entitybingo.DataManager;
import org.aubrithehuman.entitybingo.EntityBingo;
import org.aubrithehuman.entitybingo.util.Helper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.BroadcastMessageEvent;

import java.io.UncheckedIOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ChatListener implements Listener {


    public static HashMap<String, Integer> scoreboard;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBroadcast(BroadcastMessageEvent e) {
        boolean matchFound = e.getMessage().equals(EntityBingo.getInstance().getConfig().getString("startMessage"));
        boolean matchFound2;
        if(matchFound) {
            //start the event, only if previous one is marked as done
            if(EntityBingo.getCurrentEvent() == null) {
                EntityBingo.setCurrentEvent(new BingoEvent());
                Bukkit.broadcastMessage(Helper.chatLabel() + "Entity Bingo has begun! Type a number to enter!");
                EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + "Entity Bingo has begun! Type a number to enter!");
            } else {
                if(EntityBingo.getCurrentEvent().isDone()) {
                    EntityBingo.setCurrentEvent(new BingoEvent());
                    Bukkit.broadcastMessage(Helper.chatLabel() + "Entity Bingo has begun! Type a number to enter!");
                    EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + "Entity Bingo has begun! Type a number to enter!");
                } else {
                    Bukkit.broadcastMessage(Helper.chatLabel() + "Event Started too recently, ignoring Bingo attempt!");
                    EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + "Event Started too recently, ignoring Bingo attempt!");
                }
            }
        } else {
            //check for end broadcast
            String check = EntityBingo.getInstance().getConfig().getString("endMessage");
            if(e.getMessage().length() >= 23) {
                String p1 = e.getMessage().substring(0, 23);
                matchFound2 = p1.equals(check);
                if(matchFound2) {
                    //check if we have a bingo event happening
                    if(EntityBingo.getCurrentEvent() != null) {
                        if(!EntityBingo.getCurrentEvent().isDone()) {
                            Bukkit.broadcastMessage(Helper.chatLabel() + "Entity Bingo has ended!");

                            //extract the result number
                            String str = e.getMessage();
                            str = ChatColor.stripColor(str);
                            String numberOnly = str.replaceAll("[^0-9]", "");
                            int i = -1;
                            if(!str.isEmpty()) {
                                //we need to check if the guess exists as an int
                                try {
                                    i = Integer.parseInt(numberOnly);
                                } catch (NumberFormatException exception) {
                                    EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + Helper.color("&cFailed to find the result number, cancelling bingo."));
                                    EntityBingo.getCurrentEvent().setDone();
                                    return;
                                }
                            }

                            //look for winners
                            List<String> winners = new ArrayList<>();
                            if(i >= 0) {
                                for(String s : EntityBingo.getCurrentEvent().getEntries().keySet()) {
                                    if(EntityBingo.getCurrentEvent().getEntries().get(s) == i) {
                                        winners.add(s);
                                    }
                                }
                            }

                            //broadcast winners
                            if(winners.isEmpty()) {
                                //no winners??????
                                Bukkit.broadcastMessage(Helper.chatLabel() + "No one guessed correctly.");
                                EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + "No one guessed correctly.");
                            } else {
                                Map<String, Object> raw = DataManager.getData("config.yml");
                                if(raw.containsKey("scores")) {
                                    try {
                                        HashMap<String, Integer> data = (HashMap<String, Integer>) raw.get("scores");
                                        for (String s : winners) {
                                            //broadcast winner
                                            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(s));
                                            Bukkit.broadcastMessage(Helper.chatLabel() + player.getName() + " guessed correctly!");
                                            EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + player.getName() + " guessed correctly!");

                                            //get player uuid and save their wins
                                            UUID uuid = UUID.fromString(s);
                                            if (data != null) {
                                                if (data.containsKey(uuid.toString())) {
                                                    data.put(uuid.toString(), (int) data.get(uuid.toString()) + 1);
                                                } else {
                                                    data.put(uuid.toString(), 1);
                                                }
                                            } else {
                                                EntityBingo.getInstance().getLogger().log(Level.WARNING, "Data failed to load, plugins/EntityBingo/config.yml may be broken.");
                                            }
                                        }

                                        if (data != null) {
                                            //save scoreboard
                                            HashMap<String, Object> out = new HashMap<>();
                                            out.put("scores", data);
                                            DataManager.saveData("config.yml", out);

                                            //grab only entries with integer values, should be all, but we need to check anyway
                                            Map<String, Integer> filtered = data.entrySet()
                                                    .stream()
                                                    .filter(v -> v.getValue() instanceof Integer)
                                                    .collect(Collectors.toMap(
                                                            Map.Entry::getKey,
                                                            v -> (int) v.getValue()));

                                            //Save to static scoreboard reference
                                            scoreboard = Helper.sortData(filtered);
                                        }
                                    } catch (ClassCastException ex) {
                                        EntityBingo.getInstance().getLogger().log(Level.WARNING, "Failed to load scores, is config.yml broken?");
                                        ex.printStackTrace();
                                    }
                                } else {
                                    HashMap<String, Object> data = new HashMap<>();
                                    for (String s : winners) {
                                        //broadcast winner
                                        Bukkit.broadcastMessage(Helper.chatLabel() + s + " guessed correctly!");
                                        EntityBingo.getInstance().getLogger().info(Helper.chatLabel() + s + " guessed correctly!");

                                        //get player uuid and save their wins
                                        UUID uuid = UUID.fromString(s);
                                        if(data.containsKey(uuid.toString())) {
                                            data.put(uuid.toString(), (int) data.get(uuid.toString()) + 1);
                                        } else {
                                            data.put(uuid.toString(), 1);
                                        }
                                    }
                                    //save scoreboard
                                    HashMap<String, Object> out = new HashMap<>();
                                    out.put("scores", data);
                                    DataManager.saveData("config.yml", out);
                                }


                            }

                            EntityBingo.getCurrentEvent().setDone();

                        }
                    }
                }
            }
        }
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        //Entry detection
        if (EntityBingo.getCurrentEvent() != null) {
            if(EntityBingo.getCurrentEvent().isDone()) {
                return;
            }

            //Force end event if it's too old.
            int maxEventAge = EntityBingo.getInstance().getConfig().getInt("maxEventAge");
            if(System.currentTimeMillis() - EntityBingo.getCurrentEvent().getAge() > (maxEventAge * 1000L)) {
                EntityBingo.getCurrentEvent().setDone();
                Bukkit.broadcastMessage(Helper.chatLabel() + Helper.color("&cForcefully ended bingo event as it has been more than " + maxEventAge + " seconds."));
                return;
            }

            String str = e.getMessage();
            //remove all color codes from this message
            str = ChatColor.stripColor(str);
            //remove all text other than numbers
            str = str.replaceAll("[^0-9.]", "");
            if(!str.isEmpty()) {
                int guess = - 1;
                //we need to check if the guess exists as an int
                try {
                    guess = Integer.parseInt(str);
                } catch (NumberFormatException exception) {
                    e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&cDetected a number that could not be formatted, make sure not to use decimals or seperators."));
                    return;
                }

                if (guess >= 0) {
                    //do the entry
                    int result = EntityBingo.getCurrentEvent().addEntry(e.getPlayer().getUniqueId().toString(), guess);
                    //feedback to entrant
                    switch (result) {
                        case 0 -> e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&aYou have entered with the guess " + guess + "."));
                        case 1 -> {
                            e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&eYou have previously entered entity bingo, your old guess has been overridden."));
                            e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&aYou have entered with the guess " + guess + "."));
                        }
                        case 2 -> e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&eSomeone has already made the guess " + guess + ". Enter a different number!"));
                        default -> {
                            e.getPlayer().sendMessage(Helper.chatLabel() + Helper.color("&cSomething went wrong entering your guess. Try again!"));
                            EntityBingo.getInstance().getLogger().info("Failed to enter guess \"" + guess + "\" by player " + e.getPlayer() + " from message \"" + e.getMessage() + "\"");
                        }
                    }
                }
            }
        }
    }

}
