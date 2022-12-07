package fr.forky.coordboard;

import fr.forky.coordboard.commands.CommandBroadcast;
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
    }

    private void createCommand(SimpleCommand command) {
        CraftServer server = (CraftServer) getServer();
        server.getCommandMap().register(getName(), command);
    }
}