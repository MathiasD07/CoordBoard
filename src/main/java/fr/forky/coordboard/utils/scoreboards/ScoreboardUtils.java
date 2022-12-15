package fr.forky.coordboard.utils.scoreboards;

import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.enums.ArrowDirection;
import fr.forky.coordboard.utils.maths.LocationMath;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardUtils {
    static public void setCurrentPlayerScoreCoordinate(Objective objective, Location playerLocation) {
        final Score scoreTitle = objective.getScore(
                ChatColor.GOLD + "X/Y/Z: " +
                        ChatColor.GREEN + (int) playerLocation.getX() +
                        ChatColor.WHITE + " / " + ChatColor.AQUA + (int) playerLocation.getY() +
                        ChatColor.WHITE + " / " + ChatColor.YELLOW + (int) playerLocation.getZ()
        );
        scoreTitle.setScore(1);
    }

    static public void setForeignPlayerScoreCoordinate(Objective objective, Player player, double angle, double dist) {
        final Score scoreTitle = objective.getScore(
                ChatColor.GOLD + player.getName() +
                        ChatColor.GOLD + "(" + ChatColor.AQUA +
                        (int) player.getLocation().getY() + ChatColor.GOLD + ")" + ": " +
                        ChatColor.GREEN + (int) dist + " blocks" +
                        "  " + getArrowDirection(angle)
        );
        scoreTitle.setScore(0);
    }

    static public void setOtherWorldPlayerCoordinate(Objective objective, Player player) {
        final Score scoreTitle = objective.getScore(
                ChatColor.GOLD + player.getName() +
                        ChatColor.GOLD + "(" + ChatColor.AQUA +
                        ChatColor.MAGIC + "00" + ChatColor.GOLD + ")" + ": " +
                        ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " blocks" +
                        "  " + ChatColor.MAGIC + "00"
        );
        scoreTitle.setScore(0);
    }

    static private String getArrowDirection(double angle) {
        if (angle < 22.5 || angle > 337.5) {
            return ArrowDirection.UP.arrow;
        } else if (angle > 67.5 && angle < 112.5) {
            return ArrowDirection.RIGHT.arrow;
        } else if (angle > 157.5 && angle < 202.5) {
            return ArrowDirection.DOWN.arrow;
        } else if (angle > 247.5 && angle < 292.5) {
            return ArrowDirection.LEFT.arrow;
        } else if (angle >= 112.5 && angle <= 157.5) {
            return ArrowDirection.DOWN_RIGHT.arrow;
        } else if (angle >= 202.5 && angle <= 247.5) {
            return ArrowDirection.DOWN_LEFT.arrow;
        } else if (angle >= 292.5) {
            return ArrowDirection.UP_LEFT.arrow;
        } else {
            return ArrowDirection.UP_RIGHT.arrow;
        }

    }

    static public void updateAllScoreboard() {
        final PlayerList plist = PlayerList.getInstance();

        for (int i = 0; i < plist.list.size(); i++) {
            Player currentPlayer = plist.list.get(i);
            final Scoreboard scoreboard = currentPlayer.getScoreboard();
            final Objective objective = scoreboard.getObjective("general");
            assert objective != null;
            objective.unregister();

            final Objective newObjective = scoreboard.registerNewObjective("general", "dummy", "general");
            newObjective.setDisplayName(ChatColor.GOLD + "Friends Location");
            newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

            refreshScoreboard(currentPlayer, plist, newObjective);
            currentPlayer.setScoreboard(scoreboard);
        }
    }

    static private void refreshScoreboard(Player currentPlayer, PlayerList plist, Objective obj) {
        Location currentPlayerLocation = currentPlayer.getLocation();
        double angle = 0;
        double dist = 0;

        for (int i = 0; i < plist.list.size(); i++) {
            Player foreignPlayer = plist.list.get(i);
            final Location tmpPlayerLocation = foreignPlayer.getLocation();

            if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                angle = LocationMath.angleLookToPoint(currentPlayer, tmpPlayerLocation);
                dist = LocationMath.vector2Distance(
                        currentPlayerLocation.getX(),
                        currentPlayerLocation.getZ(),
                        tmpPlayerLocation.getX(),
                        tmpPlayerLocation.getZ()
                );
            }

            if (currentPlayer == foreignPlayer) {
                setCurrentPlayerScoreCoordinate(obj, tmpPlayerLocation);
            } else if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                setForeignPlayerScoreCoordinate(obj, foreignPlayer, angle, dist);
            } else {
                setOtherWorldPlayerCoordinate(obj, foreignPlayer);
            }
        }
    }
}
