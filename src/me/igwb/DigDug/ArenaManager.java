package me.igwb.DigDug;

import java.util.ArrayList;

public class ArenaManager {

    private ArrayList<Arena> arenas;

    /**
     * Creates a new instance of ArenaManager.
     */
    public ArenaManager() {
        arenas = new ArrayList<Arena>();
    }

    /**
     * Tries to add a new Arena by name. This will automatically create a new instance of the Arena class.
     * @param name The name for the Arena
     * @return true if the arena was added, false if an arena by that name already exists
     */
    public boolean addArena(String name) {
        for (Arena ar : getArenas()) {
            if (ar.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        arenas.add(new Arena(name));
        return true;
    }

    /**
     * Tries to remove an Arena by name.
     * @param name The Arena to remove.
     * @return True if the arena was delete. False if it was not found.
     */
    public boolean deleteArena(String name) {
        for (Arena ar : getArenas()) {
            if (ar.getName().equalsIgnoreCase(name)) {
                arenas.remove(ar);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the arena with a specific name or null if the arena does not exist.
     * @param name The arena name
     * @return The arena or null
     */
    public Arena getArena(String name) {
        for (Arena ar : getArenas()) {
            if (ar.getName().equalsIgnoreCase(name)) {
                return ar;
            }
        }
        return null;
    }

    /**
     * Returns an ArrayList<Arena> with all arenas known.
     * @return the arenas
     */
    public ArrayList<Arena> getArenas() {
        return arenas;
    }

    /**
     * Check if an arena exists.
     * @param arenaName Name of the arena to check for.
     * @return Does it exist?
     */
    public boolean exists(String arenaName) {
        for (Arena ar : getArenas()) {
            if (ar.getName().equalsIgnoreCase(arenaName)) {
                return true;
            }
        }
        return false;
    }
}
