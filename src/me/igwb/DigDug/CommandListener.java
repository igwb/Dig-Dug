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
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {


        if (!(arg0 instanceof Player)) {
            arg0.sendMessage("DigDug commands can only be performed by a player!");
            return true;
        }

        if (arg1.getName().toLowerCase().equals("dd") | arg1.getName().toLowerCase().equals("digdug")) {
            if (arg3 != null && arg3.length >= 1) {
                switch (arg3[0].toLowerCase()) {

                case "arena":
                    if (arg3.length >= 3) {
                        switch (arg3[1].toLowerCase()) {
                        case "create":
                            if (parent.getWE().getSelection((Player) arg0) != null) {
                                if (parent.getArenaManager().exists(arg3[2])) {
                                    arg0.sendMessage("An arena with the name \"" + arg3[2] + "\" already exists!");
                                } else {
                                    parent.getArenaManager().addArena(arg3[2]);
                                    try {
                                        parent.getArenaManager().getArena(arg3[2]).setRegionArena((CuboidRegion) parent.getWE().getSelection((Player) arg0).getRegionSelector().getRegion());
                                    } catch (IncompleteRegionException e) {
                                        arg0.sendMessage("The region you selected is invalid! It must be a cuboid!");
                                    }
                                }
                            } else {
                                arg0.sendMessage("You did not select an area!");
                            }
                            break;
                        case "delete":
                            break;
                        case "set":

                            break;
                        case "check":

                            break;
                        default:
                            arg0.sendMessage("Usage: digdug arena [name] [create, delete, set, check]");
                            break;
                        }
                    } else {
                        arg0.sendMessage("Usage: digdug arena [create, delete] [name]");
                    }
                    break;
                case "list":
                    ArrayList<Arena> arenas = parent.getArenaManager().getArenas();
                    String msg = "Known arenas: ";
                    if (arenas != null | arenas.size() == 0) {
                        for (Arena arena : arenas) {
                            msg += arena.getName() + " ";
                        }
                        arg0.sendMessage(msg);
                    } else {
                        arg0.sendMessage("There are currently no arenas defined!");
                        arg0.sendMessage("Create on with /digdug arena create [name]");
                    }

                    break;
                default:
                    break;
                }
            } else {
                arg0.sendMessage("Missing parameters!");
            }
        } else {
            return false;
        }
        return true;
    }

}
