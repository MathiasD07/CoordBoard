package fr.forky.coordboard.enums;

public enum ArrowDirection {
    UP_LEFT("⬉"),
    UP("↑"),
    UP_RIGHT("⬈"),
    RIGHT("→"),
    DOWN_RIGHT("⬊"),
    DOWN("↓"),
    DOWN_LEFT("⬋"),
    LEFT("←");

    public final String arrow;

    private ArrowDirection(String arrow) {
        this.arrow = arrow;
    }
}

//→←↑↓⬈⬉⬊⬋