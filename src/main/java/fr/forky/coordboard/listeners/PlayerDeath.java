package fr.forky.coordboard.listeners;

import fr.forky.coordboard.utils.player.CoordManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    private final CoordManager coordManager;

    public PlayerDeath(CoordManager coordManager) {
        this.coordManager = coordManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        coordManager.addCoord(player, "Mort");

        Component message = Component.text("Vous êtes morts ! Souhaitez-vous lancer le GPS vers le lieu de votre mort ? ")
                .color(NamedTextColor.DARK_RED)
                .decorate(TextDecoration.BOLD)
                .append(
                        Component.text("✓")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.runCommand("/coord go Mort"))
                                .hoverEvent(HoverEvent.showText(Component.text("Cliquez pour lancer le GPS")))
                );

        player.sendMessage(message);
    }
}
