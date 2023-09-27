package fr.forky.coordboard.listeners;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.maths.LocationMath;
import fr.forky.coordboard.utils.player.CoordManager;
import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
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

    private void checkDestination(Player player)
    {
        FileConfiguration customConfig = Main.getCustomConfig();
        CoordManager coordManager = Main.getCoordManager();
        String uuid = player.getUniqueId().toString();

        String spacialWarpPath = uuid + "." + WarpType.DESTINATION.type;

        if (customConfig.contains(spacialWarpPath)) {
            String destinationWarpName = customConfig.getString(spacialWarpPath);
            Location playerLocation = player.getLocation();
            Location destinationLocation = coordManager.getCoord(player, destinationWarpName);

            double dist = LocationMath.vector2Distance(
                    playerLocation.getX(),
                    playerLocation.getZ(),
                    destinationLocation.getX(),
                    destinationLocation.getZ()
            );

            if (dist <= 1.5 && playerLocation.getY() == destinationLocation.getY()) {
                player.sendMessage("Vous êtes arrivé à destination !");
                coordManager.removeSpecialWarp(player, WarpType.DESTINATION);
            }
        }
    }
 }
