package fr.forky.coordboard;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Stack;

public class PlayerList {
    public Stack<Player> list;
    private static PlayerList instance;
    private PlayerList() {
        list = new Stack<>();
    }

    public static PlayerList getInstance() {
        if (instance == null) {
            instance = new PlayerList();
        }
        return instance;
    }
    public void addPlayer(Player player) {
        if (list.search(player) < 0) {
            list.push(player);
        }
    }

    public void removePlayer(Player player) {
        int exist = list.search(player);

        if (exist >= 0) {
            System.out.println("player " + player.getName() + " is removed from the list !");
            list.remove(player);
        }
    }

    public String getPlayerList() {
        String str = "";

        for (int i = 0; i < list.size(); i++) {
            str = str + list.get(i).getName();
//            str = str + "\n";
        }

        return str;
    }
}
