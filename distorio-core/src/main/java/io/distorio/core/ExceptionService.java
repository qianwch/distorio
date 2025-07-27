package io.distorio.core;

import java.io.File;

public class ExceptionService {
    
    /**
     * Handle file operation exceptions
     */
    public static void handleFileException(String operation, File file, Exception e) {
        String message = String.format("Failed to %s file: %s", operation, file != null ? file.getPath() : "null");
        LoggingService.error(message, e);
    }
    
    /**
     * Handle image operation exceptions
     */
    public static void handleImageException(String operation, Exception e) {
        String message = String.format("Failed to perform image operation: %s", operation);
        LoggingService.error(message, e);
    }
    
    /**
     * Handle validation exceptions
     */
    public static void handleValidationException(String field, String reason, Exception e) {
        String message = String.format("Validation failed for %s: %s", field, reason);
        LoggingService.error(message, e);
    }
    
    /**
     * Handle configuration exceptions
     */
    public static void handleConfigException(String key, Exception e) {
        String message = String.format("Configuration error for key '%s'", key);
        LoggingService.error(message, e);
    }
    
    /**
     * Handle UI operation exceptions
     */
    public static void handleUIException(String operation, Exception e) {
        String message = String.format("UI operation failed: %s", operation);
        LoggingService.error(message, e);
    }
    
    /**
     * Handle plugin loading exceptions
     */
    public static void handlePluginException(String pluginName, Exception e) {
        String message = String.format("Plugin loading failed: %s", pluginName);
        LoggingService.error(message, e);
    }
    
    /**
     * Create a user-friendly error message
     */
    public static String createUserFriendlyMessage(Throwable e) {
        if (e instanceof java.io.FileNotFoundException) {
            return "File not found. Please check if the file exists and you have permission to access it.";
        } else if (e instanceof java.io.IOException) {
            return "Unable to read or write the file. Please check if the file is not being used by another application.";
        } else if (e instanceof java.lang.OutOfMemoryError) {
            return "Not enough memory to process this image. Try using a smaller image or closing other applications.";
        } else if (e instanceof java.lang.IllegalArgumentException) {
            return "Invalid operation parameters. Please check your input and try again.";
        } else if (e instanceof java.lang.UnsupportedOperationException) {
            return "This operation is not supported for this image format.";
        } else {
            return "An unexpected error occurred. Please try again or contact support if the problem persists.";
        }
    }
    
    /**
     * Check if an exception is recoverable
     */
    public static boolean isRecoverable(Throwable e) {
        return !(e instanceof java.lang.OutOfMemoryError) && 
               !(e instanceof java.lang.VirtualMachineError);
    }
    
    /**
     * Log and rethrow an exception with additional context
     */
    public static RuntimeException logAndRethrow(String context, Exception e) {
        LoggingService.error(context, e);
        return new RuntimeException(context, e);
    }
} 