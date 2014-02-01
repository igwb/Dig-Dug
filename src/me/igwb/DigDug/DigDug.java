package me.igwb.DigDug;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class DigDug extends JavaPlugin {


    private WorldEditPlugin worldEditPlugin;
    private CommandListener myCommandListener;
    private ArenaManager myArenaManager;

    /**
     * Called by bukkit.
     */
    public void onEnable() {

        worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");

        if (worldEditPlugin != null) {
            getLogger().log(Level.INFO, "WorldEdit found!");
        } else {
            getLogger().log(Level.SEVERE, "WorldEdit not found! Disabeling...");
            getServer().getPluginManager().disablePlugin(this);
        }

        myCommandListener = new CommandListener(this);
        registerCommands();

        myArenaManager = new ArenaManager();
    }

    /**
     * Registers the commands with bukkit.
     */
    private void registerCommands() {
        this.getCommand("digdug").setExecutor(myCommandListener);
        this.getCommand("dd").setExecutor(myCommandListener);
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

}
