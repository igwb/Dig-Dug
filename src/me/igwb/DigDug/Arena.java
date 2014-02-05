package me.igwb.DigDug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.regions.CuboidRegion;

public class Arena {

    //Configurations
    private FileConfiguration arenaConfig = null;
    private File arenaConfigFile = null;

    //Utility
    private DigDug parent;

    //Regions
    private CuboidRegion regionArena;
    private CuboidRegion regionDig;

    //Warps
    private HashMap<String, Location> playerSpawns;
    private Location exitPoint;

    //Regeneration
    private ArrayList<BlockChance> blocksChances;

    //Informational
    private String name;

    //Game Mechanics
    private ArrayList<String> players;
    private HashMap<String, Integer> scores;
    private ArrayList<BlockEffect> effects;
    private boolean editMode, running = true; //TODO: remove default true

    /**
     * Creates a new instance of Arena.
     * @param parentPlugin Instance of the DigDug plugin.
     * @param arenaName The name for the new Arena.
     */
    public Arena(DigDug parentPlugin, String arenaName) {

        parent = parentPlugin;
        name = arenaName;
        playerSpawns = new HashMap<String, Location>();

        blocksChances = new ArrayList<BlockChance>();
        blocksChances.add(new BlockChance(new BaseBlock(7), 100.0));

        players = new ArrayList<>();
        scores = new HashMap<String, Integer>();

        effects = new ArrayList<BlockEffect>();

    }

    /*
     * Getters
     */

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
     * Returns the dig region.
     * @return The region.
     */
    public CuboidRegion getRegionDig() {
        return regionDig;
    }

    /**
     * Gets the player spawns in a hashmap<String name, Vector location>.
     * @return The hashmap.
     */
    public HashMap<String, Location> getPlayerSpawns() {
        return playerSpawns;
    }

    /**
     * Returns the point players are teleported to when the Arena ends.
     * @return The exit point.
     */
    public Location getExitPoint() {
        return exitPoint;
    }

    /**
     * Returns the name of this Arena.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of players that can play in this arena. Equals the number of playerSpawns
     * @return Max players count.
     */
    public Integer getMaxPlayers() {

        if (playerSpawns != null) {
            return playerSpawns.size();
        }
        return 0;
    }

    /*
     * Setters
     */

    /**
     * Sets a new Arena region.
     * @param rgArena The region.
     */
    public void setRegionArena(CuboidRegion rgArena) {
        this.regionArena = rgArena;

        save();
    }

    /**
     * Sets a new dig region.
     * @param rgDig The region.
     */
    public void setRegionDig(CuboidRegion rgDig) {
        this.regionDig = rgDig;

        save();
    }

    /**
     * Sets the player spawn points.
     * @param playerSpawnPoints Set the spawn points as a hashmap<String name, Vector location>.
     */
    public void setPlayerSpawns(HashMap<String, Location> playerSpawnPoints) {
        this.playerSpawns = playerSpawnPoints;

        save();
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

            save();
            return true;
        }
        return false;
    }

    /**
     * Sets the point players are teleported to when the Arena ends.
     * @param exitTeleportPoint The exit point.
     */
    public void setExitPoint(Location exitTeleportPoint) {
        this.exitPoint = exitTeleportPoint;

        save();
    }

    /*
     * Saving and loading
     */

    /**
     * Saves this Arena to file.
     */
    @SuppressWarnings("deprecation")
    public void save() {
        FileConfiguration conf = getArenaConfig();

        //Save the regions
        if (regionArena != null) {
            conf.set("regions.arena", Serializer.cuboidToString(regionArena));
        }

        if (regionDig != null) {
            conf.set("regions.dig", Serializer.cuboidToString(regionDig));
        }

        //Save the spawns
        if (playerSpawns != null) {
            for (String key : playerSpawns.keySet()) {
                conf.set("spawns." + key, Serializer.locationToString(playerSpawns.get(key)));
            }
        }
        //Save the exit warp
        if (exitPoint != null) {
            conf.set("warps.exit", Serializer.locationToString(exitPoint));
        }

        //Save the DigRegion's composition
        ArrayList<String> chanceStrings = new ArrayList<String>();
        for (BlockChance bc : blocksChances) {
            chanceStrings.add(Serializer.blockChanceToString(bc));
        }

        conf.set("blocks", chanceStrings);

        //Save the effects
        if (effects == null || effects.size() == 0) {
            effects.add(new BlockEffect("example", Material.HARD_CLAY, new PotionEffect(PotionEffectType.BLINDNESS, 20, 1), 5, BlockEffect.Target.all_but_trigger));
        }

        HashMap<String, HashMap<String, String>> effectList = new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> map;
        for (BlockEffect eff : effects) {
            map = eff.serialize();
            effectList.put(((Integer) eff.getTriggerBlock().getId()).toString(), eff.serialize());
            conf.createSection("effects." + map.get("block") + "." + map.get("name"), map);
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

        //Load the regions
        regionArena = Serializer.stringToCuboid(conf.getString("regions.arena"));
        regionDig = Serializer.stringToCuboid(conf.getString("regions.dig"));

        //Load the spawns
        HashMap<String, Object> spawnsRead = (HashMap<String, Object>) conf.getConfigurationSection("spawns").getValues(false);
        for (String key : spawnsRead.keySet()) {
            playerSpawns.put(key, Serializer.stringToLocation((String) spawnsRead.get(key)));
        }

        //Load the exit warp
        exitPoint = Serializer.stringToLocation(conf.getString("warps.exit"));

        //Load the DigRegion composition
        @SuppressWarnings("unchecked")
        ArrayList<String> chanceStrings = (ArrayList<String>) conf.get("blocks");
        blocksChances.clear();
        for (String cs : chanceStrings) {
            blocksChances.add(Serializer.stringToBlockChance(cs));
        }

        //Load the effects
        Object[] blocks = conf.getConfigurationSection("effects").getKeys(false).toArray();
        Object[] blockEffects;
        Map<String, Object> map;
        HashMap<String, String> stringMap = new HashMap<String, String>();

        for (Object block : blocks) {
            blockEffects = conf.getConfigurationSection("effects." + block).getKeys(false).toArray();
            for (Object effectName : blockEffects) {
                map = conf.getConfigurationSection("effects." + block + "." + effectName).getValues(false);

                stringMap.clear();
                //Bukkit.getServer().getLogger().log(Level.INFO, (String) effectName);
                for (String key : map.keySet()) {
                    // Bukkit.getServer().getLogger().log(Level.INFO, key + ", " + map.get(key));
                    stringMap.put(key, (String) map.get(key));
                }

                effects.add(new BlockEffect(stringMap));
            }
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

    /*
     * Game mechanics
     */

    /**
     * Regenerates the dig region of this Arena.
     * The Arena must be valid.
     */
    @SuppressWarnings("deprecation")
    public void regenerateDigRegion() {

        //Is the arena valid?
        if (!isValid()) {
            return;
        }

        Iterator<BlockVector> it = regionDig.iterator();
        RandomFillPattern rndFill = new RandomFillPattern(blocksChances);

        Block current;
        BlockVector currentVector;
        World world = Bukkit.getServer().getWorld(regionDig.getWorld().getName());

        while (it.hasNext()) {
            currentVector = it.next();
            current = new Location(world, currentVector.getBlockX(), currentVector.getBlockY(), currentVector.getBlockZ()).getBlock();
            current.setType(Material.getMaterial((rndFill.next(currentVector.getBlockX(), currentVector.getBlockY(), currentVector.getBlockZ()).getType())));
        }
    }

    /**
     * Called on a BlockBreakEvent.
     * @param e the event
     */
    public void notifyOfBlockBreak(BlockBreakEvent e) {

        Location blockLocation =  e.getBlock().getLocation();

        //Check if the block is inside of this arena
        if (regionArena.contains(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()))) {

            //Check if the block is inside of the dig region
            if (regionDig.contains(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()))) {
                if (isRunning()) {

                    //Execute effects
                    for (BlockEffect eff : effects) {
                        if (eff.getTriggerBlock().equals(e.getBlock().getType())) {
                            eff.execute(e.getPlayer(), this);
                            e.getPlayer().sendMessage("Exectued effect " + eff.getTriggerBlock().toString());
                        }
                    }

                    //Prevent drops
                    e.setExpToDrop(0);
                    e.setCancelled(true);
                    e.getBlock().setType(Material.AIR);
                }
            } else {
                //Cancel if the block is inside of the arena and outside of the dig region.
                if (!editMode) {
                    e.getPlayer().sendMessage("You are not allowed to destroy the arena!");
                    e.setCancelled(true);
                }
            }
        }


    }

    /**
     * Returns if the arena is running.
     * @return Is it running?
     */
    public boolean isRunning() {

        return running;
    }

    /*
     * Players
     */

    /**
     * Adds a player to the players in the arena. This will teleport him to the arena.
     * @param playerName The player that wants to join.
     */
    public void joinPlayer(String playerName) {

        if (!players.contains(playerName) && players.size() < getMaxPlayers()) {
            players.add(playerName);

            scores.put(playerName, 0);

            Bukkit.getServer().getPlayer(playerName).teleport(playerSpawns.get(playerSpawns.keySet().toArray()[players.size() - 1]));
        }
    }

    /**
     * Returns an ArrayList of all players currently in the arena.
     * @return The ArrayList.
     */
    public ArrayList<String> getPlayers() {

        return players;
    }

    /**
     * Adds the given amount of points to a player's score. The amount may be negative.
     * @param player The player
     * @param pointModifier The value to add to the score. Can be negative.
     */
    public void modifyPlayerPoints(String player, Integer pointModifier) {

        scores.put(player, scores.get(player) + pointModifier);

        //TODO: Fire score changed event
    }





}
