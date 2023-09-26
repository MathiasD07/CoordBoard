package fr.forky.coordboard;

import fr.forky.coordboard.commands.CoordCommand;
import fr.forky.coordboard.commands.ListPlayer;
import fr.forky.coordboard.listeners.*;
import fr.forky.coordboard.utils.commands.SimpleCommand;
import fr.forky.coordboard.utils.player.CoordManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends JavaPlugin {
    private File customConfigFile;
    private FileConfiguration customConfig;
    private CoordManager coordManager;

    @Override
    public void onEnable() {
        System.out.println("ENABLING COORDBOARD V1...");

        createCustomConfig();

        this.coordManager = new CoordManager(this);

        getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorld(), this);
        getServer().getPluginManager().registerEvents(new EntityDamage(), this);
        getServer().getPluginManager().registerEvents(new EntityRegainHeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(coordManager), this);
        Objects.requireNonNull(getCommand("playerlist")).setExecutor(new ListPlayer());
        Objects.requireNonNull(getCommand("coord")).setExecutor(new CoordCommand(coordManager));

        System.out.println("COORDBOARD V1 ENABLED !");
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "coords.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("coords.yml", false);
        }

        this.customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    public void saveCustomConfig() {
        try {
            this.customConfig.save(customConfigFile);
        } catch (IOException e) {
            System.out.println("An error occurred when trying to save custom config file");
        }
    }

    public CoordManager getCoordManager() {
        return this.coordManager;
    }

    private void createCommand(SimpleCommand command) {
        CraftServer server = (CraftServer) getServer();
        server.getCommandMap().register(getName(), command);
    }
}
