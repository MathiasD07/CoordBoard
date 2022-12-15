package fr.forky.coordboard.utils.maths;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LocationMath {
    static public double vector2Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    static public double angleLookToPoint(Player sourcePlayer, Location toPlayerLocation) {
        Vector inBetween = toPlayerLocation.clone().subtract(sourcePlayer.getLocation()).toVector();
        Vector lookVec = sourcePlayer.getEyeLocation().getDirection();

        double angleDir = (Math.atan2(inBetween.getZ(),inBetween.getX()) / 2 / Math.PI * 360 + 360) % 360;
        double angleLook = (Math.atan2(lookVec.getZ(),lookVec.getX()) / 2 / Math.PI * 360 + 360) % 360;
        return (angleDir - angleLook + 360) % 360;
    }
}
