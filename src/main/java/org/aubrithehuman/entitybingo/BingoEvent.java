package org.aubrithehuman.entitybingo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BingoEvent {

    private final HashMap<String, Integer> entries;

    private boolean done = false;

    private final long age;

    public BingoEvent() {
        this.entries = new HashMap<>();
        age = System.currentTimeMillis();
    }

    public HashMap<String, Integer> getEntries() {
        return entries;
    }

    /**
     * return 0 if this is first guess, 1 if previous guess, 2 if guess already exists.
     *
     * @param player player
     * @param value value
     * @return previous guess
     */
    public int addEntry(String player, int value) {
        if(!entries.containsValue(value)) {
            if(!entries.containsKey(player)) {
                entries.put(player, value);
                return 0;
            }
            entries.put(player, value);
            return 1;
        }
        return 2;

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

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        this.done = true;
    }
}
