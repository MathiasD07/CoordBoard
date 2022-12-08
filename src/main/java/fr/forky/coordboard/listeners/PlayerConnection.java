package fr.forky.coordboard.listeners;

import fr.forky.coordboard.PlayerList;
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
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        PlayerList playerList = PlayerList.getInstance();
//
//        final Player player = event.getPlayer();
//        final Location playerLocation = player.getLocation();
//
//        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
//        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
//        final Objective objective = scoreboard.registerNewObjective(player.getName(), "dummy", player.getName());
//
//        objective.setDisplayName(ChatColor.GOLD + "Position de " + ChatColor.BOLD + player.getDisplayName());
//        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
//
//        final Score coord = objective.getScore(
//                ChatColor.GREEN + "X: " + (int) playerLocation.getX() +
//                        ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) playerLocation.getY() +
//                        ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) playerLocation.getZ()
//        );
//
//        coord.setScore(0);
//
//        player.setScoreboard(scoreboard);
//
//        // Add player to the custom player list
//        playerList.addPlayer(player);
//
//    }
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerList playerList = PlayerList.getInstance();

        final Player player = event.getPlayer();
        final Location playerLocation = player.getLocation();

        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective(player.getName(), "dummy");

        objective.setDisplayName(ChatColor.GOLD + "Friends Location");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final Score coord = objective.getScore(
                ChatColor.GOLD + player.getName() + ": " +
                ChatColor.GREEN + "X: " + (int) playerLocation.getX() +
                        ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) playerLocation.getY() +
                        ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) playerLocation.getZ()
        );

        coord.setScore(0);

        player.setScoreboard(scoreboard);

        // Add player to the custom player list
        playerList.addPlayer(player);

    }
}
