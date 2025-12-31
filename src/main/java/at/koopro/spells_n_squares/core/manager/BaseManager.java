package at.koopro.spells_n_squares.core.manager;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Base class for manager classes with common patterns.
 * Provides logging, validation, and common functionality.
 */
public abstract class BaseManager {
    protected final Logger logger;
    
    protected BaseManager() {
        this.logger = LogUtils.getLogger();
    }
    
    /**
     * Gets the logger for this manager.
     * @return The logger
     */
    protected Logger getLogger() {
        return logger;
    }
    
    /**
     * Validates that an object is not null.
     * @param obj The object to validate
     * @param name The name of the object (for error messages)
     * @throws IllegalArgumentException if the object is null
     */
    protected void validateNotNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }
    
    /**
     * Logs a debug message.
     * @param message The message
     * @param args The message arguments
     */
    protected void debug(String message, Object... args) {
        logger.debug(message, args);
    }
    
    /**
     * Logs an info message.
     * @param message The message
     * @param args The message arguments
     */
    protected void info(String message, Object... args) {
        logger.info(message, args);
    }
    
    /**
     * Logs a warning message.
     * @param message The message
     * @param args The message arguments
     */
    protected void warn(String message, Object... args) {
        logger.warn(message, args);
    }
    
    /**
     * Logs an error message.
     * @param message The message
     * @param args The message arguments
     */
    protected void error(String message, Object... args) {
        logger.error(message, args);
    }
    
    /**
     * Logs an error message with exception.
     * @param message The message
     * @param throwable The exception
     * @param args The message arguments
     */
    protected void error(String message, Throwable throwable, Object... args) {
        logger.error(message, args, throwable);
    }
}


