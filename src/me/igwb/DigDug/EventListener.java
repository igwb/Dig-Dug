package me.igwb.DigDug;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {

    private DigDug parent;

    /**
     * Creates an instance of EventListener.
     * @param parentPlugin The plugin instantiating this.
     */
    public EventListener(DigDug parentPlugin) {

        parent = parentPlugin;
    }

    /**
     * Called by bukkit in case of a BlockBreakEvent.
     * @param e The event.
     */
    @EventHandler
    public void onBlockBreakEvent(final BlockBreakEvent e) {

        parent.getArenaManager().notifyArenasOfBlockBreak(e);
    }

    /**
     * Called by bukkit in case of a PlayerMoveEvent.
     * @param e The event.
     */
    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent e) {

        parent.getArenaManager().notifyArenasOfPlayerMove(e);
    }
}
