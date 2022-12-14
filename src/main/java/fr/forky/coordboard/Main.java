package fr.forky.coordboard;

import fr.forky.coordboard.commands.CommandBroadcast;
import fr.forky.coordboard.commands.ListPlayer;
import fr.forky.coordboard.listeners.PlayerChangeWorld;
import fr.forky.coordboard.listeners.PlayerConnection;
import fr.forky.coordboard.listeners.PlayerLeave;
import fr.forky.coordboard.listeners.PlayerMove;
import fr.forky.coordboard.utils.commands.SimpleCommand;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("ça marche !");

        createCommand(new SimpleCommand(
                "broadcast",
                "Envoyer un message à tout le serveur",
                new CommandBroadcast(),
                "bc"
        ));

        getServer().getPluginManager().registerEvents(new PlayerConnection(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);
        getServer().getPluginManager().registerEvents(new PlayerChangeWorld(), this);
        getCommand("playerlist").setExecutor(new ListPlayer());
    }

    private void createCommand(SimpleCommand command) {
        CraftServer server = (CraftServer) getServer();
        server.getCommandMap().register(getName(), command);
    }
}
