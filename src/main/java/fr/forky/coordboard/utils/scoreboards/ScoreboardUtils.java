package fr.forky.coordboard.utils.scoreboards;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.enums.ArrowDirection;
import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.maths.LocationMath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardUtils {
    static public void setCurrentPlayerScoreCoordinate(Scoreboard scoreboard, Objective objective, Location playerLocation) {
        Team team = getOrCreateTeam(scoreboard, objective, "CurrentPlayer", ChatColor.GOLD.toString(), 3);

        team.setSuffix(
                ChatColor.GOLD + "X/Y/Z: "
                        + ChatColor.GREEN + (int) playerLocation.getX()
                        + ChatColor.WHITE + " / " + ChatColor.AQUA + (int) playerLocation.getY()
                        + ChatColor.WHITE + " / " + ChatColor.YELLOW + (int) playerLocation.getZ()
        );
    }

    static public void setForeignPlayerScoreCoordinate(Player foreignPlayer, Scoreboard scoreboard, Objective objective, double angle, double dist) {
        Team team = getOrCreateTeam(scoreboard, objective, foreignPlayer.getUniqueId().toString(), ChatColor.GOLD + foreignPlayer.getName(), 0);

        team.setSuffix(
                ChatColor.GOLD + ": "
                        + ChatColor.RED + getFormattedPlayerHealth(foreignPlayer) + "❤ "
                        + ChatColor.GREEN + (int) dist + " " + getArrowDirection(angle) + " "
                        + ChatColor.GOLD + "(" + ChatColor.AQUA + (int) foreignPlayer.getLocation().getY() + ChatColor.GOLD + ")"
        );
    }

    static public void setOtherWorldPlayerCoordinate(Player foreignPlayer, Scoreboard scoreboard, Objective objective) {
        Team team = getOrCreateTeam(scoreboard, objective, foreignPlayer.getUniqueId().toString(), ChatColor.GOLD + foreignPlayer.getName(), 0);

        team.setSuffix(
                ChatColor.GOLD + ": "
                        + ChatColor.RED + getFormattedPlayerHealth(foreignPlayer) + "❤ "
                        + ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " " + ChatColor.MAGIC + "00" + " "
                        + ChatColor.GOLD + "(" + ChatColor.AQUA + ChatColor.MAGIC + "00" + ChatColor.GOLD + ")" + ": "
        );
    }

    static public Team getOrCreateTeam(Scoreboard scoreboard, Objective objective, String teamName, String teamKey, Integer score) {
        Team team = scoreboard.getTeam(teamName);

        if (null == team) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(teamKey);
            team.setPrefix("");
            team.setSuffix("");

            objective.getScore(teamKey).setScore(score);
        }

        return team;
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
                setCurrentPlayerScoreCoordinate(scoreboard, objective, tmpPlayerLocation);
                setCurrentPlayerFavoriteCoordinate(currentPlayer, scoreboard, objective);
                setCurrentPlayerDestinationCoordinate(currentPlayer, scoreboard, objective);
            } else if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                setForeignPlayerScoreCoordinate(foreignPlayer, scoreboard, objective, angle, dist);
            } else {
                setOtherWorldPlayerCoordinate(foreignPlayer, scoreboard, objective);
            }
        }
    }

    static private void setCurrentPlayerFavoriteCoordinate(Player player, Scoreboard scoreboard, Objective objective)
    {
        FileConfiguration customConfig = Main.getCustomConfig();
        String uuid = player.getUniqueId().toString();
        String favoritePath = uuid + "." + WarpType.FAVORITE.type;

        if (!customConfig.contains(favoritePath)) {
            Team favoriteTeam = scoreboard.getTeam("Favorite");

            if (null != favoriteTeam) {
                favoriteTeam.removeEntry(ChatColor.YELLOW.toString());
                favoriteTeam.unregister();
                objective.getScore(ChatColor.YELLOW.toString()).setScore(0);
                scoreboard.resetScores(ChatColor.YELLOW.toString());
            }

            return;
        }

        String warpName = customConfig.getString(favoritePath);
        String warpPath = uuid + ".warp." + warpName;
        String worldName = customConfig.getString(warpPath + ".world");
        double x = customConfig.getDouble(warpPath + ".x");
        double y = customConfig.getDouble(warpPath + ".y");
        double z = customConfig.getDouble(warpPath + ".z");
        assert worldName != null;
        Location favoriteLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

        Team team = getOrCreateTeam(scoreboard, objective, "Favorite", ChatColor.YELLOW.toString(), 2);

        if (player.getWorld() != favoriteLocation.getWorld())
        {
            team.setSuffix(
                    ChatColor.YELLOW + "★ " + warpName + ": "
                            + ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " " + ChatColor.MAGIC + "00" + " "
                            + ChatColor.GOLD + "(" + ChatColor.AQUA + ChatColor.MAGIC + "00" + ChatColor.GOLD + ")"
            );

            return;
        }

        double angle = LocationMath.angleLookToPoint(player, favoriteLocation);
        double dist = LocationMath.vector2Distance(
                player.getLocation().getX(),
                player.getLocation().getZ(),
                favoriteLocation.getX(),
                favoriteLocation.getZ()
        );

        team.setSuffix(
                ChatColor.YELLOW + "★ " + warpName + ": "
                        + ChatColor.GREEN + (int) dist + " " + getArrowDirection(angle)
                        + ChatColor.GOLD + " (" + ChatColor.AQUA + (int) favoriteLocation.getY() + ChatColor.GOLD + ")"
        );
    }

    static private void setCurrentPlayerDestinationCoordinate(Player player, Scoreboard scoreboard, Objective objective)
    {
        FileConfiguration customConfig = Main.getCustomConfig();
        String uuid = player.getUniqueId().toString();
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;

        if (!customConfig.contains(destinationPath)) {
            Team destinationTeam = scoreboard.getTeam("Destination");

            if (null != destinationTeam) {
                destinationTeam.removeEntry(ChatColor.BLUE.toString());
                destinationTeam.unregister();
                objective.getScore(ChatColor.BLUE.toString()).setScore(0);
                scoreboard.resetScores(ChatColor.BLUE.toString());
            }

            return;
        }

        String warpName = customConfig.getString(destinationPath);
        String warpPath = uuid + ".warp." + warpName;
        String worldName = customConfig.getString(warpPath + ".world");
        double x = customConfig.getDouble(warpPath + ".x");
        double y = customConfig.getDouble(warpPath + ".y");
        double z = customConfig.getDouble(warpPath + ".z");
        assert worldName != null;
        Location destinationLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

        Team team = getOrCreateTeam(scoreboard, objective, "Destination", ChatColor.BLUE.toString(), 1);

        if (player.getWorld() != destinationLocation.getWorld())
        {
            team.setSuffix(
                    ChatColor.BLUE + "✈ " + warpName + ": "
                            + ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " " + ChatColor.MAGIC + "00" + " "
                            + ChatColor.GOLD + "(" + ChatColor.AQUA + ChatColor.MAGIC + "00" + ChatColor.GOLD + ")"
            );

            return;
        }

        double angle = LocationMath.angleLookToPoint(player, destinationLocation);
        double dist = LocationMath.vector2Distance(
                player.getLocation().getX(),
                player.getLocation().getZ(),
                destinationLocation.getX(),
                destinationLocation.getZ()
        );

        team.setSuffix(
                ChatColor.BLUE + "✈ " + warpName + ": "
                        + ChatColor.GREEN + (int) dist + " " + getArrowDirection(angle)
                        + ChatColor.GOLD + " (" + ChatColor.AQUA + (int) destinationLocation.getY() + ChatColor.GOLD + ")"
        );
    }
}
