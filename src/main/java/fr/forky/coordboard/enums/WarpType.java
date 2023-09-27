package fr.forky.coordboard.enums;

public enum WarpType
{
    FAVORITE("favoriteWarp"),
    DESTINATION("destinationWarp");

    public final String type;

    private WarpType(String type) {
        this.type = type;
    }
}