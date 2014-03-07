package me.igwb.DigDug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.igwb.DigDug.Effects.BlockEffect;
import me.igwb.DigDug.Effects.BlockEffect.Target;
import me.igwb.DigDug.Effects.BlockEffectPotion;
import me.igwb.DigDug.Effects.BlockEffectScore;
import me.igwb.DigDug.Effects.SerializedBlockEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

    private FileConfiguration arenaBlocksConfig = null;
    private File arenaBlocksConfigFile = null;

    private FileConfiguration arenaEffectsConfig = null;
    private File arenaEffectsConfigFile = null;

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
    private HashMap<String, Location> playerSpecificSpawns;
    private HashMap<String, Integer> scores;

    private ArrayList<BlockEffect> effects;
    private boolean editMode = false, running = false;
    private HashMap<String, ArenaScoreboard> scoreboards;

    private Integer minPlayers;
    private Integer autoStartTimer;
    private Integer gameDuration;
    private Integer startTime;

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

        players = new ArrayList<>();
        playerSpecificSpawns = new HashMap<String, Location>();
        scores = new HashMap<String, Integer>();

        effects = new ArrayList<BlockEffect>();
        scoreboards = new HashMap<String, ArenaScoreboard>();

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
        FileConfiguration blocksConf = getArenaBlocksConfig();
        FileConfiguration effectsConf = getArenaEffectsConfig();

        //Save the times
        if (autoStartTimer == null) {
            autoStartTimer = 20;
        }
        conf.set("time.autoStart", autoStartTimer);

        if (gameDuration == null) {
            gameDuration = 120;
        }
        conf.set("time.gameTime", gameDuration);

        //Save minPlayers
        if (minPlayers == null) {
            minPlayers = 1;
        }
        conf.set("minPlayers", minPlayers);


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
        HashMap<String, String> chances = new HashMap<String, String>();
        for (BlockChance bc : blocksChances) {
            chances.put(bc.getBlock().getId() + ":" + bc.getBlock().getData(), bc.getChance() + "%");
        }

        blocksConf.createSection("blocks", chances);

        //Save the effects

        //Create examples effect if there are no effects yet.
        if (effects == null || effects.size() == 0) {
            effects.add(new BlockEffectScore(Material.STONE, 0, 0, Target.trigger, 1));
            effects.add(new BlockEffectPotion(Material.COAL_ORE, 0, 1, Target.trigger, new PotionEffect(PotionEffectType.BLINDNESS, 120, 1), 20));
        }

        SerializedBlockEffect current;

        for (BlockEffect eff : effects) {

            current = eff.serialize();
            effectsConf.createSection("effects." +  current.getBlockType().getId() + ":" + current.getBlockDataValue() + "." + current.getId(), current.getData());
        }

        try {
            conf.save(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml"));
            blocksConf.save(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_blocks.yml"));
            effectsConf.save(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_effects.yml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Attempts to load settings for this Arena from file.
     */
    @SuppressWarnings("deprecation")
    public void load() {
        FileConfiguration conf = getArenaConfig();
        FileConfiguration blocksConf = getArenaBlocksConfig();
        FileConfiguration effectsConf = getArenaEffectsConfig();

        //Load the times
        autoStartTimer = (Integer) conf.get("time.autoStart");
        gameDuration =  (Integer) conf.get("time.gameTime");

        //Load minPlayers
        minPlayers = (Integer) conf.get("minPlayers");


        //Load the regions
        regionArena = Serializer.stringToCuboid(conf.getString("regions.arena"));
        regionDig = Serializer.stringToCuboid(conf.getString("regions.dig"));

        //Load the spawns
        playerSpawns.clear();

        HashMap<String, Object> spawnsRead = (HashMap<String, Object>) conf.getConfigurationSection("spawns").getValues(false);
        for (String key : spawnsRead.keySet()) {
            playerSpawns.put(key, Serializer.stringToLocation((String) spawnsRead.get(key)));
        }

        //Load the exit warp
        exitPoint = Serializer.stringToLocation(conf.getString("warps.exit"));

        //Load the DigRegion composition
        blocksChances.clear();

        Map<String, Object> chanceBlocks;
        String strKey;
        chanceBlocks = blocksConf.getConfigurationSection("blocks").getValues(false);

        for (Object cb : chanceBlocks.keySet()) {
            strKey = cb.toString();
            blocksChances.add(new BlockChance(new BaseBlock(Integer.parseInt(strKey.split(":")[0]), Integer.parseInt(strKey.split(":")[1])), Double.parseDouble(chanceBlocks.get(cb).toString().replace("%", ""))));
        }

        if (blocksChances.size() == 0) {
            blocksChances.add(new BlockChance(new BaseBlock(7), 100.0));
        }

        //Load the effects
        effects.clear();

        Object[] blocks;
        Object[] effectIds;
        Map<String, Object> rawData = new HashMap<String, Object>();
        HashMap<String, String> data = new HashMap<String, String>();

        SerializedBlockEffect current;

        //Get all defined blocks from the effects section.
        blocks = effectsConf.getConfigurationSection("effects").getKeys(false).toArray();

        for (Object block : blocks) {
            //Get all defined effect id's for this block type-
            effectIds = effectsConf.getConfigurationSection("effects." + block).getKeys(false).toArray();

            for (Object effectId : effectIds) {
                //Get the data for this effect as a HashMap<String, String>
                rawData.clear();
                rawData = effectsConf.getConfigurationSection("effects." + block + "." + effectId).getValues(false);

                //Convert the HashMap<Object, String> to a HashMap<String, String>
                data.clear();
                for (String key : rawData.keySet()) {
                    data.put(key, (String) rawData.get(key));
                }

                //Finally de-serialize
                current = new SerializedBlockEffect(Material.getMaterial(Integer.parseInt(block.toString().split(":")[0])), Integer.parseInt(block.toString().split(":")[1]), Integer.parseInt((String) effectId), data);

                //Determine the effect type and create it
                if (current.getData().containsKey("effect")) {
                    effects.add(new BlockEffectPotion(current));
                    continue;
                }

                if (current.getData().containsKey("score")) {
                    effects.add(new BlockEffectScore(current));
                    continue;
                }
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
     * Gets the Blocks FileConfiguration for this Arena.
     * @return The blocks configuration
     */
    public FileConfiguration getArenaBlocksConfig() {
        if (arenaBlocksConfig == null) {
            reloadArenaConfig();
        }
        return arenaBlocksConfig;
    }

    /**
     * Gets the Effects FileConfiguration for this Arena.
     * @return The effects configuration
     */
    public FileConfiguration getArenaEffectsConfig() {
        if (arenaEffectsConfig == null) {
            reloadArenaConfig();
        }
        return arenaEffectsConfig;
    }

    /**
     * Saves the default configuration to file if it doesn't exist already.
     */
    public void saveDefaultConfig() {
        //Save the arena data
        //___________________

        if (arenaConfigFile == null) {
            arenaConfigFile = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml");
        }
        if (!arenaConfigFile.exists()) {
            parent.saveResource("arenas\\arenaConfig.yml", false);
            File temp = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena.yml");
            temp.renameTo(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + ".yml"));
        }

        //Save the arena blocks settings
        //______________________________

        if (arenaBlocksConfigFile == null) {
            arenaBlocksConfigFile = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_blocks.yml");
        }
        if (!arenaBlocksConfigFile.exists()) {
            parent.saveResource("arenas\\arena_blocks.yml", false);
            File temp = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_blocks.yml");
            temp.renameTo(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_blocks.yml"));
        }

        //Save the arena effects settings
        //_______________________________

        if (arenaEffectsConfigFile == null) {
            arenaEffectsConfigFile = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_effects.yml");
        }
        if (!arenaEffectsConfigFile.exists()) {
            parent.saveResource("arenas\\arena_effects.yml", false);
            File temp = new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_effects.yml");
            temp.renameTo(new File(parent.getDataFolder().getAbsolutePath() + "\\arenas\\arena_" + name + "_effects.yml"));
        }
    }

    /**
     * Reloads the Arena configuration from File.
     */
    public void reloadArenaConfig() {
        InputStream defConfigStream;

        //Load the arena data
        //___________________
        if (arenaConfigFile == null) {
            arenaConfigFile = new File(parent.getDataFolder(), "\\arenas\\arena_" + name + ".yml");
        }
        arenaConfig = YamlConfiguration.loadConfiguration(arenaConfigFile);

        // Look for defaults in the jar
        defConfigStream = parent.getResource("\\arenas\\arena.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            arenaConfig.setDefaults(defConfig);
        }

        //Load the arena blocks settings
        //______________________________

        if (arenaBlocksConfigFile == null) {
            arenaBlocksConfigFile = new File(parent.getDataFolder(), "\\arenas\\arena_" + name + "_blocks.yml");
        }
        arenaBlocksConfig = YamlConfiguration.loadConfiguration(arenaBlocksConfigFile);

        // Look for defaults in the jar
        defConfigStream = parent.getResource("\\arenas\\arena_blocks.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            arenaBlocksConfig.setDefaults(defConfig);
        }

        //Load the arena effects settings
        //_______________________________

        if (arenaEffectsConfigFile == null) {
            arenaEffectsConfigFile = new File(parent.getDataFolder(), "\\arenas\\arena_" + name + "_effects.yml");
        }
        arenaEffectsConfig = YamlConfiguration.loadConfiguration(arenaEffectsConfigFile);

        // Look for defaults in the jar
        defConfigStream = parent.getResource("\\arenas\\arena_blocks.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            arenaEffectsConfig.setDefaults(defConfig);
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
        BaseBlock currentBase;
        BlockVector currentVector;
        World world = Bukkit.getServer().getWorld(regionDig.getWorld().getName());

        while (it.hasNext()) {
            currentVector = it.next();
            currentBase = rndFill.next(currentVector.getBlockX(), currentVector.getBlockY(), currentVector.getBlockZ());

            current = new Location(world, currentVector.getBlockX(), currentVector.getBlockY(), currentVector.getBlockZ()).getBlock();
            current.setType(Material.getMaterial(currentBase.getType()));
            current.setData((byte) currentBase.getData());
        }
    }

    /**
     * Called on a BlockBreakEvent.
     * @param e the event
     */
    @SuppressWarnings("deprecation")
    public void notifyOfBlockBreak(BlockBreakEvent e) {

        Location blockLocation =  e.getBlock().getLocation();

        //Check if the block is inside of this arena
        if (regionArena.contains(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()))) {

            //Check if the block is inside of the dig region
            if (regionDig.contains(new Vector(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()))) {
                if (isRunning()) {

                    //Execute effects
                    for (BlockEffect eff : effects) {
                        if (eff.getTriggerBlock().equals(e.getBlock().getType()) && Byte.toString(e.getBlock().getState().getRawData()).equals(eff.getDataValue().toString())) {
                            eff.execute(e.getPlayer(), this);
                        }
                    }

                    //Prevent drops
                    e.setExpToDrop(0);
                    e.setCancelled(true);
                    e.getBlock().setType(Material.AIR);
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("There is no game in progress!");
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
     * Called on a PlayerMoveEvent.
     * @param e The event.
     */
    public void notifyOfPlayerMove(PlayerMoveEvent e) {

        Location locTo = e.getTo();
        Location pSpawn;

        //Prevent players from leaving their spawn before the game starts.
        if (!isRunning() && players.contains(e.getPlayer().getName())) {
            pSpawn = playerSpecificSpawns.get(e.getPlayer().getName());

            //Return if the player is still standing on his spawn.
            if (locTo.getX() == pSpawn.getX() && locTo.getY() == pSpawn.getY() && locTo.getZ() == pSpawn.getZ()) {
                return;
            } else {
                //Teleport the player back to his spawn.
                e.setCancelled(true);
                e.getPlayer().teleport(new Location(pSpawn.getWorld(), pSpawn.getX(), pSpawn.getY(), pSpawn.getZ()));
                e.getPlayer().teleport(playerSpecificSpawns.get(e.getPlayer().getName()));
                e.getPlayer().sendMessage("You can not move until the game starts!");
                return;
            }
        }

        if (isRunning() && !regionArena.contains(new Vector(locTo.getBlockX(), locTo.getBlockY(), locTo.getBlockZ()))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("You can not leave the arena while it is running!");
        }
    }

    /**
     * Returns if the arena is running.
     * @return Is it running?
     */
    public boolean isRunning() {

        return running;
    }

    /**
     * Adds the given amount of points to a player's score. The amount may be negative.
     * @param player The player
     * @param pointModifier The value to add to the score. Can be negative.
     */
    public void modifyPlayerPoints(String player, Integer pointModifier) {

        scores.put(player, scores.get(player) + pointModifier);

        scoreboards.get(player).setScore(scores.get(player));

        //TODO: Fire score changed event
    }

    /**
     * Counts down and then starts the game.
     */
    private void startGameCountdown() {

        parent.getServer().getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
            @Override
            public void run() {
                if (!isRunning() && players.size() >= minPlayers) {
                    startGame();
                }
                startGame();
            }
        }, autoStartTimer * 20L);
    }

    /**
     * Starts the end countdown.
     */
    private void startEndCountDown() {

        parent.getServer().getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
            @Override
            public void run() {
                endGame();
            }
        }, gameDuration * 20L);
    }

    /**
     * Starts the game.
     */
    public void startGame() {

        running = true;

        for (String p : players) {
            Bukkit.getServer().getPlayer(p).sendMessage("Ready! Set! Mine!");

            scoreboards.put(p, new ArenaScoreboard(Bukkit.getServer().getPlayer(p)));
        }

        startTime = (int) System.currentTimeMillis();

        startAutoScoreboards();
        startEndCountDown();

    }

    /**
     * Automatically updates the scoreboards until the game ends.
     */
    private void startAutoScoreboards() {
        final  Integer remainingTime = ((gameDuration * 1000) - (((int) System.currentTimeMillis()) - startTime)) / 1000;
        if (remainingTime > 0 && isRunning()) {
            parent.getServer().getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
                @Override
                public void run() {
                    for (String p : scoreboards.keySet()) {

                        scoreboards.get(p).setTimeRemaining(remainingTime);
                    }
                    startAutoScoreboards();
                }
            },  20L);
        }

    }

    /**
     * Ends a game.
     */
    public void endGame() {

        for (String p : players) {

            leavePlayer(p);
        }

        players.clear();
        scores.clear();
        scoreboards.clear();
        playerSpecificSpawns.clear();

        regenerateDigRegion();

        running = false;
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

            playerSpecificSpawns.put(playerName, playerSpawns.get(playerSpawns.keySet().toArray()[players.size() - 1]));

            telportPlayerToSpawn(playerName);

            startGameCountdown();
        }
    }

    /**
     * Teleports a player to his playerSpawn.
     * @param playerName The player
     */
    private void telportPlayerToSpawn(String playerName) {

        Bukkit.getServer().getPlayer(playerName).teleport(playerSpecificSpawns.get(playerName));

    }

    /**
     * Makes a player leave the arena. Makes him teleport to the exit point.
     * @param playerName The player.
     */
    public void leavePlayer(String playerName) {

        Bukkit.getServer().getPlayer(playerName).teleport(exitPoint);
        Bukkit.getServer().getPlayer(playerName).sendMessage("You have scored " + scores.get(playerName) + " points!");
        scoreboards.get(playerName).remove();
    }

    /**
     * Returns an ArrayList of all players currently in the arena.
     * @return The ArrayList.
     */
    public ArrayList<String> getPlayers() {

        return players;
    }



}
