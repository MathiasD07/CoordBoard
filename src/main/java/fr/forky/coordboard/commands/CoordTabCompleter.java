package fr.forky.coordboard.commands;

import fr.forky.coordboard.utils.player.CoordManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CoordTabCompleter implements TabCompleter {
    private static final List<String> SUBCOMMANDS = List.of(
            "add", "get", "list", "delete", "rename", "favorite", "unfavorite", "go", "stop", "share", "help"
    );

    private final CoordManager coordManager;

    public CoordTabCompleter(CoordManager coordManager) {
        this.coordManager = coordManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }

        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "get", "delete", "favorite", "go", "rename", "share" -> filterCoordNames(player, args[1]);
                case "unfavorite" -> filter(coordManager.getFavorites(player), args[1]);
                default -> List.of();
            };
        }

        if (args.length == 3) {
            return switch (args[0].toLowerCase()) {
                case "rename" -> filterCoordNames(player, args[2]);
                case "share" -> filterOnlinePlayers(player, args[2]);
                default -> List.of();
            };
        }

        return List.of();
    }

    private List<String> filterCoordNames(Player player, String prefix) {
        Map<String, Location> coords = coordManager.getCoordList(player);
        if (coords == null) return List.of();
        return filter(coords.keySet().stream().toList(), prefix);
    }

    private List<String> filterOnlinePlayers(Player player, String prefix) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(player.getName()))
                .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                .toList();
    }

    private List<String> filter(List<String> list, String prefix) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .toList();
    }
}
