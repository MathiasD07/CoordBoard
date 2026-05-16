package fr.forky.coordboard.listeners;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.maths.LocationMath;
import fr.forky.coordboard.utils.player.CoordManager;
import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        checkDestination(event.getPlayer());
        ScoreboardUtils.updateAllScoreboard();
    }

    private void checkDestination(Player player) {
        FileConfiguration customConfig = Main.getCustomConfig();
        CoordManager coordManager = Main.getCoordManager();
        String uuid = player.getUniqueId().toString();
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;

        if (!customConfig.contains(destinationPath)) return;

        String destinationWarpName = customConfig.getString(destinationPath);
        Location destinationLocation = coordManager.getCoord(player, destinationWarpName);

        if (destinationLocation == null) {
            coordManager.removeSpecialWarp(player, WarpType.DESTINATION);
            return;
        }

        if (destinationLocation.getWorld() != player.getWorld()) return;

        Location playerLocation = player.getLocation();
        double dist = LocationMath.vector2Distance(
                playerLocation.getX(), playerLocation.getZ(),
                destinationLocation.getX(), destinationLocation.getZ()
        );

        boolean arrived = dist <= 1.5 && Math.abs(playerLocation.getY() - destinationLocation.getY()) <= 2.0;

        if (arrived) {
            coordManager.removeSpecialWarp(player, WarpType.DESTINATION);
            if ("Mort".equals(destinationWarpName)) {
                coordManager.deleteCoord(player, "Mort");
            }
            player.sendMessage(Component.text("✔ Vous êtes arrivé à destination !").color(NamedTextColor.GREEN));
            ScoreboardUtils.updateAllScoreboard();
        }
    }
}
