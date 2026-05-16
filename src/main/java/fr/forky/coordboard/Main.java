package fr.forky.coordboard;

import fr.forky.coordboard.commands.CoordCommand;
import fr.forky.coordboard.commands.CoordTabCompleter;
import fr.forky.coordboard.commands.ListPlayer;
import fr.forky.coordboard.listeners.*;
import fr.forky.coordboard.utils.player.CoordManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin {
    private File customConfigFile;
    private static FileConfiguration customConfig = null;
    private static CoordManager coordManager;

    @Override
    public void onEnable() {
        getLogger().info("ENABLING COORDBOARD V1...");

        createCustomConfig();

        coordManager = new CoordManager(this);

        getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorld(), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        getServer().getPluginManager().registerEvents(new EntityRegainHeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(coordManager), this);
        Objects.requireNonNull(getCommand("playerlist")).setExecutor(new ListPlayer());
        CoordCommand coordCommand = new CoordCommand(coordManager);
        Objects.requireNonNull(getCommand("coord")).setExecutor(coordCommand);
        Objects.requireNonNull(getCommand("coord")).setTabCompleter(new CoordTabCompleter(coordManager));

        getLogger().info("COORDBOARD V1 ENABLED !");
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "coords.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("coords.yml", false);
        }

        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public static FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public void saveCustomConfig() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                customConfig.save(customConfigFile);
            } catch (IOException e) {
                getLogger().severe("An error occurred when trying to save custom config file");
            }
        });
    }

    public static CoordManager getCoordManager() {
        return coordManager;
    }
}
