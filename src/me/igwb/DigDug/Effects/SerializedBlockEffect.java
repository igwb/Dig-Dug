package me.igwb.DigDug.Effects;

import java.util.HashMap;

import org.bukkit.Material;

public class SerializedBlockEffect {

    private final Material blockType;
    private final Integer id, dataValue;
    private HashMap<String, String> data;

    /**
     * Constructs a new serialized BlockEffect.
     * @param type The block causing this effect.
     * @param blockDataValue The data value of the trigger block.
     * @param effectId An id unique to this instance of a (Serialized)BlockEffect.
     * @param hmData The data.
     */
    public SerializedBlockEffect(Material type, Integer blockDataValue, Integer effectId, HashMap<String, String> hmData) {

        blockType = type;
        dataValue = blockDataValue;
        id = effectId;
        data = hmData;
    }

    /**
     * Returns the type of the block that triggers this effect.
     * @return The type.
     */
    public Material getBlockType() {

        return blockType;
    }

    /**
     * Returns an id unique to this instance of a BlockEffect.
     * @return The id.
     */
    public Integer getId() {

        return id;
    }

    /**
     * Returns the data value of the trigger block.
     * @return The data value.
     */
    public Integer getBlockDataValue() {

        return dataValue;
    }

    /**
     * Returns a HashMap<String, String> containing the data specific to the effect.
     * @return The HashMap.
     */
    public HashMap<String, String> getData() {

        return data;
    }

    /**
     * Adds data to the HashMap<String, String>.
     * @param key Key for the HashMap.
     * @param value Value for the HashMap.
     */
    public void addData(String key, String value) {

        data.put(key, value);
    }

}
