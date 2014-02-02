package me.igwb.DigDug;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.regions.CuboidRegion;

public class Arena {
    private CuboidRegion regionArena;
    private CuboidRegion regionDig;

    private HashMap<String, Vector> playerSpawns;
    private Vector exitPoint;

    private String name;

    /**
     * Creates a new instance of Arena.
     * @param arenaName The name for the new Arena.
     */
    public Arena(String arenaName) {

        name = arenaName;
        playerSpawns = new HashMap<String, Vector>();
    }

    /**
     * Checks if this arena is valid and if it can run a game.
     * @return valid or not
     */
    public boolean isValid() {
        boolean valid = true;

        //Check if the regions are set and if the dig region is inside of the arena region
        valid = valid && regionArena != null && regionDig != null && !(regionArena.contains(regionDig.getPos1()) && regionArena.contains(regionDig.getPos2()));

        //Check if there are at least two player spawns.
        valid = valid && playerSpawns != null && playerSpawns.size() >= 2;

        //Check if the exit point exists
        valid = valid && exitPoint != null;

        return valid;
    }

    /**
     * Returns a list of missing parameters and other things that are wrong with this arena. May return null.
     * @return Errors or null
     */
    public ArrayList<String> getMissing() {

        ArrayList<String> missing = new ArrayList<>();

        if (regionArena == null) {
            missing.add("The arena region is not defined!");
        }

        if (regionDig == null) {
            missing.add("The dig region is not defined!");
        }

        if (regionArena != null && regionDig != null) {
            if (!(regionArena.contains(regionDig.getPos1()) && regionArena.contains(regionDig.getPos2()))) {
                missing.add("The dig region is not inside of the arena region!");
            }
        }

        if (playerSpawns == null || playerSpawns.size() < 2) {
            missing.add("There must be at least two player spawns!");
        }

        if (exitPoint == null) {
            missing.add("The exit point is not defined!");
        }

        return missing;
    }


    /**
     * Returns the Arena region.
     * @return The region.
     */
    public CuboidRegion getRegionArena() {
        return regionArena;
    }

    /**
     * Sets a new Arena region.
     * @param rgArena The region.
     */
    public void setRegionArena(CuboidRegion rgArena) {
        this.regionArena = rgArena;
    }

    /**
     * Returns the dig region.
     * @return The region.
     */
    public CuboidRegion getRegionDig() {
        return regionDig;
    }

    /**
     * Sets a new dig region.
     * @param rgDig The region.
     */
    public void setRegionDig(CuboidRegion rgDig) {
        this.regionDig = rgDig;
    }

    /**
     * Gets the player spawns in a hashmap<String name, Vector location>.
     * @return The hashmap.
     */
    public HashMap<String, Vector> getPlayerSpawns() {
        return playerSpawns;
    }

    /**
     * Sets the player spawn points.
     * @param playerSpawnPoints Set the spawn points as a hashmap<String name, Vector location>.
     */
    public void setPlayerSpawns(HashMap<String, Vector> playerSpawnPoints) {
        this.playerSpawns = playerSpawnPoints;
    }

    /**
     * Adds a new player spawn to the Arena.
     * @param spawnName Name of the new spawn.
     * @param location Location of the new spawn.
     * @return If the spawn was added.
     */
    public boolean addPlayerSpawn(String spawnName, Vector location) {

        //Check if a spawn by that name already exists
        if (!playerSpawns.containsKey(spawnName.toLowerCase())) {
            playerSpawns.put(spawnName, location);
        }
        return false;
    }

    /**
     * Returns the point players are teleported to when the Arena ends.
     * @return The exit point.
     */
    public Vector getExitPoint() {
        return exitPoint;
    }

    /**
     * Sets the point players are teleported to when the Arena ends.
     * @param exitTeleportPoint The exit point.
     */
    public void setExitPoint(Vector exitTeleportPoint) {
        this.exitPoint = exitTeleportPoint;
    }

    /**
     * Returns the name of this Arena.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this Arena.
     * @param arenaName The new name.
     */
    public void setName(String arenaName) {
        if (arenaName != null) {
            name = arenaName;
        }
    }

}
