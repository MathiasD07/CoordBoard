package fr.forky.coordboard.utils.scoreboards;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.enums.ArrowDirection;
import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.maths.LocationMath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class ScoreboardUtils {
    // Espaces invisibles comme clés d'entrée pour les lignes fixes du scoreboard
    private static final String CURRENT_PLAYER_ENTRY = " ";
    private static final String DESTINATION_ENTRY = "  ";
    private static final String[] FAVORITE_ENTRIES = {"   ", "    ", "     "};
    private static final int MAX_DISPLAYED_FAVORITES = 3;

    private static final int SCORE_CURRENT_PLAYER = 100;
    private static final int SCORE_DESTINATION = 5;
    private static final int SCORE_FAVORITE_BASE = 20; // 20, 19, 18 pour les favoris
    private static final int SCORE_FOREIGN_PLAYER = 0;

    static public void setCurrentPlayerScoreCoordinate(Scoreboard scoreboard, Objective objective, Location playerLocation) {
        Team team = getOrCreateTeam(scoreboard, objective, "CurrentPlayer", CURRENT_PLAYER_ENTRY, SCORE_CURRENT_PLAYER);
        team.suffix(
                Component.text("X/Y/Z: ").color(NamedTextColor.GOLD)
                        .append(Component.text((int) playerLocation.getX()).color(NamedTextColor.GREEN))
                        .append(Component.text(" / ").color(NamedTextColor.WHITE))
                        .append(Component.text((int) playerLocation.getY()).color(NamedTextColor.AQUA))
                        .append(Component.text(" / ").color(NamedTextColor.WHITE))
                        .append(Component.text((int) playerLocation.getZ()).color(NamedTextColor.YELLOW))
        );
    }

    static public void setForeignPlayerScoreCoordinate(Player foreignPlayer, Scoreboard scoreboard, Objective objective, double angle, double dist) {
        Team team = getOrCreateTeam(scoreboard, objective, foreignPlayer.getUniqueId().toString(), foreignPlayer.getName(), SCORE_FOREIGN_PLAYER);
        team.suffix(
                Component.text(": ").color(NamedTextColor.GOLD)
                        .append(Component.text(getFormattedPlayerHealth(foreignPlayer) + "❤ ").color(NamedTextColor.RED))
                        .append(Component.text((int) dist + " " + getArrowDirection(angle) + " ").color(NamedTextColor.GREEN))
                        .append(Component.text("(").color(NamedTextColor.GOLD))
                        .append(Component.text((int) foreignPlayer.getLocation().getY()).color(NamedTextColor.AQUA))
                        .append(Component.text(")").color(NamedTextColor.GOLD))
        );
    }

    static public void setOtherWorldPlayerCoordinate(Player foreignPlayer, Scoreboard scoreboard, Objective objective) {
        Team team = getOrCreateTeam(scoreboard, objective, foreignPlayer.getUniqueId().toString(), foreignPlayer.getName(), SCORE_FOREIGN_PLAYER);
        team.suffix(
                Component.text(": ").color(NamedTextColor.GOLD)
                        .append(Component.text(getFormattedPlayerHealth(foreignPlayer) + "❤ ").color(NamedTextColor.RED))
                        .append(Component.text("000 00").color(NamedTextColor.GREEN).decorate(TextDecoration.OBFUSCATED))
                        .append(Component.text(" (").color(NamedTextColor.GOLD))
                        .append(Component.text("00").color(NamedTextColor.AQUA).decorate(TextDecoration.OBFUSCATED))
                        .append(Component.text(")").color(NamedTextColor.GOLD))
        );
    }

    @SuppressWarnings("deprecation")
    static public Team getOrCreateTeam(Scoreboard scoreboard, Objective objective, String teamName, String entryKey, int score) {
        Team team = scoreboard.getTeam(teamName);
        if (null == team) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(entryKey);
            team.color(NamedTextColor.GOLD);
            team.prefix(Component.empty());
            team.suffix(Component.empty());
            objective.getScore(entryKey).setScore(score);
        }
        return team;
    }

    static private String getFormattedPlayerHealth(Player player) {
        double hearts = player.getHealth() / 2.0;
        hearts = Math.round(hearts * 2) / 2.0;
        if (hearts == (int) hearts) return String.format("%d", (int) hearts);
        return String.format("%.1f", hearts);
    }

    static private String getArrowDirection(double angle) {
        if (angle < 22.5 || angle > 337.5) return ArrowDirection.UP.arrow;
        else if (angle > 67.5 && angle < 112.5) return ArrowDirection.RIGHT.arrow;
        else if (angle > 157.5 && angle < 202.5) return ArrowDirection.DOWN.arrow;
        else if (angle > 247.5 && angle < 292.5) return ArrowDirection.LEFT.arrow;
        else if (angle >= 112.5 && angle <= 157.5) return ArrowDirection.DOWN_RIGHT.arrow;
        else if (angle >= 202.5 && angle <= 247.5) return ArrowDirection.DOWN_LEFT.arrow;
        else if (angle >= 292.5) return ArrowDirection.UP_LEFT.arrow;
        else return ArrowDirection.UP_RIGHT.arrow;
    }

    static public void updateAllScoreboard() {
        final PlayerList plist = PlayerList.getInstance();
        List<Player> snapshot = List.copyOf(plist.list);

        for (Player currentPlayer : snapshot) {
            if (!currentPlayer.isOnline()) continue;
            final Scoreboard scoreboard = currentPlayer.getScoreboard();
            final Objective objective = scoreboard.getObjective("general");
            if (objective == null) continue;
            refreshScoreboard(currentPlayer, snapshot, scoreboard, objective);
        }
    }

    @SuppressWarnings("deprecation")
    static public void removePlayerFromAllScoreboard(Player player) {
        final PlayerList plist = PlayerList.getInstance();
        List<Player> snapshot = List.copyOf(plist.list);

        for (Player currentPlayer : snapshot) {
            if (!currentPlayer.isOnline()) continue;
            Scoreboard scoreboard = currentPlayer.getScoreboard();
            Objective objective = scoreboard.getObjective("general");
            Team team = scoreboard.getTeam(player.getUniqueId().toString());
            if (team == null) continue;

            String entry = player.getName();
            team.removeEntry(entry);
            team.unregister();

            if (objective != null) {
                objective.getScore(entry).setScore(0);
                scoreboard.resetScores(entry);
            }
        }
    }

    static private void refreshScoreboard(Player currentPlayer, List<Player> plist, Scoreboard scoreboard, Objective objective) {
        Location currentPlayerLocation = currentPlayer.getLocation();

        for (Player foreignPlayer : plist) {
            final Location tmpPlayerLocation = foreignPlayer.getLocation();
            double angle = 0, dist = 0;

            if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                angle = LocationMath.angleLookToPoint(currentPlayer, tmpPlayerLocation);
                dist = LocationMath.vector2Distance(
                        currentPlayerLocation.getX(), currentPlayerLocation.getZ(),
                        tmpPlayerLocation.getX(), tmpPlayerLocation.getZ()
                );
            }

            if (currentPlayer == foreignPlayer) {
                setCurrentPlayerScoreCoordinate(scoreboard, objective, tmpPlayerLocation);
                setCurrentPlayerFavoritesCoordinates(currentPlayer, scoreboard, objective);
                setCurrentPlayerDestinationCoordinate(currentPlayer, scoreboard, objective);
            } else if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                setForeignPlayerScoreCoordinate(foreignPlayer, scoreboard, objective, angle, dist);
            } else {
                setOtherWorldPlayerCoordinate(foreignPlayer, scoreboard, objective);
            }
        }
    }

    @SuppressWarnings("deprecation")
    static private void setCurrentPlayerFavoritesCoordinates(Player player, Scoreboard scoreboard, Objective objective) {
        List<String> favorites = Main.getCoordManager().getFavorites(player);
        int displayed = Math.min(favorites.size(), MAX_DISPLAYED_FAVORITES);

        // Affiche les favoris actifs
        for (int i = 0; i < displayed; i++) {
            String favName = favorites.get(i);
            String entryKey = FAVORITE_ENTRIES[i];
            int score = SCORE_FAVORITE_BASE - i;

            Location favLocation = Main.getCoordManager().getCoord(player, favName);
            if (favLocation == null) continue;

            Team team = getOrCreateTeam(scoreboard, objective, "Favorite_" + i, entryKey, score);
            team.color(NamedTextColor.YELLOW);

            if (player.getWorld() != favLocation.getWorld()) {
                team.suffix(buildOtherWorldFavoriteSuffix(favName));
            } else {
                double angle = LocationMath.angleLookToPoint(player, favLocation);
                double dist = LocationMath.vector2Distance(
                        player.getLocation().getX(), player.getLocation().getZ(),
                        favLocation.getX(), favLocation.getZ()
                );
                team.suffix(buildFavoriteSuffix(favName, dist, angle, favLocation));
            }
        }

        // Supprime les slots de favoris inutilisés
        for (int i = displayed; i < MAX_DISPLAYED_FAVORITES; i++) {
            removeFavoriteSlot(scoreboard, objective, i);
        }
    }

    @SuppressWarnings("deprecation")
    static private void removeFavoriteSlot(Scoreboard scoreboard, Objective objective, int index) {
        Team team = scoreboard.getTeam("Favorite_" + index);
        if (team != null) {
            String entry = FAVORITE_ENTRIES[index];
            team.removeEntry(entry);
            team.unregister();
            objective.getScore(entry).setScore(0);
            scoreboard.resetScores(entry);
        }
    }

    static private Component buildFavoriteSuffix(String name, double dist, double angle, Location location) {
        return Component.text("★ " + name + ": ").color(NamedTextColor.YELLOW)
                .append(Component.text((int) dist + " " + getArrowDirection(angle)).color(NamedTextColor.GREEN))
                .append(Component.text(" (").color(NamedTextColor.GOLD))
                .append(Component.text((int) location.getY()).color(NamedTextColor.AQUA))
                .append(Component.text(")").color(NamedTextColor.GOLD));
    }

    static private Component buildOtherWorldFavoriteSuffix(String name) {
        return Component.text("★ " + name + ": ").color(NamedTextColor.YELLOW)
                .append(Component.text("000 00").color(NamedTextColor.GREEN).decorate(TextDecoration.OBFUSCATED))
                .append(Component.text(" (").color(NamedTextColor.GOLD))
                .append(Component.text("00").color(NamedTextColor.AQUA).decorate(TextDecoration.OBFUSCATED))
                .append(Component.text(")").color(NamedTextColor.GOLD));
    }

    @SuppressWarnings("deprecation")
    static private void setCurrentPlayerDestinationCoordinate(Player player, Scoreboard scoreboard, Objective objective) {
        FileConfiguration customConfig = Main.getCustomConfig();
        String uuid = player.getUniqueId().toString();
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;

        if (!customConfig.contains(destinationPath)) {
            Team team = scoreboard.getTeam("Destination");
            if (team != null) {
                team.removeEntry(DESTINATION_ENTRY);
                team.unregister();
                objective.getScore(DESTINATION_ENTRY).setScore(0);
                scoreboard.resetScores(DESTINATION_ENTRY);
            }
            return;
        }

        String warpName = customConfig.getString(destinationPath);
        Location destLocation = Main.getCoordManager().getCoord(player, warpName);
        if (destLocation == null) return;

        Team team = getOrCreateTeam(scoreboard, objective, "Destination", DESTINATION_ENTRY, SCORE_DESTINATION);
        team.color(NamedTextColor.AQUA);

        if (player.getWorld() != destLocation.getWorld()) {
            team.suffix(
                    Component.text("✈ " + warpName + ": ").color(NamedTextColor.AQUA)
                            .append(Component.text("000 00").color(NamedTextColor.GREEN).decorate(TextDecoration.OBFUSCATED))
                            .append(Component.text(" (").color(NamedTextColor.GOLD))
                            .append(Component.text("00").color(NamedTextColor.AQUA).decorate(TextDecoration.OBFUSCATED))
                            .append(Component.text(")").color(NamedTextColor.GOLD))
            );
            return;
        }

        double angle = LocationMath.angleLookToPoint(player, destLocation);
        double dist = LocationMath.vector2Distance(
                player.getLocation().getX(), player.getLocation().getZ(),
                destLocation.getX(), destLocation.getZ()
        );

        team.suffix(
                Component.text("✈ " + warpName + ": ").color(NamedTextColor.AQUA)
                        .append(Component.text((int) dist + " " + getArrowDirection(angle)).color(NamedTextColor.GREEN))
                        .append(Component.text(" (").color(NamedTextColor.GOLD))
                        .append(Component.text((int) destLocation.getY()).color(NamedTextColor.AQUA))
                        .append(Component.text(")").color(NamedTextColor.GOLD))
        );
    }
}
