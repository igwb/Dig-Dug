package me.igwb.DigDug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ArenaScoreboard {

    private Scoreboard board;
    private Objective objectivePoints;
    private Score score;

    private Player player;
    private Integer points;


    /**
     * Creates a new scoreboard for a specific player.
     * @param p The player.
     */
    public ArenaScoreboard(Player p) {

        player = p;
        points = 0;

        board = Bukkit.getScoreboardManager().getNewScoreboard();

        objectivePoints = board.registerNewObjective("points", "dummy");

        objectivePoints.setDisplaySlot(DisplaySlot.SIDEBAR);
        objectivePoints.setDisplayName(ChatColor.GREEN.toString() + ChatColor.UNDERLINE.toString() + "Points");

        score = objectivePoints.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString()));
        score.setScore(points);

        p.setScoreboard(board);
    }

    /**
     * Sets the score for a player.
     * @param newScore The score.
     */
    public void setScore(Integer newScore) {

        points = newScore;
        score.setScore(points);
    }

    /**
     * Removes the scoreboard from a player.
     */
    public void remove() {

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
