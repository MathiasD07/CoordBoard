package fr.forky.coordboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final Scoreboard scoreboard = player.getScoreboard();

        final Objective objective = scoreboard.getObjective("general");

        assert objective != null;
        final Score x = objective.getScore(ChatColor.GREEN + "X");
        final Score y = objective.getScore(ChatColor.GREEN + "Y");
        final Score z = objective.getScore(ChatColor.GREEN + "Z");

        x.setScore((int) player.getLocation().getX());
        y.setScore((int) player.getLocation().getY());
        z.setScore((int) player.getLocation().getZ());

        player.setScoreboard(scoreboard);
    }
}
