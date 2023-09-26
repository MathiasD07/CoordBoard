package fr.forky.coordboard.listeners;

import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityRegainHeath implements Listener {

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            ScoreboardUtils.updateAllScoreboard();
        }
    }
 }
