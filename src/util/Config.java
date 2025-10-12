package util;

/**
 * Configuration helper: checks system properties then environment variables, otherwise default.
 * Keys:
 *   students.file
 *   courses.file
 *   enrollments.file
 */
public final class Config {
    private Config() { }

    public static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.trim().isEmpty()) return sys;
        String env = System.getenv(key.replace('.', '_').toUpperCase());
        if (env != null && !env.trim().isEmpty()) return env;
        return defaultValue;
    }
}
