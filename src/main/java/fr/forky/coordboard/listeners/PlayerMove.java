package fr.forky.coordboard.listeners;

import fr.forky.coordboard.PlayerList;
import net.minecraft.network.chat.ChatComponentUtils;
import net.minecraft.network.chat.ChatDecoration;
import net.minecraft.network.chat.ChatDecorator;
import org.bukkit.Bukkit;
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

public class PlayerMove implements Listener {

    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent event) {
//        final Player player = event.getPlayer();
//        final Location playerLocation = player.getLocation();
//
//        final Scoreboard scoreboard = player.getScoreboard();
//
//        final Objective objective = scoreboard.getObjective(player.getName());
//        assert objective != null;
//        objective.unregister();
//
//        final Objective newObjective = scoreboard.registerNewObjective(player.getName(), "dummy", player.getName());
//        newObjective.setDisplayName(ChatColor.GOLD + "Position de " + ChatColor.BOLD + player.getDisplayName());
//        newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
//
//        final Score coord = newObjective.getScore(
//                ChatColor.GREEN + "X: " + (int) playerLocation.getX() +
//                        ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) playerLocation.getY() +
//                        ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) playerLocation.getZ()
//        );
//
//        coord.setScore(0);
//
//        player.setScoreboard(scoreboard);
//    }
    public void onPlayerMove(PlayerMoveEvent event) {
        PlayerList pl = PlayerList.getInstance();

        final Player player = event.getPlayer();
        final Location playerLocation = player.getLocation();

        final Scoreboard scoreboard = player.getScoreboard();

        final Objective objective = scoreboard.getObjective(player.getName());
        assert objective != null;
        objective.unregister();


        final Objective newObjective = scoreboard.registerNewObjective(player.getName(), "dummy", player.getName());
        newObjective.setDisplayName(ChatColor.GOLD + "Friends Location");
        newObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

//        final Score coord = newObjective.getScore(
//                ChatColor.GOLD + player.getName() + ": " +
//                        ChatColor.GREEN + "X: " + (int) playerLocation.getX() +
//                        ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) playerLocation.getY() +
//                        ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) playerLocation.getZ()
//        );

        for (int i = 0; i < pl.list.size(); i++) {
            Player tmpPlayer = pl.list.get(i);
            final Location tmpPlayerLocation = tmpPlayer.getLocation();

            final Score coord = newObjective.getScore(
                    ChatColor.GOLD + tmpPlayer.getName() + ": " +
                            ChatColor.GREEN + "X: " + (int) tmpPlayerLocation.getX() +
                            ChatColor.WHITE + " / " + ChatColor.AQUA + "Y: " + (int) tmpPlayerLocation.getY() +
                            ChatColor.WHITE + " / " + ChatColor.YELLOW + "Z: " + (int) tmpPlayerLocation.getZ()
            );
//→←↑↓⬈⬉⬊⬋
            coord.setScore(0);
        }



        player.setScoreboard(scoreboard);
    }
}
