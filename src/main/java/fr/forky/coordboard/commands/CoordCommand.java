package fr.forky.coordboard.commands;

import fr.forky.coordboard.enums.WarpType;
import fr.forky.coordboard.utils.player.CoordManager;
import fr.forky.coordboard.utils.scoreboards.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CoordCommand implements CommandExecutor {
    private final CoordManager coordManager;

    public CoordCommand(CoordManager coordManager) {
        this.coordManager = coordManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 0) {
            return executeHelpCommand(player);
        }

        if (args.length == 1) {
            return switch (args[0].toLowerCase()) {
                case "list" -> executeListCommand(player);
                case "stop" -> executeStopCommand(player);
                case "help" -> executeHelpCommand(player);
                default -> executeHelpCommand(player);
            };
        }

        if (args.length == 2) {
            String name = args[1];
            if (!isValidName(name)) {
                player.sendMessage(Component.text("Nom invalide. Utilise uniquement des lettres, chiffres, _ et - (32 caractères max).").color(NamedTextColor.RED));
                return true;
            }
            return switch (args[0].toLowerCase()) {
                case "add" -> executeAddCommand(player, name);
                case "get" -> executeGetCommand(player, name);
                case "delete" -> executeDeleteCommand(player, name);
                case "favorite" -> executeFavoriteCommand(player, name);
                case "unfavorite" -> executeUnfavoriteCommand(player, name);
                case "go" -> executeGoCommand(player, name);
                default -> executeHelpCommand(player);
            };
        }

        if (args.length == 3) {
            return switch (args[0].toLowerCase()) {
                case "rename" -> {
                    String oldName = args[1];
                    String newName = args[2];
                    if (!isValidName(oldName) || !isValidName(newName)) {
                        player.sendMessage(Component.text("Nom invalide. Utilise uniquement des lettres, chiffres, _ et - (32 caractères max).").color(NamedTextColor.RED));
                        yield true;
                    }
                    yield executeRenameCommand(player, oldName, newName);
                }
                case "share" -> {
                    String coordName = args[1];
                    String targetName = args[2];
                    if (!isValidName(coordName)) {
                        player.sendMessage(Component.text("Nom invalide.").color(NamedTextColor.RED));
                        yield true;
                    }
                    yield executeShareCommand(player, coordName, targetName);
                }
                default -> executeHelpCommand(player);
            };
        }

        if (args.length == 6 && args[0].equalsIgnoreCase("import")) {
            return executeImportCommand(player, args);
        }

        return executeHelpCommand(player);
    }

    private boolean executeAddCommand(Player player, String name) {
        coordManager.addCoord(player, name);
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("Coordonnées sauvegardées sous le nom ").color(NamedTextColor.GREEN)
                .append(Component.text(name).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                .append(Component.text(" !").color(NamedTextColor.GREEN)));
        return true;
    }

    private boolean executeGetCommand(Player player, String name) {
        Location location = coordManager.getCoord(player, name);
        if (location == null) {
            player.sendMessage(Component.text("Aucune coordonnée trouvée avec ce nom.").color(NamedTextColor.RED));
            return true;
        }
        player.sendMessage(buildCoordComponent(name, location, coordManager.isFavorite(player, name)));
        return true;
    }

    private boolean executeDeleteCommand(Player player, String name) {
        if (!coordManager.deleteCoord(player, name)) {
            player.sendMessage(Component.text("Aucune coordonnée trouvée avec ce nom.").color(NamedTextColor.RED));
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("Coordonnées ").color(NamedTextColor.GREEN)
                .append(Component.text(name).decorate(TextDecoration.BOLD))
                .append(Component.text(" supprimées.").color(NamedTextColor.GREEN)));
        return true;
    }

    private boolean executeListCommand(Player player) {
        Map<String, Location> coordList = coordManager.getCoordList(player);
        if (coordList == null) {
            player.sendMessage(Component.text("Tu n'as aucune coordonnée enregistrée.").color(NamedTextColor.RED));
            return true;
        }

        List<String> favorites = coordManager.getFavorites(player);
        player.sendMessage(Component.text("━━ Tes coordonnées ━━").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        for (String name : coordList.keySet()) {
            player.sendMessage(buildCoordComponent(name, coordList.get(name), favorites.contains(name)));
        }
        return true;
    }

    private boolean executeFavoriteCommand(Player player, String name) {
        if (!coordManager.toggleFavorite(player, name)) {
            player.sendMessage(Component.text("Coordonnée '" + name + "' introuvable.").color(NamedTextColor.RED));
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        boolean isNowFavorite = coordManager.isFavorite(player, name);
        if (isNowFavorite) {
            player.sendMessage(Component.text("★ ").color(NamedTextColor.YELLOW)
                    .append(Component.text(name).decorate(TextDecoration.BOLD))
                    .append(Component.text(" ajouté aux favoris.").color(NamedTextColor.YELLOW)));
        } else {
            player.sendMessage(Component.text("★ ").color(NamedTextColor.GRAY)
                    .append(Component.text(name).decorate(TextDecoration.BOLD))
                    .append(Component.text(" retiré des favoris.").color(NamedTextColor.GRAY)));
        }
        return true;
    }

    private boolean executeUnfavoriteCommand(Player player, String name) {
        if (!coordManager.removeFavorite(player, name)) {
            player.sendMessage(Component.text("'" + name + "' n'est pas dans tes favoris.").color(NamedTextColor.RED));
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("★ ").color(NamedTextColor.GRAY)
                .append(Component.text(name).decorate(TextDecoration.BOLD))
                .append(Component.text(" retiré des favoris.").color(NamedTextColor.GRAY)));
        return true;
    }

    private boolean executeGoCommand(Player player, String name) {
        if (!coordManager.addSpecialWarp(player, name, WarpType.DESTINATION)) {
            player.sendMessage(Component.text("Coordonnée '" + name + "' introuvable.").color(NamedTextColor.RED));
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("✈ GPS lancé vers ").color(NamedTextColor.GREEN)
                .append(Component.text(name).decorate(TextDecoration.BOLD))
                .append(Component.text(". Bon voyage !").color(NamedTextColor.GREEN)));
        return true;
    }

    private boolean executeStopCommand(Player player) {
        if (!coordManager.removeSpecialWarp(player, WarpType.DESTINATION)) {
            player.sendMessage(Component.text("Aucune destination en cours.").color(NamedTextColor.RED));
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("GPS arrêté.").color(NamedTextColor.GRAY));
        return true;
    }

    private boolean executeRenameCommand(Player player, String oldName, String newName) {
        if (!coordManager.renameCoord(player, oldName, newName)) {
            if (coordManager.getCoord(player, oldName) == null) {
                player.sendMessage(Component.text("Coordonnée '" + oldName + "' introuvable.").color(NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text("Une coordonnée nommée '" + newName + "' existe déjà.").color(NamedTextColor.RED));
            }
            return true;
        }
        ScoreboardUtils.updateAllScoreboard();
        player.sendMessage(Component.text("Coordonnée renommée : ").color(NamedTextColor.GREEN)
                .append(Component.text(oldName).decorate(TextDecoration.STRIKETHROUGH).color(NamedTextColor.GRAY))
                .append(Component.text(" → ").color(NamedTextColor.GREEN))
                .append(Component.text(newName).decorate(TextDecoration.BOLD)));
        return true;
    }

    private boolean executeShareCommand(Player player, String name, String targetPlayerName) {
        Location location = coordManager.getCoord(player, name);
        if (location == null) {
            player.sendMessage(Component.text("Coordonnée '" + name + "' introuvable.").color(NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(targetPlayerName);
        if (target == null) {
            player.sendMessage(Component.text("Joueur '" + targetPlayerName + "' introuvable ou hors ligne.").color(NamedTextColor.RED));
            return true;
        }

        if (target == player) {
            player.sendMessage(Component.text("Tu ne peux pas te partager une coordonnée à toi-même.").color(NamedTextColor.RED));
            return true;
        }

        String worldName = Objects.requireNonNull(location.getWorld()).getName();
        String importCmd = "/coord import %s %s %d %d %d".formatted(
                name, worldName, location.getBlockX(), location.getBlockY(), location.getBlockZ());

        target.sendMessage(
                Component.text("📍 ").color(NamedTextColor.GOLD)
                        .append(Component.text(player.getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                        .append(Component.text(" partage une coordonnée : ").color(NamedTextColor.GOLD))
                        .append(Component.text(name).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                        .append(Component.text(" (%d, %d, %d)".formatted(location.getBlockX(), location.getBlockY(), location.getBlockZ())).color(NamedTextColor.GRAY))
                        .append(Component.text("  [Ajouter]")
                                .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.runCommand(importCmd))
                                .hoverEvent(HoverEvent.showText(Component.text("Cliquer pour ajouter à tes coordonnées"))))
        );

        player.sendMessage(Component.text("Coordonnée ").color(NamedTextColor.GREEN)
                .append(Component.text(name).decorate(TextDecoration.BOLD))
                .append(Component.text(" partagée avec ").color(NamedTextColor.GREEN))
                .append(Component.text(target.getName()).color(NamedTextColor.YELLOW))
                .append(Component.text(" !").color(NamedTextColor.GREEN)));
        return true;
    }

    private boolean executeImportCommand(Player player, String[] args) {
        String name = args[1];
        String worldName = args[2];

        if (!isValidName(name)) {
            player.sendMessage(Component.text("Nom de coordonnée invalide.").color(NamedTextColor.RED));
            return true;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(Component.text("Le monde '" + worldName + "' est introuvable sur ce serveur.").color(NamedTextColor.RED));
            return true;
        }

        try {
            double x = Double.parseDouble(args[3]);
            double y = Double.parseDouble(args[4]);
            double z = Double.parseDouble(args[5]);

            String finalName = coordManager.getCoord(player, name) != null ? name + "_partagé" : name;
            coordManager.addCoordAtLocation(player, finalName, new Location(world, x, y, z));
            ScoreboardUtils.updateAllScoreboard();
            player.sendMessage(Component.text("📍 Coordonnée ").color(NamedTextColor.GREEN)
                    .append(Component.text(finalName).decorate(TextDecoration.BOLD))
                    .append(Component.text(" ajoutée !").color(NamedTextColor.GREEN)));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Données de coordonnée invalides.").color(NamedTextColor.RED));
        }
        return true;
    }

    private boolean executeHelpCommand(Player player) {
        player.sendMessage(Component.text("━━ CoordBoard ━━").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        sendHelpLine(player, "/coord add <nom>", "Sauvegarder ta position actuelle");
        sendHelpLine(player, "/coord get <nom>", "Afficher une coordonnée");
        sendHelpLine(player, "/coord list", "Lister toutes tes coordonnées");
        sendHelpLine(player, "/coord delete <nom>", "Supprimer une coordonnée");
        sendHelpLine(player, "/coord rename <ancien> <nouveau>", "Renommer une coordonnée");
        sendHelpLine(player, "/coord favorite <nom>", "Ajouter/retirer un favori (★)");
        sendHelpLine(player, "/coord unfavorite <nom>", "Retirer un favori");
        sendHelpLine(player, "/coord go <nom>", "Lancer le GPS vers une coordonnée");
        sendHelpLine(player, "/coord stop", "Arrêter le GPS");
        sendHelpLine(player, "/coord share <nom> <joueur>", "Partager une coordonnée");
        return true;
    }

    private void sendHelpLine(Player player, String command, String description) {
        player.sendMessage(
                Component.text(command).color(NamedTextColor.YELLOW)
                        .append(Component.text(" — " + description).color(NamedTextColor.GRAY))
        );
    }

    private Component buildCoordComponent(String name, Location location, boolean isFavorite) {
        String worldName = location.getWorld() != null ? location.getWorld().getName() : "?";
        Component starPrefix = isFavorite
                ? Component.text("★ ").color(NamedTextColor.YELLOW)
                : Component.text("  ");

        return starPrefix
                .append(Component.text(name).color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(Component.text(location.getBlockX()).color(NamedTextColor.GREEN))
                .append(Component.text(" / ").color(NamedTextColor.GRAY))
                .append(Component.text(location.getBlockY()).color(NamedTextColor.AQUA))
                .append(Component.text(" / ").color(NamedTextColor.GRAY))
                .append(Component.text(location.getBlockZ()).color(NamedTextColor.YELLOW))
                .append(Component.text(" (" + worldName + ")").color(NamedTextColor.DARK_GRAY));
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z0-9_\\-]{1,32}");
    }
}
