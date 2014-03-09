package me.igwb.DigDug.Commands;
import java.util.ArrayList;

import me.igwb.DigDug.Arena;
import me.igwb.DigDug.DigDug;
import me.igwb.DigDug.messages.Messages;

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
    private Messages msg;

    /**
     * Instantiates the CommandListener.
     * @param parentPlugin The plugin that instantiated this class.
     */
    public CommandListener(DigDug parentPlugin) {
        parent = parentPlugin;
        msg = parent.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String firstArg, String[] args) {

        /*
         *   /---- [arena, list, join]
         *   |         /---- [arenaName]
         *   |         |         /---- [create, delete, set, check, regen, reload]
         *   |         |         |         /---- [digregion, playerspawn, exitpoint]
         *   |         |         |         |         /---- other arguments
         *   |         |         |         |         |
         *   |         |         |         |         |
         * args[0] | args[1] | args[2] | args[3] | args[4] ...
         */

        if (!(sender instanceof Player)) {
            sender.sendMessage("DigDug commands can only be performed by a player!");
            return true;
        }

        //Initialize the pSender variable
        //        Player pSender = (Player) sender;

        String commandName = cmd.getName();

        //Check if the command is for digdug
        if (commandName.equals("dd") | commandName.equals("digdug")) {

            //Check if there were any arguments. Else return.
            if (args == null || args.length == 0) {
                sender.sendMessage(msg.getMsg("cmd_dd_usage"));
                return true;
            }

            String commandBase = args[0].toLowerCase();

            switch (commandBase) {
            case "arena":
                cmdArena(sender, args);
                break;
            case "list":

                cmdList(sender);
                break;
            case "join":
                if (args.length >= 2) {
                    cmdJoin(sender, args[1]);
                } else {
                    sender.sendMessage(msg.getMsg("cmd_join_usage"));
                }
                break;
            default:
                sender.sendMessage(msg.getMsg("cmd_dd_usage"));
                break;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Execute a /digdug arena * command.
     * @param sender The CommandSender
     * @param args The arguments.
     */
    private void cmdArena(CommandSender sender, String[] args) {

        //Check if there are enough arguments
        if (args.length < 3) {
            sender.sendMessage(msg.getMsg("cmd_arena_usage"));
            return;
        }

        //Initialize arenaName variable
        String arenaName = args[1];

        switch (args[2].toLowerCase()) {
        case "create":

            cmdArenaCreate(sender, arenaName);
            break;
        case "delete":

            cmdArenaDelete(sender, arenaName);
            break;
        case "set":
            cmdArenaSet(sender, arenaName, args);
            break;
        case "check":

            cmdArenaCheck(sender, arenaName);
            break;
        case "regen":

            cmdArenaRegen(sender, arenaName);
            break;
        case "reload":

            cmdArenaReload(sender, arenaName);
            break;
        default:

            sender.sendMessage(msg.getMsg("cmd_arena_usage"));
            break;
        }

    }

    /**
     * Creates an arena on behalf of a Player using his WorldEdit selection.
     * @param sender The player to pull the WorldEdit-Selection from.
     * @param arenaName The Name for the new arena.
     */
    private void cmdArenaCreate(CommandSender sender, String arenaName) {

        //Check if the sender is a player. Else return.
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMsg("player_only"));
            return;
        }

        Player pSender = (Player) sender;

        //Check if the arena exists already
        if (parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_exists").replace("%arena%", arenaName));
            return;
        }

        //Check if the sender as selected a region
        if (parent.getWE().getSelection(pSender) != null) {
            //Add the arena
            parent.getArenaManager().addArena(parent, arenaName);
            try {
                parent.getArenaManager().getArena(arenaName).setRegionArena((CuboidRegion) parent.getWE().getSelection(pSender).getRegionSelector().getRegion().clone());
            } catch (IncompleteRegionException e) {
                sender.sendMessage(msg.getMsg("arena_region_invalid"));
            }

            //Notify the sender about the creation progress
            sender.sendMessage(msg.getMsg("cmd_arena_create_success"));
            if (!parent.getArenaManager().getArena(arenaName).isValid()) {
                sendMissing(sender, arenaName);
            }
        } else {
            sender.sendMessage(msg.getMsg("arena_region_notselected"));
        }
    }

    /**
     *  Tries to delete an Arena by name.
     * @param sender The player to notify of the deletion progress.
     * @param arenaName The Arena to delete.
     */
    private void cmdArenaDelete(CommandSender sender, String arenaName) {

        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        //Delete the Arena
        parent.getArenaManager().deleteArena(arenaName);
    }

    /**
     * Executes "the arena set" command.
     * @param sender The CommandSender.
     * @param arenaName The arena affected.
     * @param args Some arguments.
     */
    private void cmdArenaSet(CommandSender sender, String arenaName, String[] args) {
        switch (args[3]) {
        case "digregion":

            cmdArenaSetDigregion(sender, arenaName);
            break;
        case "playerspawn":

            if (args.length >= 5) {
                cmdArenaSetPlayerspawn(sender, arenaName, args[4]);
            } else {
                sender.sendMessage(msg.getMsg("cmd_arena_set_playerspawn_usage"));
            }
            break;
        case "exitpoint":

            cmdArenaSetExitpoint(sender, arenaName);
            break;
        default:
            sender.sendMessage(msg.getMsg("cmd_arena_set_usage"));
            break;
        }
    }

    /**
     * Changes the dig region of an arena to the WorldEdit selection of the supplied Player.
     * @param sender The player to pull the WorldEdit-Selection from.
     * @param arenaName The Name of the arena.
     */
    private void cmdArenaSetDigregion(CommandSender sender, String arenaName) {
        //Check if the sender is a player. Else return.
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMsg("player_only"));
            return;
        }

        Player pSender = (Player) sender;

        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        //Check if the sender as selected a region
        if (parent.getWE().getSelection(pSender) != null) {
            //Get the arena
            Arena ar = parent.getArenaManager().getArena(arenaName);
            try {
                ar.setRegionDig((CuboidRegion) parent.getWE().getSelection(pSender).getRegionSelector().getRegion().clone());
            } catch (IncompleteRegionException e) {
                sender.sendMessage(msg.getMsg("arena_region_invalid"));
            }

            //Notify the sender about the creation progress
            sender.sendMessage(msg.getMsg("cmd_arena_set_digregion_success"));
            if (!parent.getArenaManager().getArena(arenaName).isValid()) {
                sendMissing(sender, arenaName);
            }

        } else {
            sender.sendMessage(msg.getMsg("arena_region_notselected"));
        }
    }

    /**
     * Adds a player spawn to an Arena at the location of the Player sender.
     * @param sender Use the location from this player.
     * @param arenaName Add the spawn to this Arena.
     * @param spawnName The name of the player spawn.
     */
    private void cmdArenaSetPlayerspawn(CommandSender sender, String arenaName, String spawnName) {
        //Check if the sender is a player. Else return.
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMsg("player_only"));
            return;
        }

        Player pSender = (Player) sender;

        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        //Try to add the spawn
        Location location = pSender.getPlayer().getLocation();
        if (parent.getArenaManager().getArena(arenaName).addPlayerSpawn(spawnName, location)) {
            sender.sendMessage(msg.getMsg("cmd_arena_set_playerspawn_success").replace("%x%", Integer.toString(location.getBlockX())).replace("%y%", Integer.toString(location.getBlockY())).replace("%z%", Integer.toString(location.getBlockZ())));
        } else {
            sender.sendMessage(msg.getMsg("cmd_arena_set_playerspawn_failure").replace("%x%", Integer.toString(location.getBlockX())).replace("%y%", Integer.toString(location.getBlockY())).replace("%z%", Integer.toString(location.getBlockZ())));
        }
    }

    /**
     * Sets the exit point of an Arena to the location of the Player sender.
     * @param sender The player to pull the location from.
     * @param arenaName The Arena to set the exit for.
     */
    private void cmdArenaSetExitpoint(CommandSender sender, String arenaName) {
        //Check if the sender is a player. Else return.
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMsg("player_only"));
            return;
        }

        Player pSender = (Player) sender;

        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        //Try to add the exit point
        Location location = pSender.getPlayer().getLocation();
        parent.getArenaManager().getArena(arenaName).setExitPoint(location);
        sender.sendMessage(msg.getMsg("cmd_arena_set_exitpoint_success").replace("%x%", Integer.toString(location.getBlockX())).replace("%y%", Integer.toString(location.getBlockY())).replace("%z%", Integer.toString(location.getBlockZ())));
    }

    /**
     * Checks if an Arena is valid and notifies the sender about it's status.
     * @param sender The Player to notify.
     * @param arenaName The Arena to check.
     */
    private void cmdArenaCheck(CommandSender sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        //Notify the sender
        if (parent.getArenaManager().getArena(arenaName).isValid()) {
            sender.sendMessage(msg.getMsg("arena_valid").replace("%arena%", arenaName));
        } else {
            sender.sendMessage(msg.getMsg("arena_invalid").replace("%arena%", arenaName));
            sendMissing(sender, arenaName);
        }
    }

    /**
     * Regenerates the digregion of an arena.
     * @param sender The player that sent the request.
     * @param arenaName The arena affected.
     */
    private void cmdArenaRegen(CommandSender sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        parent.getArenaManager().getArena(arenaName).regenerateDigRegion();
        sender.sendMessage(msg.getMsg("cmd_arena_regen_success"));
    }

    /**
     * Reloads the configuration of an arena.
     * @param sender The command sender.
     * @param arenaName The arena affected.
     */
    private void cmdArenaReload(CommandSender sender, String arenaName) {
        //Check if the arena exists.
        if (!parent.getArenaManager().exists(arenaName)) {
            sender.sendMessage(msg.getMsg("arena_noexist").replace("%arena%", arenaName));
            cmdList(sender);
            return;
        }

        parent.getArenaManager().getArena(arenaName).reloadArenaConfig();
        parent.getArenaManager().getArena(arenaName).load();

        sender.sendMessage(msg.getMsg("cmd_arena_reload_success").replace("%arena", arenaName));
    }

    /**
     * Sends a list of all known arenas to the Player supplied.
     * @param sender The player to notify.
     */
    private void cmdList(CommandSender sender) {
        ArrayList<Arena> arenas = parent.getArenaManager().getArenas();
        String lst = msg.getMsg("cmd_list_knownarenas");
        if (arenas != null | arenas.size() == 0) {
            for (Arena arena : arenas) {

                if (arena.isValid()) {
                    lst += ChatColor.GREEN + arena.getName() + " ";
                } else {
                    lst += ChatColor.RED + arena.getName() + " ";
                }
            }
            sender.sendMessage(lst);
        } else {
            sender.sendMessage(msg.getMsg("cmd_list_knownarenas"));
            sender.sendMessage(msg.getMsg("cmd_list_noarenas"));
        }
    }

    /**
     * Let's a player join an arena.
     * @param sender The CommandSender
     * @param arenaName The arena.
     */
    private void cmdJoin(CommandSender sender, String arenaName) {
        //Check if the sender is a player. Else return.
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getMsg("player_only"));
            return;
        }

        Player pSender = (Player) sender;

        parent.getArenaManager().getArena(arenaName).joinPlayer(pSender.getName());
    }

    /**
     * Sends a list of missing things in an Arena to a Player.
     * @param sender The Player to notify.
     * @param arenaName The Arena to check.
     */
    private void sendMissing(CommandSender sender, String arenaName) {
        //Check if the arena exists
        if (!parent.getArenaManager().exists(arenaName)) {
            return;
        }

        //Notify the player
        sender.sendMessage(msg.getMsg("arena_invalid").replace("%arena%", arenaName));

        ArrayList<String> missing = parent.getArenaManager().getArena(arenaName).getMissing();
        if (missing != null) {
            for (String mis : missing) {
                sender.sendMessage(mis);
            }
        }
    }


}
