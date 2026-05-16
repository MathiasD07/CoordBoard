package fr.forky.coordboard.listeners;

import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scoreboard.*;

public class PlayerChangeWorld implements Listener {
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("general", Criteria.DUMMY,
                Component.text("Friends Location").color(NamedTextColor.GOLD));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);

        ScoreboardUtils.updateAllScoreboard();
    }
}
