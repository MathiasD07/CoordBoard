package fr.forky.coordboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class PlayerConnection implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Location playerLocation = player.getLocation();

        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("general", "dummy", "Position");

        objective.setDisplayName(ChatColor.GOLD + "Position");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Score x = objective.getScore(ChatColor.GREEN + "X");
        final Score y = objective.getScore(ChatColor.GREEN + "Y");
        final Score z = objective.getScore(ChatColor.GREEN + "Z");

        x.setScore((int) playerLocation.getX());
        y.setScore((int) playerLocation.getY());
        z.setScore((int) playerLocation.getZ());

        player.setScoreboard(scoreboard);
    }
}
