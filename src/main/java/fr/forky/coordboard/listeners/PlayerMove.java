package fr.forky.coordboard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location playerLocation = player.getLocation();

        final Scoreboard scoreboard = player.getScoreboard();

        final Objective objective = scoreboard.getObjective(player.getName());
        assert objective != null;
        objective.unregister();

        final Objective newObjective = scoreboard.registerNewObjective(player.getName(), "dummy", player.getName());
        newObjective.setDisplayName(ChatColor.GOLD + "Position de " + ChatColor.BOLD + player.getDisplayName());
        newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Score coord = newObjective.getScore(
                ChatColor.GREEN + "X: " + (int) playerLocation.getX() +
                        ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) playerLocation.getY() +
                        ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) playerLocation.getZ()
        );

        coord.setScore(0);

        player.setScoreboard(scoreboard);
    }
}
