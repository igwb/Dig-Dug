package me.igwb.DigDug;

import java.util.ArrayList;

import org.bukkit.util.Vector;

import com.sk89q.worldedit.regions.CuboidRegion;

public class Arena {
    private CuboidRegion regionArena;
    private CuboidRegion regionDig;

    private ArrayList<Vector> playerSpawns;
    private Vector exitPoint;

    private String name;


    public Arena(String arenaName) {

        name = arenaName;
    }

    /**
     * Checks if this arena is valid and if it can run a game.
     * @return valid or not
     */
    public boolean isValid() {
        boolean valid = true;

        //Check if the regions are set and if the dig region is inside of the arena region
        valid = valid && regionArena != null && regionDig != null && regionArena.contains(regionDig.getMinimumPoint()) && regionArena.contains(regionArena.getMaximumPoint());

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
            if (!regionArena.contains(regionDig.getMinimumPoint()) || !regionArena.contains(regionDig.getMaximumPoint())) {
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


    public CuboidRegion getRegionArena() {
        return regionArena;
    }

    public void setRegionArena(CuboidRegion rgArena) {
        this.regionArena = rgArena;
    }

    public CuboidRegion getRegionDig() {
        return regionDig;
    }

    public void setRegionDig(CuboidRegion rgDig) {
        this.regionDig = rgDig;
    }

    public ArrayList<Vector> getPlayerSpawns() {
        return playerSpawns;
    }

    public void setPlayerSpawns(ArrayList<Vector> playerSpawnPoints) {
        this.playerSpawns = playerSpawnPoints;
    }

    public Vector getExitPoint() {
        return exitPoint;
    }

    public void setExitPoint(Vector exitTeleportPoint) {
        this.exitPoint = exitTeleportPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String arenaName) {
        if (arenaName != null) {
            name = arenaName;
        }
    }

}
