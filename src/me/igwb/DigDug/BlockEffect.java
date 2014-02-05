package me.igwb.DigDug;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockEffect {

    private String name;
    private Material triggerBlock;
    private PotionEffect potionEffect;
    private Integer pointValue;
    private Target affected;

    public enum Target {
        all,
        all_but_trigger,
        trigger;
    }

    /**
     * Creates a new BlockEffect.
     * @param effectName A Name for this effect. Can be anything.
     * @param block The Block (Material) causing this effect to trigger.
     * @param potion The PotionEffect to apply.
     * @param points The Points to give the affected players. May be negative.
     * @param targets The Players affected.
     */
    public BlockEffect(String effectName, Material block, PotionEffect potion, Integer points, Target targets) {

        name = effectName;
        triggerBlock = block;
        potionEffect = potion;
        pointValue = points;
        affected = targets;
    }

    /**
     * Creates a new BlockEffect with the data from a HashMap. Works best if the HashMap was created with the BlockEffect.serialize() method.
     * @param data The HashMap<String, String>.
     */
    public BlockEffect(HashMap<String, String> data) {

        deSerialize(data);
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

            for (String playerName : arena.getPlayers()) {
                current = Bukkit.getServer().getPlayer(playerName);
                applyEffects(current, arena);
            }
            break;
        case all_but_trigger:

            for (String playerName : arena.getPlayers()) {
                if (!playerName.equalsIgnoreCase(triggerPlayer.getName())) {
                    current = Bukkit.getServer().getPlayer(playerName);
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

    /**
     * Serializes this BlockEffect to a HashMap.
     * @return The HashMap
     */
    @SuppressWarnings("deprecation")
    public HashMap<String, String> serialize() {

        HashMap<String, String> result = new HashMap<String, String>();

        result.put("name", name);

        result.put("block", ((Integer) triggerBlock.getId()).toString());

        result.put("potion", potionEffect.getType().getName() + "," + potionEffect.getDuration());

        result.put("points", pointValue.toString());

        result.put("affects", affected.name());

        return result;
    }

    /**
     * De-serializes a HashMap.
     * @param data The HashMap
     */
    private void deSerialize(HashMap<String, String> data) {

        name = data.get("name");

        triggerBlock = Material.getMaterial(Integer.parseInt(data.get("block")));

        String[] split = data.get("potion").split(",");
        potionEffect = new PotionEffect(PotionEffectType.getByName(split[0]), Integer.parseInt(split[1]), 1);

        pointValue = Integer.parseInt(data.get("points"));

        affected = Target.valueOf(data.get("affects"));

        for (String string : data.keySet()) {
            Bukkit.getServer().getLogger().log(Level.INFO, data.get(string));
        }


    }
}
