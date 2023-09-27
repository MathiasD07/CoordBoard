package fr.forky.coordboard.listeners;

import fr.forky.coordboard.utils.player.CoordManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
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

        TextComponent messagePart1 = new TextComponent("Vous êtes morts ! Souhaitez-vous lancer le GPS vers le lieu de votre mort ? ");
        messagePart1.setBold(true);
        messagePart1.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);

        TextComponent clickablePart = new TextComponent("✓");
        clickablePart.setClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/coord go Mort"
                )
        );
        clickablePart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Cliquez pour lancer le GPS")));

        clickablePart.setBold(true);
        clickablePart.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        messagePart1.addExtra(clickablePart);

        player.spigot().sendMessage(messagePart1);
    }
 }
