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
//        Pattern pattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(e.getMessage());
        boolean matchFound = e.getMessage().equals("§4[ClearLag] §cWarning Ground items will be removed in §760 §cseconds!");
        boolean matchFound2 = false;
        if(matchFound) {
            Bukkit.getLogger().info("Match found");
        } else {
            Bukkit.getLogger().info("Match not found, checking endpoint");

            String check = "§6[ClearLag] §aRemoved ";
            if(e.getMessage().length() >= 23) {
                String p1 = e.getMessage().substring(0, 23);
                matchFound2 = p1.equals(check);
                if(matchFound2) {
                    Bukkit.getLogger().info("Match found");
                } else {
                    Bukkit.getLogger().info("Match not found");
                }
            }
        }




        //TODO start event
        if(matchFound) {
            if(EntityBingo.getCurrrentEvent() == null) {
                EntityBingo.setCurrrentEvent(new BingoEvent());
                Bukkit.broadcastMessage(chatLabel() + "Entity Bingo has begun! Enter a number in chat to enter!");
                return;
            } else {
                Bukkit.getLogger().info("Event Started too recently, ignoring Bingo attempt!");
                return;
            }
        }

        if(matchFound2) {
            if(EntityBingo.getCurrrentEvent() != null) {
                Bukkit.broadcastMessage(chatLabel() + "Entity Bingo has ended!");

                String str = e.getMessage();
                String numberOnly = str.replaceAll("[^0-9]", "");
                int i = Integer.parseInt(numberOnly);

                List<String> winners = new ArrayList<>();
                for(String s : EntityBingo.getCurrrentEvent().getEntries().keySet()) {
                    if(EntityBingo.getCurrrentEvent().getEntries().get(s) == i) {
                        winners.add(s);
                    }
                }

                if(winners.isEmpty()) {
                    Bukkit.broadcastMessage(chatLabel() + "No one guessed correctly.");
                } else {
                    for (String s : winners) {
                        Bukkit.broadcastMessage(chatLabel() + s + " guessed correctly!");
                        //TODO save result
                    }
                }

                EntityBingo.setCurrrentEvent(null);
            }
        }
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        //Entry detection
        Bukkit.getLogger().info(chatLabel() + "comparing: " + e.getMessage());
        if (EntityBingo.getCurrrentEvent() != null) {
            String str = e.getMessage();
            String numberOnly = str.replaceAll("[^0-9]", "");
            int guess = Integer.parseInt(numberOnly);

            if (EntityBingo.getCurrrentEvent().getEntries().containsKey(e.getPlayer().getName())) {
                e.getPlayer().sendMessage("You have already entered entity bingo, your old guess has been overridden.");
            }
            e.getPlayer().sendMessage("You have entered with the guess " + guess + ".");
            EntityBingo.getCurrrentEvent().getEntries().put(e.getPlayer().getName(), guess);
        }
    }

    public static String color(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String chatLabel() {
        return color("&6[&4EntityBingo&6]&r ");
    }

}
