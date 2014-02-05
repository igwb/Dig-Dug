package me.igwb.DigDug;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventListener implements Listener {

    DigDug parent;

    /**
     * Creates an instance of EventListener.
     * @param parentPlugin The plugin instantiating this.
     */
    public EventListener(DigDug parentPlugin) {

        parent = parentPlugin;
    }

    /**
     * Called by bukkit in case of a BlockBreak event.
     * @param e The event.
     */
    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent e) {

        parent.getArenaManager().notifyArenasOfBlockBreak(e);
    }
}
