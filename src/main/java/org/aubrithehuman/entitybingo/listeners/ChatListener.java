package org.aubrithehuman.entitybingo.listeners;

import org.aubrithehuman.entitybingo.BingoEvent;
import org.aubrithehuman.entitybingo.EntityBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerEvent;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBroadcast(BroadcastMessageEvent e) {
        Bukkit.getLogger().info(chatLabel() + "comparing: " + e.getMessage());
        boolean matchFound = e.getMessage().equals("§4[ClearLag] §cWarning Ground items will be removed in §760 §cseconds!");
        boolean matchFound2 = false;
        if(matchFound) {
            //start the event
            if(EntityBingo.getCurrrentEvent() == null) {
                EntityBingo.setCurrrentEvent(new BingoEvent());
                Bukkit.broadcastMessage(chatLabel() + "Entity Bingo has begun! Type a number to enter!");
                EntityBingo.getInstance().getLogger().info(chatLabel() + "Entity Bingo has begun! Type a number to enter!");
            } else {
                Bukkit.getLogger().info(chatLabel() + "Event Started too recently, ignoring Bingo attempt!");
                EntityBingo.getInstance().getLogger().info(chatLabel() + "Event Started too recently, ignoring Bingo attempt!");
            }
            return;
        } else {
            Bukkit.getLogger().info("Match not found, checking endpoint");

            //check for end broadcast
            String check = "§6[ClearLag] §aRemoved ";
            if(e.getMessage().length() >= 23) {
                String p1 = e.getMessage().substring(0, 23);
                matchFound2 = p1.equals(check);
                if(matchFound2) {
                    Bukkit.getLogger().info("Match found for endpoint");
                    //check if we have an bingo event happening
                    if(EntityBingo.getCurrrentEvent() != null) {
                        Bukkit.broadcastMessage(chatLabel() + "Entity Bingo has ended!");

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
                                EntityBingo.getInstance().getLogger().info(chatLabel() + color("&cFailed to find the result number, cancelling bingo."));
                                EntityBingo.setCurrrentEvent(null);
                                return;
                            }
                        }

                        //look for winners
                        List<String> winners = new ArrayList<>();
                        if(i >= 0) {
                            for(String s : EntityBingo.getCurrrentEvent().getEntries().keySet()) {
                                if(EntityBingo.getCurrrentEvent().getEntries().get(s) == i) {
                                    winners.add(s);
                                }
                            }
                        }

                        //broadcast winners
                        if(winners.isEmpty()) {
                            Bukkit.broadcastMessage(chatLabel() + "No one guessed correctly.");
                            EntityBingo.getInstance().getLogger().info(chatLabel() + "No one guessed correctly.");
                        } else {
                            for (String s : winners) {
                                Bukkit.broadcastMessage(chatLabel() + s + " guessed correctly!");
                                EntityBingo.getInstance().getLogger().info(chatLabel() + s + " guessed correctly!");
                                //TODO save result
                            }
                        }

                        EntityBingo.setCurrrentEvent(null);

                    }
                } else {
                    Bukkit.getLogger().info("Match not found for any part");
                }
            }
        }

        if(matchFound2) {

        }
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        //Entry detection
        Bukkit.getLogger().info(chatLabel() + "comparing: "  + "\"" + e.getMessage()  + "\"");
        if (EntityBingo.getCurrrentEvent() != null) {
            String str = e.getMessage();
            //remove all color codes from this message
            str = ChatColor.stripColor(str);
            Bukkit.getLogger().info(chatLabel() + "purges codes for: " + "\"" + str + "\"");
            //remove all text other than numbers
            str = str.replaceAll("[^0-9.]", "");
            Bukkit.getLogger().info(chatLabel() + "numbers only: " + "\"" + str + "\"");
            if(!str.isEmpty()) {
                int guess;
                //we need to check if the guess exists as an int
                try {
                    guess = Integer.parseInt(str);
                } catch (NumberFormatException exception) {
                    e.getPlayer().sendMessage(chatLabel() + color("&cDetected a number that could not be formatted, make sure not to use decimals."));
                    return;
                }

                if (guess > 0) {
                    //do the entry
                    int result = EntityBingo.getCurrrentEvent().addEntry(e.getPlayer().getName(), guess);
                    //feedback to entrant
                    switch (result) {
                        case 0 -> e.getPlayer().sendMessage(chatLabel() + color("&aYou have entered with the guess " + guess + "."));
                        case 1 -> {
                            e.getPlayer().sendMessage(chatLabel() + color("&eYou have previously entered entity bingo, your old guess has been overridden."));
                            e.getPlayer().sendMessage(chatLabel() + color("&aYou have entered with the guess " + guess + "."));
                        }
                        case 2 -> e.getPlayer().sendMessage(chatLabel() + color("&eSomeone has already made the guess " + guess + ". Enter a different number!"));
                        default -> {
                            e.getPlayer().sendMessage(chatLabel() + color("&cSomething went wrong entering your guess. Try again!"));
                            EntityBingo.getInstance().getLogger().info("Failed to enter guess \"" + guess + "\" by player " + e.getPlayer() + " from message \"" + e.getMessage() + "\"");
                        }
                    }
                }
            }
        }
    }

    public static String color(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String chatLabel() {
        return color("&2[&aEntityBingo&2]&r ");
    }
}
