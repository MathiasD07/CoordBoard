package fr.forky.coordboard.listeners;

import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ScoreboardUtils.updateAllScoreboard();
    }
 }
