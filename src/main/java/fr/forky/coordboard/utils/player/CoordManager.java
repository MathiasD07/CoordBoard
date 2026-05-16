package fr.forky.coordboard.utils.player;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.enums.WarpType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class CoordManager {
    private static final String FAVORITES_KEY = "favorites";

    private final Main plugin;
    private final FileConfiguration customConfig;

    public CoordManager(Main plugin) {
        this.plugin = plugin;
        this.customConfig = Main.getCustomConfig();
    }

    public void addCoord(Player player, String name) {
        addCoordAtLocation(player, name, player.getLocation());
    }

    public void addCoordAtLocation(Player player, String name, Location location) {
        String uuid = player.getUniqueId().toString();
        String path = uuid + ".warp." + name;
        World world = location.getWorld();
        if (world == null) return;

        this.customConfig.set(path + ".world", world.getName());
        this.customConfig.set(path + ".x", location.getX());
        this.customConfig.set(path + ".y", location.getY());
        this.customConfig.set(path + ".z", location.getZ());

        this.plugin.saveCustomConfig();
    }

    public boolean renameCoord(Player player, String oldName, String newName) {
        String uuid = player.getUniqueId().toString();
        String oldPath = uuid + ".warp." + oldName;
        String newPath = uuid + ".warp." + newName;

        if (!this.customConfig.contains(oldPath)) return false;
        if (this.customConfig.contains(newPath)) return false;

        String worldName = this.customConfig.getString(oldPath + ".world");
        double x = this.customConfig.getDouble(oldPath + ".x");
        double y = this.customConfig.getDouble(oldPath + ".y");
        double z = this.customConfig.getDouble(oldPath + ".z");

        this.customConfig.set(newPath + ".world", worldName);
        this.customConfig.set(newPath + ".x", x);
        this.customConfig.set(newPath + ".y", y);
        this.customConfig.set(newPath + ".z", z);
        this.customConfig.set(oldPath, null);

        // Update favorites list
        List<String> favorites = new ArrayList<>(getFavorites(player));
        int idx = favorites.indexOf(oldName);
        if (idx >= 0) {
            favorites.set(idx, newName);
            this.customConfig.set(uuid + "." + FAVORITES_KEY, favorites);
        }

        // Update destination
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;
        if (oldName.equals(this.customConfig.getString(destinationPath))) {
            this.customConfig.set(destinationPath, newName);
        }

        this.plugin.saveCustomConfig();
        return true;
    }

    // --- Favorites (multiple) ---

    public List<String> getFavorites(Player player) {
        String uuid = player.getUniqueId().toString();
        String newPath = uuid + "." + FAVORITES_KEY;
        String oldPath = uuid + "." + WarpType.FAVORITE.type;

        // Migrate old single-favorite format
        if (!this.customConfig.contains(newPath) && this.customConfig.contains(oldPath)) {
            String oldFavorite = this.customConfig.getString(oldPath);
            if (oldFavorite != null) {
                this.customConfig.set(newPath, List.of(oldFavorite));
                this.customConfig.set(oldPath, null);
                this.plugin.saveCustomConfig();
            }
        }

        return this.customConfig.getStringList(newPath);
    }

    public boolean isFavorite(Player player, String name) {
        return getFavorites(player).contains(name);
    }

    public boolean addFavorite(Player player, String name) {
        String uuid = player.getUniqueId().toString();
        if (!this.customConfig.contains(uuid + ".warp." + name)) return false;

        List<String> favorites = new ArrayList<>(getFavorites(player));
        if (favorites.contains(name)) return false;

        favorites.add(name);
        this.customConfig.set(uuid + "." + FAVORITES_KEY, favorites);
        this.plugin.saveCustomConfig();
        return true;
    }

    public boolean removeFavorite(Player player, String name) {
        String uuid = player.getUniqueId().toString();
        List<String> favorites = new ArrayList<>(getFavorites(player));

        if (!favorites.remove(name)) return false;

        this.customConfig.set(uuid + "." + FAVORITES_KEY, favorites.isEmpty() ? null : favorites);
        this.plugin.saveCustomConfig();
        return true;
    }

    public boolean toggleFavorite(Player player, String name) {
        if (isFavorite(player, name)) {
            return removeFavorite(player, name);
        } else {
            return addFavorite(player, name);
        }
    }

    // --- Destination ---

    public boolean addSpecialWarp(Player player, String name, WarpType warpType) {
        String uuid = player.getUniqueId().toString();
        if (!this.customConfig.contains(uuid + ".warp." + name)) return false;

        this.customConfig.set(uuid + "." + warpType.type, name);
        this.plugin.saveCustomConfig();
        return true;
    }

    public boolean removeSpecialWarp(Player player, WarpType warpType) {
        String uuid = player.getUniqueId().toString();
        String path = uuid + "." + warpType.type;

        if (!this.customConfig.contains(path)) return false;

        this.customConfig.set(path, null);
        this.plugin.saveCustomConfig();
        return true;
    }

    // --- Coord read/write ---

    public Location getCoord(Player player, String name) {
        String uuid = player.getUniqueId().toString();
        String path = uuid + ".warp." + name;

        if (!this.customConfig.contains(path)) return null;

        String worldName = this.customConfig.getString(path + ".world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        double x = this.customConfig.getDouble(path + ".x");
        double y = this.customConfig.getDouble(path + ".y");
        double z = this.customConfig.getDouble(path + ".z");

        return new Location(world, x, y, z);
    }

    public Map<String, Location> getCoordList(Player player) {
        Map<String, Location> coordLocations = new LinkedHashMap<>();
        String uuid = player.getUniqueId().toString();
        ConfigurationSection section = this.customConfig.getConfigurationSection(uuid + ".warp");

        if (section == null) return null;

        Set<String> coords = section.getKeys(false);
        if (coords.isEmpty()) return null;

        for (String name : coords) {
            Location location = getCoord(player, name);
            if (location != null) {
                coordLocations.put(name, location);
            }
        }

        return coordLocations.isEmpty() ? null : coordLocations;
    }

    public boolean deleteCoord(Player player, String name) {
        String uuid = player.getUniqueId().toString();
        if (!this.customConfig.contains(uuid + ".warp." + name)) return false;

        this.customConfig.set(uuid + ".warp." + name, null);

        // Remove from favorites list
        List<String> favorites = new ArrayList<>(getFavorites(player));
        if (favorites.remove(name)) {
            this.customConfig.set(uuid + "." + FAVORITES_KEY, favorites.isEmpty() ? null : favorites);
        }

        // Remove destination if it was this coord
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;
        if (Objects.equals(this.customConfig.getString(destinationPath), name)) {
            this.customConfig.set(destinationPath, null);
        }

        this.plugin.saveCustomConfig();
        return true;
    }
}
