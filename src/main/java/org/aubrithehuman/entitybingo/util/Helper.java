package org.aubrithehuman.entitybingo.util;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Helper {

    public static HashMap<String, Integer> sortData(HashMap<String, Integer> data) {

        return data.entrySet()
                .stream()
                .sorted((i1, i2)
                        -> (- i1.getValue().compareTo(i2.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static String color(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String chatLabel() {
        return color("&2[&aEntityBingo&2]&r ");
    }
}
