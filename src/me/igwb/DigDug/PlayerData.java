package me.igwb.DigDug;

import java.io.Serializable;
import java.util.Collection;



import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerData implements Serializable {

    private static final long serialVersionUID = 476885974497717862L;

    private String playerName;
    private Float exp, saturation;
    private Integer hunger;
    private Double health;
    private Collection<PotionEffect> potion;
    private PlayerInventory inv;

    /**
     * Creates an instance of PlayerData with the data of a Player.
     * @param name The name of the Player.
     */
    public PlayerData(String name) {

        Player p = Bukkit.getServer().getPlayer(name);

        if (p != null && p instanceof Player) {
            playerName = name;
            exp = p.getExp();
            potion = p.getActivePotionEffects();
            inv = p.getInventory();
            health = p.getHealth();
            hunger = p.getFoodLevel();
            saturation = p.getSaturation();
        }
    }

    /**
     * Serializes the data.
     * @return The string
     */
    public String serialize() {
        StringBuilder data;

        data = new StringBuilder(playerName);
        data.append("#" + exp.toString());
        data.append("#" + health.toString());
        data.append("#" + hunger.toString());
        data.append("#" + saturation.toString());
        data.append("#" + potion.toArray().toString());
        data.append("#" + inv.getArmorContents().toString());
        data.append("#" + inv.getContents().toString());

        return data.toString();
    }

    /**
     * Restores the Player to this data.
     *
     */
    public void restore() {

    }
}
