package fr.forky.coordboard.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SimpleCommand extends Command {

    private final CommandExecutor executor;

    public SimpleCommand(
            String name,
            String description,
            CommandExecutor executor,
            String... aliases
    ) {
        super(name, description, "", Arrays.asList(aliases));

        this.executor = executor;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return executor.onCommand(sender, this, commandLabel, args);
    }
}
