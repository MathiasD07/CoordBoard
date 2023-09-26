package fr.forky.coordboard.commands;

import fr.forky.coordboard.utils.player.CoordManager;
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

    public CoordCommand(CoordManager coordManager) {
        this.coordManager = coordManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            String name = args[1];
            coordManager.addCoord((Player) sender, name);
            player.sendMessage("Coordonnées ajoutées avec succès sous le nom: " + name);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
            String name = args[1];
            Location coord = coordManager.getCoord((Player) sender, name);

            if (null == coord) {
                player.sendMessage(ChatColor.RED + "Aucune coordonnée trouvée avec ce nom.");

                return true;
            }

            player.sendMessage(
                    name + ": "
                            + coord.getBlockX() + " / "
                            + coord.getBlockY() + " / "
                            + coord.getBlockZ() + " "
                            + "(" + Objects.requireNonNull(coord.getWorld()).getName() + ")"
            );

            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            String name = args[1];
            boolean deletedCoord = coordManager.deleteCoord((Player) sender, name);

            if (!deletedCoord) {
                player.sendMessage(ChatColor.RED + "Aucune coordonnée trouvée avec ce nom.");

                return true;
            }

            player.sendMessage(ChatColor.GREEN + "Coordonnées pour " + name + " supprimées avec succès !");

            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            Map<String, Location> coordList = coordManager.getCoordList((Player) sender);

            if (null == coordList) {
                player.sendMessage(ChatColor.RED + "Aucune coordonnée trouvée.");

                return true;
            }


            player.sendMessage(ChatColor.GREEN + "Liste des coordonnées enregistrées:");
            for (String name : coordList.keySet()) {
                Location location = coordList.get(name);
                player.sendMessage(
                        name + ": "
                                + location.getBlockX() + " / "
                                + location.getBlockY() + " / "
                                + location.getBlockZ() + " "
                                + "(" + Objects.requireNonNull(location.getWorld()).getName() + ")"
                );
            }

            return true;
        }

        return false;
    }
}