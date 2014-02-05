package me.igwb.DigDug;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class BlockEffect {

    private Material triggerBlock;
    private PotionEffect potionEffect;
    private Integer pointValue;
    private Affected affected;

    public enum Affected {
        all,
        all_but_trigger,
        trigger;
    }

    /**
     * Creates a new BlockEffect.
     * @param block The Block (Material) causing this effect to trigger.
     * @param potion The PotionEffect to apply.
     * @param points The Points to give the affected players. May be negative.
     * @param targets The Players affected.
     */
    public BlockEffect(Material block, PotionEffect potion, Integer points, Affected targets) {
        triggerBlock = block;
        potionEffect = potion;
        pointValue = points;
        affected = targets;
    }

    /**
     * Returns the Material that triggers this effect.
     * @return The Material.
     */
    public Material getTriggerBlock() {

        return triggerBlock;
    }

    /**
     * Executes this BlockEffect.
     * @param triggerPlayer The player who triggered the execution.
     * @param arena The arena the effect should take place in.
     */
    public void execute(Player triggerPlayer, Arena arena) {
        Player current;

        switch (affected) {
        case all:

            for (String name : arena.getPlayers()) {
                current = Bukkit.getServer().getPlayer(name);
                applyEffects(current, arena);
            }
            break;
        case all_but_trigger:

            for (String name : arena.getPlayers()) {
                if (!name.equalsIgnoreCase(triggerPlayer.getName())) {
                    current = Bukkit.getServer().getPlayer(name);
                    applyEffects(current, arena);
                }
            }
            break;
        case trigger:

            applyEffects(triggerPlayer, arena);
            break;
        default:
            break;
        }
    }

    /**
     * Applies the effects to a Player.
     * @param p The player affected.
     * @param arena The arena the player is in.
     */
    private void applyEffects(Player p, Arena arena) {
        p.addPotionEffect(potionEffect, true);
        arena.modifyPlayerPoints(p.getName(), pointValue);
    }
}
