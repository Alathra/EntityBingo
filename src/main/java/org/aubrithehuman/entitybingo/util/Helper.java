package org.aubrithehuman.entitybingo.util;

import org.bukkit.ChatColor;

import java.util.*;

public class Helper {

    /**
     * sorts a map from the highest value to the lowest value
     *
     * @param data map of uuid and values
     * @return sorted map
     */
    public static HashMap<String, Integer> sortData(Map<String, Integer> data) {

        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list
                = new LinkedList<>(
                data.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> (-i1.getValue().compareTo(i2.getValue())));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
                = new LinkedHashMap<>();
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
