package fr.forky.coordboard.utils.player;

import fr.forky.coordboard.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class CoordManager {
    private final Main plugin;

    public CoordManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addCoord(Player player, String name) {
        FileConfiguration customConfig = plugin.getCustomConfig();
        Location location = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String path = uuid + "." + name;
        customConfig.set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
        customConfig.set(path + ".x", location.getX());
        customConfig.set(path + ".y", location.getY());
        customConfig.set(path + ".z", location.getZ());
        plugin.saveCustomConfig();
    }

    public Location getCoord(Player player, String name) {
        FileConfiguration customConfig = plugin.getCustomConfig();
        String uuid = player.getUniqueId().toString();
        String path = uuid + "." + name;

        if (!customConfig.contains(path)) {
            return null;
        }

        String worldName = customConfig.getString(path + ".world");
        double x = customConfig.getDouble(path + ".x");
        double y = customConfig.getDouble(path + ".y");
        double z = customConfig.getDouble(path + ".z");
        assert worldName != null;
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public Map<String, Location> getCoordList(Player player) {
        FileConfiguration config = plugin.getCustomConfig();
        Map<String, Location> coordLocations = new HashMap<>();
        String uuid = player.getUniqueId().toString();

        ConfigurationSection section = config.getConfigurationSection(uuid);
        if (section == null) {
            return null;
        }

        Set<String> coords = section.getKeys(false);
        if (coords.isEmpty()) {
            return null;
        }

        for (String name : coords) {
            String worldName = config.getString(uuid + "." + name + ".world");
            double x = config.getDouble(uuid + "." + name + ".x");
            double y = config.getDouble(uuid + "." + name + ".y");
            double z = config.getDouble(uuid + "." + name + ".z");
            assert worldName != null;
            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
            coordLocations.put(name, location);
        }

        return coordLocations;
    }

    public boolean deleteCoord(Player player, String name) {
        FileConfiguration config = plugin.getCustomConfig();
        String uuid = player.getUniqueId().toString();

        if (config.contains(uuid + "." + name)) {
            config.set(uuid + "." + name, null);
            plugin.saveCustomConfig();

            return true;
        } else {
            return false;
        }
    }
}
