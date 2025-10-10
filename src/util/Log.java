package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Log {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void info(String s) { System.err.println("[INFO] " + F.format(LocalDateTime.now()) + " " + s); }
    public static void warn(String s) { System.err.println("[WARN] " + F.format(LocalDateTime.now()) + " " + s); }
    public static void error(String s) { System.err.println("[ERROR] " + F.format(LocalDateTime.now()) + " " + s); }
}
