package me.whereareiam.socialismus.api;

/**
 * Enum representing ANSI color codes for terminal text coloring.
 */
public enum AnsiColor {
    /** Resets all colors and styles */
    RESET("\u001B[0m"),
    /** Black text color */
    BLACK("\u001B[30m"),
    /** Gray text color */
    GRAY("\u001B[37;1m"),
    /** Red text color */
    RED("\u001B[31m"),
    /** Orange text color */
    ORANGE("\u001B[38;5;208m"),
    /** Green text color */
    GREEN("\u001B[32m"),
    /** Yellow text color */
    YELLOW("\u001B[33m"),
    /** Blue text color */
    BLUE("\u001B[34m"),
    /** Purple text color */
    PURPLE("\u001B[35m"),
    /** Cyan text color */
    CYAN("\u001B[36m"),
    /** White text color */
    WHITE("\u001B[37m");

    private final String color;

    AnsiColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return this.color;
    }
}