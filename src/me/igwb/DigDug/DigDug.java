package me.igwb.DigDug;

import java.util.logging.Level;

import me.igwb.DigDug.Commands.CommandListener;
import me.igwb.DigDug.messages.Messages;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class DigDug extends JavaPlugin {


    private WorldEditPlugin worldEditPlugin;
    private CommandListener myCommandListener;
    private EventListener eventListener;
    private ArenaManager myArenaManager;
    private Messages myMessages;

    /**
     * Called by Bukkit.
     */
    public void onEnable() {

        myMessages = new Messages();

        worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");

        if (worldEditPlugin != null) {
            getLogger().log(Level.INFO, "WorldEdit found!");
        } else {
            getLogger().log(Level.SEVERE, "WorldEdit not found! Disabeling...");
            getServer().getPluginManager().disablePlugin(this);
        }

        myCommandListener = new CommandListener(this);
        registerCommands();

        eventListener = new EventListener(this);
        registerEvents();

        myArenaManager = new ArenaManager();
        myArenaManager.loadArenas(this);


    }

    /**
     * Called by Bukkit.
     */
    public void onDisable() {
        myArenaManager.saveArenas();
    }

    /**
     * Registers the commands with bukkit.
     */
    private void registerCommands() {
        this.getCommand("digdug").setExecutor(myCommandListener);
        this.getCommand("dd").setExecutor(myCommandListener);
    }

    /**
     * Registers the events with bukkit.
     */
    private void registerEvents() {

        getServer().getPluginManager().registerEvents(eventListener, this);
    }

    /**
     * Returns an instance of WorldEdit.
     * @return WorldEdit
     */
    public WorldEditPlugin getWE() {
        return worldEditPlugin;
    }

    /**
     * Returns an instance of the currently used ArenaManager.
     * @return ArenaManager
     */
    public ArenaManager getArenaManager() {
        return myArenaManager;
    }

    public Messages getMessages() {

        return myMessages;
    }
}
