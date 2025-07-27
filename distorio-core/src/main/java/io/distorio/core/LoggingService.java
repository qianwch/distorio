package io.distorio.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingService {
    
    private static final Logger logger = Logger.getLogger("io.distorio");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log an info message
     */
    public static void info(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.info(String.format("[%s] %s", timestamp, message));
    }
    
    /**
     * Log a warning message
     */
    public static void warning(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.warning(String.format("[%s] %s", timestamp, message));
    }
    
    /**
     * Log an error message
     */
    public static void error(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.severe(String.format("[%s] %s", timestamp, message));
    }
    
    /**
     * Log an error message with exception
     */
    public static void error(String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.log(Level.SEVERE, String.format("[%s] %s", timestamp, message), throwable);
    }
    
    /**
     * Log a debug message
     */
    public static void debug(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        logger.fine(String.format("[%s] %s", timestamp, message));
    }
    
    /**
     * Log operation execution
     */
    public static void logOperation(String operationName, String details) {
        info(String.format("Operation: %s - %s", operationName, details));
    }
    
    /**
     * Log file operation
     */
    public static void logFileOperation(String operation, String filePath) {
        info(String.format("File %s: %s", operation, filePath));
    }
    
    /**
     * Log image operation
     */
    public static void logImageOperation(String operation, int width, int height) {
        info(String.format("Image %s: %dx%d", operation, width, height));
    }
    
    /**
     * Log user action
     */
    public static void logUserAction(String action, String details) {
        debug(String.format("User action: %s - %s", action, details));
    }
    
    /**
     * Log performance metric
     */
    public static void logPerformance(String operation, long durationMs) {
        debug(String.format("Performance: %s took %dms", operation, durationMs));
    }
    
    /**
     * Set logging level
     */
    public static void setLogLevel(Level level) {
        logger.setLevel(level);
    }
    
    /**
     * Get the logger instance
     */
    public static Logger getLogger() {
        return logger;
    }
} 