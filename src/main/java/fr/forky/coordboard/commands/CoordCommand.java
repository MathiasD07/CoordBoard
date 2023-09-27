package fr.forky.coordboard.commands;

import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.player.CoordManager;
import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class CoordCommand implements CommandExecutor {
    private final CoordManager coordManager;

    public CoordCommand(CoordManager coordManager)
    {
        this.coordManager = coordManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 2) {
            String name = args[1];

            return switch (args[0].toLowerCase()) {
                case "add" -> executeAddCommand(player, name);
                case "get" -> executeGetCommand(player, name);
                case "delete" -> executeDeleteCommand(player, name);
                case "favorite" -> executeFavoriteCommand(player, name);
                case "go" -> executeGoCommand(player, name);
                default -> false;
            };
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            return executeListCommand(player);
        }

        return false;
    }

    private boolean executeAddCommand(Player player, String name)
    {
        coordManager.addCoord(player, name);
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage("Coordonnées ajoutées avec succès sous le nom: " + name);

        return true;
    }

    private boolean executeGetCommand(Player player, String name)
    {
        Location location = coordManager.getCoord(player, name);

        if (null == location) {
            player.sendMessage(ChatColor.RED + "Aucunes coordonnées trouvées avec ce nom.");

            return true;
        }

        String message = formatCoordMessage(
                name,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                Objects.requireNonNull(location.getWorld()).getName()
        );

        player.sendMessage(message);

        return true;
    }

    private boolean executeDeleteCommand(Player player, String name)
    {
        boolean deletedCoord = coordManager.deleteCoord(player, name);
        ScoreboardUtils.updateAllScoreboard();

        if (!deletedCoord) {
            player.sendMessage(ChatColor.RED + "Aucunes coordonnées trouvées avec ce nom.");

            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Coordonnées pour " + name + " supprimées avec succès !");

        return true;
    }

    private boolean executeListCommand(Player player)
    {
        Map<String, Location> coordList = coordManager.getCoordList(player);

        if (null == coordList) {
            player.sendMessage(ChatColor.RED + "Aucunes coordonnées trouvées.");

            return true;
        }


        player.sendMessage(ChatColor.GREEN + "Liste des coordonnées enregistrées:");
        for (String name : coordList.keySet()) {
            Location location = coordList.get(name);
            String message = formatCoordMessage(
                    name,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    Objects.requireNonNull(location.getWorld()).getName()
            );

            player.sendMessage(message);
        }

        return true;
    }

    private boolean executeFavoriteCommand(Player player, String name)
    {
        boolean isFavoriteAdded = coordManager.addSpecialWarp(player, name, WarpType.FAVORITE);

        if (!isFavoriteAdded) {
            player.sendMessage(ChatColor.RED + "Les coordonnées n'ont pas été ajoutées en favori car le nom n'existe pas.");

            return true;
        }

        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage("Coordonnées " + ChatColor.BOLD + name + ChatColor.RESET + " ajoutées en favori avec succès");

        return true;
    }

    private boolean executeGoCommand(Player player, String name)
    {
        boolean isDestinationAdded = coordManager.addSpecialWarp(player, name, WarpType.DESTINATION);

        if (!isDestinationAdded) {
            player.sendMessage(ChatColor.RED + "Les coordonnées n'ont pas été ajoutées en tant que destinaton car le nom n'existe pas.");

            return true;
        }

        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage("Coordonnées " + ChatColor.BOLD + name + ChatColor.RESET + " ajoutées en tant que destination avec succès ! Bon voyage ;)");

        return true;
    }

    private String formatCoordMessage(String name, int x, int y, int z, String worldName)
    {
        return name + ": " + x + " / " + y + " / " + z + " (" + worldName + ")";
    }
}