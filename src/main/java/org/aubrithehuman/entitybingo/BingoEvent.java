package org.aubrithehuman.entitybingo;

import java.util.HashMap;

public class BingoEvent {

    private final HashMap<String, Integer> entries;

    private final long age;

    public BingoEvent() {
        this.entries = new HashMap<>();
        age = System.currentTimeMillis();
    }

    public HashMap<String, Integer> getEntries() {
        return entries;
    }

    /**
     * return true if player has not yet made a guess, else return false
     *
     * @param player player
     * @param value value
     * @return previous guess
     */
    public boolean addEntry(String player, int value) {
        if(entries.containsKey(player)) {
            entries.put(player, value);
            return true;
        } else {
            entries.put(player, value);
            return false;
        }
    }

    public int totalGuesses() {
        return this.entries.size();
    }

    /**
     * Return a player based on the entry, if none match then return null;
     *
     * @param i value
     * @return player
     */
    public String getByEntry(int i) {
        for(String s : entries.keySet()) {
            if (entries.get(s) == i) {
                return s;
            }
        }
        return null;
    }

    public long getAge() {
        return age;
    }
}
