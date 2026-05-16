package fr.forky.coordboard.commands;

import fr.forky.coordboard.PlayerList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListPlayer implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) { return true; }

        if (cmd.getName().equalsIgnoreCase("playerlist")) {
            Bukkit.broadcast(Component.text(PlayerList.getInstance().getPlayerList()));
            return true;
        }

        return true;
    }
}
