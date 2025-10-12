package util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Small logger helper to provide consistent logger config.
 */
public final class LoggerUtil {
    private LoggerUtil() { }

    public static Logger getLogger(Class<?> cls) {
        Logger logger = Logger.getLogger(cls.getName());
        // minimal configuration — avoid adding multiple handlers repeatedly
        if (logger.getHandlers().length == 0) {
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.INFO);
            logger.addHandler(ch);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);
        }
        return logger;
    }
}
