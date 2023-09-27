package fr.forky.coordboard.utils.player;

import fr.forky.coordboard.Main;
import fr.forky.coordboard.enums.WarpType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class CoordManager {
    private final Main plugin;
    private final FileConfiguration customConfig;

    public CoordManager(Main plugin)
    {
        this.plugin = plugin;
        this.customConfig = Main.getCustomConfig();
    }

    public void addCoord(Player player, String name)
    {
        Location location = player.getLocation();
        String uuid = player.getUniqueId().toString();
        String path = uuid + ".warp." + name;

        this.customConfig.set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
        this.customConfig.set(path + ".x", location.getX());
        this.customConfig.set(path + ".y", location.getY());
        this.customConfig.set(path + ".z", location.getZ());

        this.plugin.saveCustomConfig();
    }

    public boolean addSpecialWarp(Player player, String name, WarpType warpType)
    {
        String uuid = player.getUniqueId().toString();
        String warpPath = uuid + ".warp." + name;
        String favoritePath = uuid + "." + warpType.type;

        if (!this.customConfig.contains(warpPath)) {
            return false;
        }

        this.customConfig.set(favoritePath, name);
        this.plugin.saveCustomConfig();

        return true;
    }

    public boolean removeSpecialWarp(Player player, WarpType warpType)
    {
        String uuid = player.getUniqueId().toString();

        String spacialWarpPath = uuid + "." + warpType.type;

        if (this.customConfig.contains(spacialWarpPath)) {
            this.customConfig.set(spacialWarpPath, null);
            plugin.saveCustomConfig();

            return true;
        }

        return false;
    }

    public Location getCoord(Player player, String name)
    {
        String uuid = player.getUniqueId().toString();
        String path = uuid + ".warp." + name;

        if (!this.customConfig.contains(path)) {
            return null;
        }

        String worldName = this.customConfig.getString(path + ".world");
        double x = this.customConfig.getDouble(path + ".x");
        double y = this.customConfig.getDouble(path + ".y");
        double z = this.customConfig.getDouble(path + ".z");
        assert worldName != null;

        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public Map<String, Location> getCoordList(Player player)
    {
        Map<String, Location> coordLocations = new HashMap<>();
        String uuid = player.getUniqueId().toString();
        ConfigurationSection section = this.customConfig.getConfigurationSection(uuid + ".warp");

        if (section == null) {
            return null;
        }

        Set<String> coords = section.getKeys(false);

        if (coords.isEmpty()) {
            return null;
        }

        for (String name : coords) {
            String path = uuid + ".warp." + name;
            String worldName = this.customConfig.getString(path + ".world");
            double x = this.customConfig.getDouble(path + ".x");
            double y = this.customConfig.getDouble(path + ".y");
            double z = this.customConfig.getDouble(path + ".z");
            assert worldName != null;
            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
            coordLocations.put(name, location);
        }

        return coordLocations;
    }

    public boolean deleteCoord(Player player, String name)
    {
        String uuid = player.getUniqueId().toString();

        if (!this.customConfig.contains(uuid + ".warp." + name)) {
            return false;
        }

        this.customConfig.set(uuid + ".warp." + name, null);

        String favoritePath = uuid + "." + WarpType.FAVORITE.type;
        String destinationPath = uuid + "." + WarpType.DESTINATION.type;

        if (this.customConfig.contains(favoritePath) && Objects.equals(this.customConfig.getString(favoritePath), name)) {
            this.customConfig.set(favoritePath, null);
        }

        if (this.customConfig.contains(destinationPath) && Objects.equals(this.customConfig.getString(destinationPath), name)) {
            this.customConfig.set(destinationPath, null);
        }

        plugin.saveCustomConfig();

        return true;
    }
}
