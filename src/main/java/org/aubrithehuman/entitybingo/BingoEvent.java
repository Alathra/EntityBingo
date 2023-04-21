package org.aubrithehuman.entitybingo;

import java.util.HashMap;

public class BingoEvent {

    private final HashMap<String, Integer> entries;

    private boolean done = false;

    public BingoEvent() {
        this.entries = new HashMap<>();
    }

    public HashMap<String, Integer> getEntries() {
        return entries;
    }

    /**
     * return 0 if this is first guess, 1 if previous guess, 2 if guess already exists.
     *
     * @param player player
     * @param value  value
     * @return previous guess
     */
    public int addEntry(String player, int value) {
        if (!entries.containsValue(value)) {
            if (!entries.containsKey(player)) {
                entries.put(player, value);
                return 0;
            }
            entries.put(player, value);
            return 1;
        }
        return 2;

    }

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        this.done = true;
    }
}
