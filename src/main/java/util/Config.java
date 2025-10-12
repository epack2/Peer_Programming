package util;


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
