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

        if (cmd.getName().toLowerCase().equals("dd") | cmd.getName().toLowerCase().equals("digdug")) {
            if (args != null && args.length >= 1) {
                switch (args[0].toLowerCase()) {

                case "arena":
                    if (args.length >= 3) {
                        switch (args[1].toLowerCase()) {
                        case "create":
                            //Check if the arena exists already
                            if (parent.getArenaManager().exists(args[2])) {
                                sender.sendMessage("An arena with the name \"" + args[2] + "\" already exists!");
                            } else {

                                //Check if the sender as selected a region
                                if (parent.getWE().getSelection((Player) sender) != null) {
                                    //Add the arena
                                    parent.getArenaManager().addArena(args[2]);
                                    try {
                                        parent.getArenaManager().getArena(args[2]).setRegionArena((CuboidRegion) parent.getWE().getSelection((Player) sender).getRegionSelector().getRegion());
                                    } catch (IncompleteRegionException e) {
                                        sender.sendMessage("The region you selected is invalid! (It must be a cuboid)");
                                    }

                                    //Notify the sender about the creation progress
                                    sender.sendMessage("Arena created successfuly! The following things are still missing:");
                                    for (String missing : parent.getArenaManager().getArena(args[2]).getMissing()) {
                                        sender.sendMessage(missing);
                                    }
                                } else {
                                    sender.sendMessage("You did not select a region with WorldEdit!");
                                }
                            }
                            break;
                        case "delete":
                            break;
                        case "set":
                            if (parent.getArenaManager().exists(args[2])) {
                                switch (args[3]) {
                                case "digregion":
                                  //Check if the sender as selected a region
                                    if (parent.getWE().getSelection((Player) sender) != null) {
                                        //Get the arena
                                        Arena ar = parent.getArenaManager().getArena(args[2]);
                                        try {
                                            ar.setRegionDig((CuboidRegion) parent.getWE().getSelection((Player) sender).getRegionSelector().getRegion());
                                        } catch (IncompleteRegionException e) {
                                            sender.sendMessage("The region you selected is invalid! (It must be a cuboid.)");
                                        }

                                        //Notify the sender about the creation progress
                                        sender.sendMessage("Dig region assigned successfuly! The following things are still missing:");
                                        for (String missing : parent.getArenaManager().getArena(args[2]).getMissing()) {
                                            sender.sendMessage(missing);
                                        }
                                    } else {
                                        sender.sendMessage("You did not select a region with WorldEdit!");
                                    }
                                    break;
                                default:
                                    break;
                                }
                            } else {
                                sender.sendMessage("The arena \"" + args[2] + "\" does not exist!");
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

}
