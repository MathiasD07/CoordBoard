package fr.forky.coordboard.listeners;

import fr.forky.coordboard.PlayerList;
import fr.forky.coordboard.enums.ArrowDirection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.lang.Math;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        updateAllScoreboard(event.getPlayer());
    }

    private void updateAllScoreboard(Player movingPlayer) {
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


    private void refreshScoreboard(Player currentPlayer, PlayerList plist, Objective obj) {
        Location currentPlayerLocation = currentPlayer.getLocation();
        double angle = 0;
        double dist = 0;

        for (int i = 0; i < plist.list.size(); i++) {
            Player tmpPlayer = plist.list.get(i);
            final Location tmpPlayerLocation = tmpPlayer.getLocation();

            if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                Vector inBetween = tmpPlayerLocation.clone().subtract(currentPlayerLocation).toVector();
                Vector lookVec = currentPlayer.getEyeLocation().getDirection();

                double angleDir = (Math.atan2(inBetween.getZ(),inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
                double angleLook = (Math.atan2(lookVec.getZ(),lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;
                angle = (angleDir - angleLook + 360) % 360;

//            double dist = currentPlayerLocation.distance(tmpPlayerLocation);
                dist = vector2Distance(
                        currentPlayerLocation.getX(),
                        currentPlayerLocation.getZ(),
                        tmpPlayerLocation.getX(),
                        tmpPlayerLocation.getZ()
                );
            }

            if (currentPlayer == tmpPlayer) {
                final Score coord = obj.getScore(
                        ChatColor.GOLD + "X/Y/Z: " +
                                ChatColor.GREEN + (int) tmpPlayerLocation.getX() +
                                ChatColor.WHITE + " / " + ChatColor.AQUA + (int) tmpPlayerLocation.getY() +
                                ChatColor.WHITE + " / " + ChatColor.YELLOW + (int) tmpPlayerLocation.getZ()
                );
                coord.setScore(1);
            } else {
                if (currentPlayerLocation.getWorld() == tmpPlayerLocation.getWorld()) {
                    final Score coord = obj.getScore(
                        ChatColor.GOLD + tmpPlayer.getName() +
                            ChatColor.GOLD + "(" + ChatColor.AQUA +
                            (int) tmpPlayerLocation.getY() + ChatColor.GOLD + ")" + ": " +
                            ChatColor.GREEN + (int) dist + " blocks" +
                            "  " + getArrowDirection(angle)
                    );
                    coord.setScore(0);
                } else {
                    final Score coord = obj.getScore(
                            ChatColor.GOLD + tmpPlayer.getName() +
                                    ChatColor.GOLD + "(" + ChatColor.AQUA +
                                    ChatColor.MAGIC + "00" + ChatColor.GOLD + ")" + ": " +
                                    ChatColor.GREEN + ChatColor.MAGIC + "000" + ChatColor.GREEN + " blocks" +
                                    "  " + ChatColor.MAGIC + "00"
                    );
                    coord.setScore(0);
                }

            }
        }
    }

    private String getArrowDirection(double angle) {
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

    private double vector2Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
 }
