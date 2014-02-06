package me.igwb.DigDug.Effects;

import java.util.HashMap;
import me.igwb.DigDug.Arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;


public class BlockEffectScore extends BlockEffect {

    private Integer score;

    /**
     * Creates a new BlockEffect.
     * @param trigger The Block type triggering this event.
     * @param blockDataValue The data value of the trigger block.
     * @param effectId An id unique to this instance of a BlockEffect.
     * @param targets The Players affected.
     * @param points The Points to give the affected players. May be negative.
     */
    public BlockEffectScore(Material trigger, Integer blockDataValue, Integer effectId, Target targets, Integer points) {

        super(trigger, blockDataValue, effectId, targets);

        score = points;
    }

    /**
     * Creates a new BlockEffect from a SerializedBlockEffect.
     * @param serializedEffect The effect.
     */
    public BlockEffectScore(SerializedBlockEffect serializedEffect) {

        super(serializedEffect);

        HashMap<String, String> data = serializedEffect.getData();

        score = Integer.parseInt(data.get("score"));
    }

    /**
     * Applies the effects to a Player.
     * @param p The player affected.
     * @param arena The arena the player is in.
     */
    @Override
    protected void applyEffects(Player p, Arena arena) {

        arena.modifyPlayerPoints(p.getName(), score);
    }

    /**
     * Serializes this BlockEffect.
     * @return A SerializedBlockEffect.
     */
    public SerializedBlockEffect serialize() {

        SerializedBlockEffect result;

        result = super.serialize();
        result.addData("score", score.toString());

        return result;
    }
}
