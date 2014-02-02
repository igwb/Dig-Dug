package me.igwb.DigDug;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class ArenaManager {

    private ArrayList<Arena> arenas;

    /**
     * Creates a new instance of ArenaManager.
     */
    public ArenaManager() {
        arenas = new ArrayList<Arena>();
    }

    /**
     * Attempts to load arenas from file.
     * @param parent Instance of the DigDug plugin.
     */
    public void loadArenas(DigDug parent) {
        File arenaFolder = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas");

        parent.getLogger().log(Level.INFO, "Reading from: " + arenaFolder.getAbsolutePath());

        File[] arenaFiles = arenaFolder.listFiles();

        parent.getLogger().log(Level.INFO, "Found: " + arenaFiles.length);

        String name;
        for (File file : arenaFiles) {
            parent.getLogger().log(Level.INFO, "Found: " + file.getAbsolutePath());
            name = file.getName().replace(".yml", "").replace("arena_", "");
            addArena(parent, name);
            getArena(name).load();
        }
    }

    /**
     * Saves all loaded arenas to file.
     */
    public void saveArenas() {
        for (Arena ar : arenas) {
            ar.save();
        }
    }

    /**
     * Tries to add a new Arena by name. This will automatically create a new instance of the Arena class.
     * @param parent An instance of the DigDug plugin.
     * @param name The name for the Arena
     * @return true if the arena was added, false if an arena by that name already exists
     */
    public boolean addArena(DigDug parent, String name) {
        for (Arena ar : getArenas()) {
            if (ar.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        arenas.add(new Arena(parent, name));
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
