package me.igwb.DigDug;
import java.util.ArrayList;

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
                        String arenaName = args[2];

                        switch (args[1].toLowerCase()) {
                        case "create":

                            createArena(pSender, arenaName);
                            break;
                        case "delete":
                            break;
                        case "set":
                            switch (args[3]) {
                            case "digregion":

                                arenaSetDigregion(pSender, arenaName);
                                break;
                            case "playerspawn":

                                break;
                            default:
                                break;
                            }
                            break;
                        case "check":
                            break;

                        default:
                            break;
                        }
                    } else {
                        sender.sendMessage("Usage: digdug arena [create, delete] [name]");
                    }

                case "list":

                    listArenas(pSender);
                    break;
                default:
                    break;
                }
            } else {
                sender.sendMessage("Missing parameters!");
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
                msg += arena.getName() + " ";
            }
            sender.sendMessage(msg);
        } else {
            sender.sendMessage("There are currently no arenas defined!");
            sender.sendMessage("Create on with /digdug arena create [name]");
        }
    }

    /**
     * Changes the digregion of an arena to the WorldEdit selection of the supplied Player.
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
                ar.setRegionDig((CuboidRegion) parent.getWE().getSelection(sender).getRegionSelector().getRegion());
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
            parent.getArenaManager().addArena(arenaName);
            try {
                parent.getArenaManager().getArena(arenaName).setRegionArena((CuboidRegion) parent.getWE().getSelection(sender).getRegionSelector().getRegion());
            } catch (IncompleteRegionException e) {
                sender.sendMessage("The region you selected is invalid! (It must be a cuboid)");
            }

            //Notify the sender about the creation progress
            sender.sendMessage("Arena created successfuly! The following things are still missing:");
            for (String missing : parent.getArenaManager().getArena(arenaName).getMissing()) {
                sender.sendMessage(missing);
            }
        } else {
            sender.sendMessage("You did not select a region with WorldEdit!");
        }
    }
}
