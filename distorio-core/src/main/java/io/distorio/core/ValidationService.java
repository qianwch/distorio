package io.distorio.core;

import java.io.File;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;

public class ValidationService {
    
    /**
     * Validate if a file is a supported image format
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String name = file.getName().toLowerCase();
        String[] suffixes = ImageIO.getReaderFileSuffixes();
        for (String ext : suffixes) {
            if (name.endsWith("." + ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate if an image is not null and has valid dimensions
     */
    public static boolean isValidImage(Image image) {
        return image != null && image.getWidth() > 0 && image.getHeight() > 0;
    }
    
    /**
     * Validate if a string is not null or empty
     */
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate if a number is within a valid range
     */
    public static boolean isValidRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validate if a number is positive
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }
    
    /**
     * Validate if a number is non-negative
     */
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }
    
    /**
     * Validate if coordinates are within image bounds
     */
    public static boolean isValidImageCoordinates(double x, double y, double width, double height, Image image) {
        if (!isValidImage(image)) {
            return false;
        }
        
        return isNonNegative(x) && isNonNegative(y) && 
               isPositive(width) && isPositive(height) &&
               x + width <= image.getWidth() && 
               y + height <= image.getHeight();
    }
    
    /**
     * Validate if a file path is valid
     */
    public static boolean isValidFilePath(String path) {
        if (!isValidString(path)) {
            return false;
        }
        
        try {
            File file = new File(path);
            return file.getParentFile() == null || file.getParentFile().exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate if a directory is writable
     */
    public static boolean isWritableDirectory(File directory) {
        return directory != null && directory.exists() && 
               directory.isDirectory() && directory.canWrite();
    }
} 