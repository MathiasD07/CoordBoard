package fr.forky.coordboard.utils.scoreboards;

import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.enums.ArrowDirection;
import fr.forky.coordboard.utils.maths.LocationMath;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardUtils {
    static public void setCurrentPlayerScoreCoordinate(Scoreboard scoreboard, Location playerLocation, Objective objective) {
        Team team = scoreboard.getTeam("currentPlayer");
        String teamKey = ChatColor.GOLD.toString();

        if (null == team) {
            team = scoreboard.registerNewTeam("currentPlayer");
            team.addEntry(teamKey);
            team.setPrefix(ChatColor.GOLD + "X/Y/Z: ");
            team.setSuffix("");

            objective.getScore(teamKey).setScore(1);
        }

        team.setSuffix(
            ChatColor.GREEN + "" + (int) playerLocation.getX() +
            ChatColor.WHITE + " / " + ChatColor.AQUA + (int) playerLocation.getY() +
            ChatColor.WHITE + " / " + ChatColor.YELLOW + (int) playerLocation.getZ()
        );
    }

    static public void setForeignPlayerScoreCoordinate(Scoreboard scoreboard, Player player, double angle, double dist, Objective objective) {
        String uniquePlayerId = player.getUniqueId().toString();
        Team team = scoreboard.getTeam(uniquePlayerId);
        String teamKey = ChatColor.GOLD + player.getName();

        if (null == team) {
            team = scoreboard.registerNewTeam(uniquePlayerId);
            team.addEntry(teamKey);
            team.setPrefix("");
            team.setSuffix("");

            objective.getScore(teamKey).setScore(0);
        }

        team.setSuffix(
                ChatColor.GOLD + ": "
                        + ChatColor.RED + getFormattedPlayerHealth(player) + "❤ "
                        + ChatColor.GREEN + (int) dist + " " + getArrowDirection(angle) + " "
                        + ChatColor.GOLD + "(" + ChatColor.AQUA + (int) player.getLocation().getY() + ChatColor.GOLD + ")"
        );
    }

    static public void setOtherWorldPlayerCoordinate(Scoreboard scoreboard, Player player, Objective objective) {
        String uniquePlayerId = player.getUniqueId().toString();
        Team team = scoreboard.getTeam(uniquePlayerId);
        String teamKey = ChatColor.GOLD + player.getName();

        if (null == team) {
            team = scoreboard.registerNewTeam(uniquePlayerId);
            team.addEntry(teamKey);
            team.setPrefix("");
            team.setSuffix("");

            objective.getScore(teamKey).setScore(0);
        }

        team.setSuffix(
                ChatColor.GOLD + ": "
                        + ChatColor.RED + getFormattedPlayerHealth(player) + "❤ "
                        + ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " " + ChatColor.MAGIC + "00" + " "
                        + ChatColor.GOLD + "(" + ChatColor.AQUA + ChatColor.MAGIC + "00" + ChatColor.GOLD + ")" + ": "
        );
    }

    static private String getFormattedPlayerHealth(Player player) {
        double hearts = player.getHealth() / 2.0;
        hearts = Math.round(hearts * 2) / 2.0;

        String heartsDisplay;

        if (hearts == (int) hearts) {
            heartsDisplay = String.format("%d", (int) hearts);
        } else {
            heartsDisplay = String.format("%.1f", hearts);
        }

        return heartsDisplay;
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

            refreshScoreboard(currentPlayer, plist, scoreboard, objective);
        }
    }

    static public void removePlayerFromAllScoreboard(Player player) {
        final PlayerList plist = PlayerList.getInstance();

        for (int i = 0; i < plist.list.size(); i++) {
            Player currentPlayer = plist.list.get(i);
            Scoreboard scoreboard = currentPlayer.getScoreboard();
            Objective objective = scoreboard.getObjective("general");
            Team team = scoreboard.getTeam(player.getUniqueId().toString());
            assert team != null;
            team.removeEntry(ChatColor.GOLD + player.getName());
            team.unregister();

            assert objective != null;
            objective.getScore(ChatColor.GOLD + player.getName()).setScore(0);
            scoreboard.resetScores(ChatColor.GOLD + player.getName());
        }
    }

    static private void refreshScoreboard(Player currentPlayer, PlayerList plist, Scoreboard scoreboard, Objective objective) {
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
                setCurrentPlayerScoreCoordinate(scoreboard, tmpPlayerLocation, objective);
            } else if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                setForeignPlayerScoreCoordinate(scoreboard, foreignPlayer, angle, dist, objective);
            } else {
                setOtherWorldPlayerCoordinate(scoreboard, foreignPlayer, objective);
            }
        }
    }
}
