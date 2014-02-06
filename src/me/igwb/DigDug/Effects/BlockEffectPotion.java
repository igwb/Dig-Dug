package me.igwb.DigDug.Effects;

import java.util.HashMap;
import me.igwb.DigDug.Arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockEffectPotion extends BlockEffect {

    private PotionEffect potionEffect;
    private Integer maxDuration;

    /**
     * Creates a new BlockEffect.
     * @param trigger The Block type triggering this event.
     * @param blockDataValue The data value of the trigger block.
     * @param effectId An id unique to this instance of a BlockEffect.
     * @param targets The Players affected.
     * @param effect The potion effect to be applied
     * @param maxTime The maximum duration this effect can get by stacking. (If the effect is called multiple times).
     */
    public BlockEffectPotion(Material trigger, Integer blockDataValue, Integer effectId, Target targets, PotionEffect effect, Integer maxTime) {

        super(trigger, blockDataValue, effectId, targets);

        potionEffect = effect;
        //Multiply by 20 to match ticks rather than seconds
        maxDuration = maxTime * 20;
    }

    /**
     * Creates a new BlockEffect from a SerializedBlockEffect.
     * @param serializedEffect The effect.
     */
    public BlockEffectPotion(SerializedBlockEffect serializedEffect) {

        super(serializedEffect);

        HashMap<String, String> data = serializedEffect.getData();

        //Multiply duration with 20 to match ticks, rather than seconds.
        potionEffect = new PotionEffect(PotionEffectType.getByName(data.get("effect")), Integer.parseInt(data.get("duration")) * 20, Integer.parseInt(data.get("amplifier")));

        //Multiply duration with 20 to match ticks, rather than seconds.
        maxDuration = Integer.parseInt(data.get("maxDuration")) * 20;
    }

    /**
     * Applies the effects to a Player.
     * @param p The player affected.
     * @param arena The arena the player is in.
     */
    @Override
    protected void applyEffects(Player p, Arena arena) {

        //Check if the player already has this effect applied and extends it's duration if possible.
        PotionEffect[] active = p.getActivePotionEffects().toArray(new PotionEffect[p.getActivePotionEffects().size()]);
        PotionEffect newEffect;

        for (PotionEffect eff : active) {
            if (eff.getType().equals(potionEffect.getType())) {
                newEffect = new PotionEffect(potionEffect.getType(), Math.min(eff.getDuration() + potionEffect.getDuration(), maxDuration), potionEffect.getAmplifier());
                p.addPotionEffect(newEffect, true);
            }
            return;
        }

        //Add the effect to the player if he doesn't have it already.
        p.addPotionEffect(potionEffect, true);
    }

    /**
     * Serializes this BlockEffect.
     * @return A SerializedBlockEffect.
     */
    public SerializedBlockEffect serialize() {

        SerializedBlockEffect result;

        result = super.serialize();

        result.addData("effect", potionEffect.getType().getName());
        //Divide by 20 to match seconds rather than ticks.
        result.addData("duration", ((Integer) (potionEffect.getDuration() / 20)).toString());
        //Divide by 20 to match seconds rather than ticks.
        result.addData("maxDuration", ((Integer) (maxDuration / 20)).toString());
        result.addData("amplifier", ((Integer) potionEffect.getAmplifier()).toString());

        return result;
    }
}
