package me.igwb.DigDug;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.CuboidRegion;

public class CommandListener implements CommandExecutor {

    private DigDug parent;

    /**
     * Instantiates the CommandListener.
     * @param parentPlugin The plugin that instantiated this class.
     */
    public CommandListener(DigDug parentPlugin) {
        parent = parentPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String firstArg, String[] args) {


        if (!(sender instanceof Player)) {
            sender.sendMessage("DigDug commands can only be performed by a player!");
            return true;
        }

        //Initialize the pSender variable
        Player pSender = (Player) sender;

        if (cmd.getName().toLowerCase().equals("dd") | cmd.getName().toLowerCase().equals("digdug")) {
            if (args != null && args.length >= 1) {
                switch (args[0].toLowerCase()) {
                case "arena":
                    if (args.length >= 3) {

                        //Initialize arenaName variable
                        String arenaName = args[1];

                        switch (args[2].toLowerCase()) {
                        case "create":

                            createArena(pSender, arenaName);
                            break;
                        case "delete":

                            deleteArena(pSender, arenaName);
                            break;
                        case "set":
                            switch (args[3]) {
                            case "digregion":

                                arenaSetDigregion(pSender, arenaName);
                                break;
                            case "playerspawn":

                                if (args.length >= 5) {
                                    setPlayerSpawn(pSender, arenaName, args[4]);
                                } else {
                                    pSender.sendMessage("Usage: digdug arena [name] set playerspawn [spawnname]");
                                }
                                break;
                            case "exitpoint":

                                setExitPoint(pSender, arenaName);
                                break;
                            default:
                                break;
                            }
                            break;
                        case "check":

                            checkArena(pSender, arenaName);
                            break;
                        default:
                            break;
                        }
                    } else {
                        sender.sendMessage("Usage: digdug arena [name] [create, delete, set]");
                    }
                    break;
                case "list":

                    listArenas(pSender);
                    break;
                default:
                    break;
                }
            } else {
                sender.sendMessage("Usage: digdug [arena, list]");
            }
        } else {
            return false;
        }
        return true;
    }


    /**
     * Sends a list of all known arenas to the Player supplied.
     * @param sender The player to notify.
     */
    private void listArenas(Player sender) {
        ArrayList<Arena> arenas = parent.getArenaManager().getArenas();
        String msg = "Known arenas: ";
        if (arenas != null | arenas.size() == 0) {
            for (Arena arena : arenas) {

                if (arena.isValid()) {
                    msg += ChatColor.GREEN + arena.getName() + " ";
                } else {
                    msg += ChatColor.RED + arena.getName() + " ";
                }
            }
            sender.sendMessage(msg);
        } else {
            sender.sendMessage("There are currently no arenas defined!");
            sender.sendMessage("Create on with /digdug arena create [name]");
        }
    }

    /**
     * Changes the dig region of an arena to the WorldEdit selection of the supplied Player.
     * @param sender The player to pull the WorldEdit-Selection from.
     * @param arenaName The Name of the arena.
     */
    private void arenaSetDigregion(Player sender, String arenaName) {

        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("The arena \"" + arenaName + "\" does not exist!");
            listArenas(sender);
            return;
        }

        //Check if the sender as selected a region
        if (parent.getWE().getSelection(sender) != null) {
            //Get the arena
            Arena ar = parent.getArenaManager().getArena(arenaName);
            try {
                ar.setRegionDig((CuboidRegion) parent.getWE().getSelection(sender).getRegionSelector().getRegion().clone());
            } catch (IncompleteRegionException e) {
                sender.sendMessage("The region you selected is invalid! (It must be a cuboid.)");
            }

            //Notify the sender about the creation progress
            sender.sendMessage("Dig region assigned successfuly! The following things are still missing:");
            for (String missing : parent.getArenaManager().getArena(arenaName).getMissing()) {
                sender.sendMessage(missing);
            }
        } else {
            sender.sendMessage("You did not select a region with WorldEdit!");
        }
    }

    /**
     * Checks if an Arena is valid and notifies the sender about it's status.
     * @param sender The Player to notify.
     * @param arenaName The Arena to check.
     */
    private void checkArena(Player sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("The arena \"" + arenaName + "\" does not exist!");
            listArenas(sender);
            return;
        }

        //Notify the sender
        if (parent.getArenaManager().getArena(arenaName).isValid()) {
            sender.sendMessage("The arena \"" + arenaName + "\" is valid and ready for use!");
        } else {
            sender.sendMessage("The following things are missing in arena \"" + arenaName + "\":");
            sendMissing(sender, arenaName);
        }

    }

    /**
     * Sends a list of missing things in an Arena to a Player.
     * @param sender The Player to notify.
     * @param arenaName The Arena to check.
     */
    private void sendMissing(Player sender, String arenaName) {
        //Check if the arena exists
        if (!parent.getArenaManager().exists(arenaName)) {
            return;
        }

        //Notify the player
        ArrayList<String> missing = parent.getArenaManager().getArena(arenaName).getMissing();
        if (missing != null) {
            for (String msg : missing) {
                sender.sendMessage(msg);
            }
        }
    }

    /**
     * Adds a player spawn to an Arena at the location of the Player sender.
     * @param sender Use the location from this player.
     * @param arenaName Add the spawn to this Arena.
     * @param spawnName The name of the player spawn.
     */
    private void setPlayerSpawn(Player sender, String arenaName, String spawnName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("The arena \"" + arenaName + "\" does not exist!");
            listArenas(sender);
            return;
        }

        //Try to add the spawn
        Location location = sender.getPlayer().getLocation();
        if (parent.getArenaManager().getArena(arenaName).addPlayerSpawn(spawnName, location)) {
            sender.sendMessage("Successfuly added playerspawn \"" + spawnName + "\" at X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ());
        } else {
            sender.sendMessage("Could not add \"" + spawnName + "\" at X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ() + " does it exist already?");
        }
    }

    /**
     * Sets the exit point of an Arena to the location of the Player sender.
     * @param sender The player to pull the location from.
     * @param arenaName The Arena to set the exit for.
     */
    private void setExitPoint(Player sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("The arena \"" + arenaName + "\" does not exist!");
            listArenas(sender);
            return;
        }

        //Try to add the exit point
        Location location = sender.getPlayer().getLocation();
        parent.getArenaManager().getArena(arenaName).setExitPoint(location);
        sender.sendMessage("Successfuly set exit point to X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ());

    }

    /**
     * Creates an arena on behalf of a Player using his WorldEdit selection.
     * @param sender The player to pull the WorldEdit-Selection from.
     * @param arenaName The Name for the new arena.
     */
    private void createArena(Player sender, String arenaName) {

        //Check if the arena exists already
        if (parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("An arena with the name \"" + arenaName + "\" already exists!");
            return;
        }

        //Check if the sender as selected a region
        if (parent.getWE().getSelection(sender) != null) {
            //Add the arena
            parent.getArenaManager().addArena(parent, arenaName);
            try {
                parent.getArenaManager().getArena(arenaName).setRegionArena((CuboidRegion) parent.getWE().getSelection(sender).getRegionSelector().getRegion().clone());
            } catch (IncompleteRegionException e) {
                sender.sendMessage("The region you selected is invalid! (It must be a cuboid)");
            }

            //Notify the sender about the creation progress
            sender.sendMessage("Arena created successfuly! The following things are still missing:");
            sendMissing(sender, arenaName);
        } else {
            sender.sendMessage("You did not select a region with WorldEdit!");
        }
    }

    /**
     *  Tries to delete an Arena by name.
     * @param sender The player to notify of the deletion progress.
     * @param arenaName The Arena to delete.
     */
    private void deleteArena(Player sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage("The arena \"" + arenaName + "\" does not exist!");
            listArenas(sender);
            return;
        }

        //Delete the Arena
        parent.getArenaManager().deleteArena(arenaName);
    }

}
