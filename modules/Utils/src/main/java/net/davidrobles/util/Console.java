package net.davidrobles.util;

public final class Console {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    private Console() {}

    public static String fmt(String text, String... codes) {
        StringBuilder sb = new StringBuilder();
        for (String code : codes) sb.append(code);
        sb.append(text);
        sb.append(RESET);
        return sb.toString();
    }

    public static String bold(String text) {
        return fmt(text, BOLD);
    }

    public static String dim(String text) {
        return fmt(text, DIM);
    }

    public static String red(String text) {
        return fmt(text, BRIGHT_RED);
    }

    public static String green(String text) {
        return fmt(text, BRIGHT_GREEN);
    }

    public static String yellow(String text) {
        return fmt(text, BRIGHT_YELLOW);
    }

    public static String blue(String text) {
        return fmt(text, BRIGHT_BLUE);
    }

    public static String cyan(String text) {
        return fmt(text, BRIGHT_CYAN);
    }

    public static String magenta(String text) {
        return fmt(text, MAGENTA);
    }

    public static void header(String text) {
        String line = "─".repeat(52);
        System.out.println(fmt(line, CYAN));
        System.out.println(fmt("  " + text, BOLD, BRIGHT_CYAN));
        System.out.println(fmt(line, CYAN));
    }

    public static void separator() {
        System.out.println(fmt("┄".repeat(52), DIM));
    }
}
