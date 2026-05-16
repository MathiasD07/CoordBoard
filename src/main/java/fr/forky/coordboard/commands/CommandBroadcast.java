package fr.forky.coordboard.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBroadcast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Veuillez préciser un message !").color(NamedTextColor.RED));
            return false;
        }

        String rawMessage = String.join(" ", args);
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(rawMessage);
        Bukkit.broadcast(message);

        return false;
    }
}
