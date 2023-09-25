package fr.forky.coordboard.commands;

import fr.forky.coordboard.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListPlayer implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args) {
        if (!(sender instanceof Player)) { return true; }

        //list player
        if (cmd.getName().equalsIgnoreCase("playerlist")) {
            Bukkit.broadcastMessage(PlayerList.getInstance().getPlayerList());
            return true;
        }

        return true;
    }
}
