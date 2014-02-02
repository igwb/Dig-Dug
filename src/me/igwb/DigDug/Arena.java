package me.igwb.DigDug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldedit.regions.CuboidRegion;

public class Arena {
    private FileConfiguration arenaConfig = null;
    private File arenaConfigFile = null;

    private DigDug parent;

    private CuboidRegion regionArena;
    private CuboidRegion regionDig;

    private HashMap<String, Location> playerSpawns;
    private Location exitPoint;

    private String name;

    /**
     * Creates a new instance of Arena.
     * @param parentPlugin Instance of the DigDug plugin.
     * @param arenaName The name for the new Arena.
     */
    public Arena(DigDug parentPlugin, String arenaName) {

        parent = parentPlugin;
        name = arenaName;
        playerSpawns = new HashMap<String, Location>();
    }

    /**
     * Checks if this arena is valid and if it can run a game.
     * @return valid or not
     */
    public boolean isValid() {
        boolean valid = true;

        //Check if the regions are set
        valid = valid && regionArena != null && regionDig != null;

        //Check if the dig region is inside of the arena region
        valid = valid && regionArena.contains(regionDig.getPos1()) && regionArena.contains(regionDig.getPos2());

        //Check if there are at least two player spawns.
        valid = valid && playerSpawns != null && playerSpawns.size() >= 2;

        //Check if the exit point exists
        valid = valid && exitPoint != null;

        save();

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

        if (missing.size() == 0) {
            return null;
        } else {
            return missing;
        }
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
    public HashMap<String, Location> getPlayerSpawns() {
        return playerSpawns;
    }

    /**
     * Sets the player spawn points.
     * @param playerSpawnPoints Set the spawn points as a hashmap<String name, Vector location>.
     */
    public void setPlayerSpawns(HashMap<String, Location> playerSpawnPoints) {
        this.playerSpawns = playerSpawnPoints;
    }

    /**
     * Adds a new player spawn to the Arena.
     * @param spawnName Name of the new spawn.
     * @param location Location of the new spawn.
     * @return If the spawn was added.
     */
    public boolean addPlayerSpawn(String spawnName, Location location) {

        //Check if a spawn by that name already exists
        if (!playerSpawns.containsKey(spawnName.toLowerCase())) {
            playerSpawns.put(spawnName, location);
            return true;
        }
        return false;
    }

    /**
     * Returns the point players are teleported to when the Arena ends.
     * @return The exit point.
     */
    public Location getExitPoint() {
        return exitPoint;
    }

    /**
     * Sets the point players are teleported to when the Arena ends.
     * @param exitTeleportPoint The exit point.
     */
    public void setExitPoint(Location exitTeleportPoint) {
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
     * Saves this Arena to file.
     */
    public void save() {
        FileConfiguration conf = getArenaConfig();

        if (regionArena != null) {
            conf.set("regions.arena", Serializer.cuboidToString(regionArena));
        }

        if (regionDig != null) {
            conf.set("regions.dig", Serializer.cuboidToString(regionDig));
        }

        if (playerSpawns != null) {
            for (String key : playerSpawns.keySet()) {
                conf.set("spawns." + key, Serializer.locationToString(playerSpawns.get(key)));
            }
        }

        if (exitPoint != null) {
            conf.set("warps.exit", Serializer.locationToString(exitPoint));
        }

        try {
            conf.save(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Attempts to load settings for this Arena from file.
     */
    public void load() {
        FileConfiguration conf = getArenaConfig();

        regionArena = Serializer.stringToCuboid(conf.getString("regions.arena"));
        regionArena = Serializer.stringToCuboid(conf.getString("regions.dig"));
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

    /**
     * Gets the FileConfiguration for this Arena.
     * @return The configuration
     */
    public FileConfiguration getArenaConfig() {
        if (arenaConfig == null) {
            reloadArenaConfig();
        }
        return arenaConfig;
    }

    /**
     * Saves the default configuration to file if it doesn't exist already.
     */
    public void saveDefaultConfig() {
        if (arenaConfigFile == null) {
            parent.getLogger().log(Level.INFO, "Saving to: " + parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml");
            arenaConfigFile = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml");
        }
        if (!arenaConfigFile.exists()) {
            parent.saveResource("arenas\\arenaConfig.yml", false);
            File temp = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena.yml");
            temp.renameTo(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml"));
        }
    }

    /**
     * Reloads the Arena configuration from File.
     */
    public void reloadArenaConfig() {
        if (arenaConfigFile == null) {
            arenaConfigFile = new File(parent.getDataFolder(), "\\arenas\\arena_" + name + ".yml");
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = parent.getResource("\\arenas\\arena.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            arenaConfig.setDefaults(defConfig);
        }
    }
}
