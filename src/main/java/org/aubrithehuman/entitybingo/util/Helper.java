package org.aubrithehuman.entitybingo.util;

import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class Helper {

    /**
     * sorts a map from the highest value to the lowest value
     * @param data
     * @return
     */
    public static HashMap<String, Integer> sortData(Map<String, Integer> data) {

        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list
                = new LinkedList<Map.Entry<String, Integer> >(
                data.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> (- i1.getValue().compareTo(i2.getValue())));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
                = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static String color(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String chatLabel() {
        return color("&2[&aEntityBingo&2]&r ");
    }
}
