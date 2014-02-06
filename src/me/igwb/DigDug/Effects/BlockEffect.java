package me.igwb.DigDug.Effects;

import java.util.HashMap;
import me.igwb.DigDug.Arena;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class BlockEffect {

    protected Integer id, dataValue;
    protected Material triggerBlock;
    protected Target affected;
    protected EffectType type;

    public enum Target {
        all,
        all_but_trigger,
        trigger;
    }

    public enum EffectType {
        score,
        potion;
    }

    /**
     * Creates a new BlockEffect.
     * @param trigger The Block type triggering this event.
     * @param blockDataValue The data value of the trigger block.
     * @param effectId An id unique to this instance of a BlockEffect.
     * @param targets The Players affected.
     */
    public BlockEffect(Material trigger, Integer blockDataValue, Integer effectId, BlockEffect.Target targets) {

        triggerBlock = trigger;
        dataValue = blockDataValue;
        id = effectId;
        affected = targets;
    }

    /**
     * Creates a new BlockEffect from a SerializedBlockEffect.
     * @param serializedEffect The effect.
     */
    public BlockEffect(SerializedBlockEffect serializedEffect) {

        HashMap<String, String> data = serializedEffect.getData();

        id = serializedEffect.getId();
        dataValue = serializedEffect.getBlockDataValue();
        triggerBlock = serializedEffect.getBlockType();
        affected = Target.valueOf(data.get("affects"));
    }

    /**
     * Returns the Material that triggers this effect.
     * @return The Material.
     */
    public final Material getTriggerBlock() {

        return triggerBlock;
    }

    /**
     * Returns the data value of the trigger block.
     * @return The data value.
     */
    public final Integer getDataValue() {

        return dataValue;
    }

    /**
     * Executes this BlockEffect.
     * @param triggerPlayer The player who triggered the execution.
     * @param arena The arena the effect should take place in.
     */
    public final void execute(Player triggerPlayer, Arena arena) {

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
    protected abstract void applyEffects(Player p, Arena arena);

    /**
     * Serializes this BlockEffect to a HashMap.
     * @return The HashMap
     */
    public SerializedBlockEffect serialize() {

        HashMap<String, String> data = new HashMap<String, String>();

        data.put("affects", affected.name());
        return new SerializedBlockEffect(triggerBlock, dataValue, id, data);
    }
}
