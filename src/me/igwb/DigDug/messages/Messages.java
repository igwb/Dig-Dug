package me.igwb.DigDug.messages;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import me.igwb.DigDug.DigDug;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {

    //Configurations
    private FileConfiguration msgConfig = null;
    private File msgConfigFile = null;

    private HashMap<String, String> msg;

    @SuppressWarnings("unchecked")
    public Messages() {

        msg = (HashMap<String, String>) getMsgConfig().getConfigurationSection("Messages");
    }

    public String getMsg(String key) {

        return msg.get(key);
    }

    /**
     * Gets the FileConfiguration for the messages.
     * @return The configuration
     */
    public FileConfiguration getMsgConfig() {
        if (msgConfig == null) {
            reloadMsgConfig();
        }
        return msgConfig;
    }

    /**
     * Saves the default configuration to file if it doesn't exist already.
     */
    public void saveDefaultConfig() {
        DigDug pl = (DigDug) Bukkit.getServer().getPluginManager().getPlugin("DigDug");

        if (msgConfigFile == null) {
            msgConfigFile = new File(pl.getDataFolder().getAbsolutePath() + "\\messages.yml");
        }
        if (!msgConfigFile.exists()) {
            pl.saveResource("arenas\\arenaConfig.yml", false);
            File temp = new File(pl.getDataFolder().getAbsolutePath() + "\\messages.yml");
            temp.renameTo(new File(pl.getDataFolder().getAbsolutePath() + "\\messages.yml"));
        }
    }

    /**
     * Reloads the messages from File.
     */
    public void reloadMsgConfig() {
        DigDug pl = (DigDug) Bukkit.getServer().getPluginManager().getPlugin("DigDug");

        if (msgConfigFile == null) {
            msgConfigFile = new File(pl.getDataFolder(), "\\messages.yml");
        }
        msgConfig = YamlConfiguration.loadConfiguration(msgConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = pl.getResource("\\messages\\messages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            msgConfig.setDefaults(defConfig);
        }
    }
}
