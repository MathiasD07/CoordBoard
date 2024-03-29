package fr.forky.coordboard.listeners;

import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerList playerList = PlayerList.getInstance();
        final Player player = event.getPlayer();

        // remove the player from the custom player list
        playerList.removePlayer(player);
        ScoreboardUtils.removePlayerFromAllScoreboard(player);
    }
}
