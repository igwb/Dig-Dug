package me.igwb.DigDug;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.regions.CuboidRegion;

public abstract class Serializer {

    /**
     * Serialize a WorldEdit cuboid.
     * @param region The cuboid to serialize.
     * @return The String.
     */
    public static String cuboidToString(CuboidRegion region) {

        return region.getWorld().getName() + "#" + region.toString();
    }

    /**
     * De-serialize a String to a WorldEdit cuboid.
     * @param regionString The String to de-serialize.
     * @return The cuboid.
     */
    public static CuboidRegion stringToCuboid(String regionString) {

        String worldName, pos1, pos2;
        String[] split;
        Vector v1, v2;

        split = regionString.split("#", 2);

        worldName = split[0];
        pos1 = split[1].split(" - ", 2)[0];
        pos2 = split[1].split(" - ", 2)[1];

        pos1 = pos1.replace("(", "").replace(")", "").replace(" ", "");
        pos2 = pos2.replace("(", "").replace(")", "").replace(" ", "");

        Bukkit.getLogger().log(Level.INFO, "pos1: " + pos1);
        split = pos1.split(",", 3);
        v1 = new Vector(Integer.parseInt(split[0].substring(0, split[0].indexOf("."))), Integer.parseInt(split[1].substring(0, split[1].indexOf("."))), Integer.parseInt(split[2].substring(0, split[2].indexOf("."))));

        split = pos2.split(",", 3);
        v2 = new Vector(Integer.parseInt(split[0].substring(0, split[0].indexOf("."))), Integer.parseInt(split[1].substring(0, split[1].indexOf("."))), Integer.parseInt(split[2].substring(0, split[2].indexOf("."))));

        return new CuboidRegion(BukkitUtil.getLocalWorld(Bukkit.getWorld(worldName)), v1, v2);
    }

    /**
     * Serialize a Location.
     * @param location The Location to serialize.
     * @return The String.
     */
    public static String locationToString(Location location) {

        String locString;

        locString = location.getWorld().getName() + "#";
        locString += "(" + location.getX() + "," + location.getY() + "," + location.getBlockZ() + "," + location.getYaw() + "," + location.getPitch() + ")";

        return locString;
    }

    /**
     * De-serialize a location.
     * @param locString The String to de-serialize.
     * @return The location.
     */
    public static Location stringToLocation(String locString) {

        String worldName, coords;
        Double x, y, z;
        Float yaw, pitch;
        String[] split;


        split = locString.split("#", 2);
        worldName = split[0];
        coords = split[1].replace("(", "").replace(")", "");

        split = coords.split(",", 5);

        x = Double.parseDouble(split[0]);
        y = Double.parseDouble(split[1]);
        z = Double.parseDouble(split[2]);
        yaw = Float.parseFloat(split[3]);
        pitch = Float.parseFloat(split[4]);

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    /**
     * Serialize a BlockChance.
     * @param bc The BlockChance to serialize.
     * @return The String.
     */
    public static String blockChanceToString(BlockChance bc) {

        return  bc.getBlock().getId() + ":" + bc.getBlock().getData() + "-" + bc.getChance() + "%";
    }

    /**
     * De-serialize a BlockChance.
     * @param bc The string to de-serialize.
     * @return The BlockChance.
     */
    public static BlockChance stringToBlockChance(String bc) {

        String[] split = bc.replace("%", "").split("-");

        return new BlockChance(new BaseBlock(Integer.parseInt(split[0].split(":")[0]), Integer.parseInt(split[0].split(":")[1])), Double.parseDouble(split[1]));
    }

}
