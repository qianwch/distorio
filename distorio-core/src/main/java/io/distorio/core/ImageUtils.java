package io.distorio.core;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

public class ImageUtils {
    
    /**
     * Convert JavaFX Image to BufferedImage
     */
    public static BufferedImage toBufferedImage(Image image) {
        return SwingFXUtils.fromFXImage(image, null);
    }
    
    /**
     * Convert BufferedImage to JavaFX Image
     */
    public static Image toFXImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    
    /**
     * Create a copy of an image
     */
    public static Image copyImage(Image source) {
        if (source == null) return null;
        
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        
        WritableImage copy = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = copy.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, y, reader.getArgb(x, y));
            }
        }
        
        return copy;
    }
    
    /**
     * Crop an image to the specified region
     */
    public static Image cropImage(Image source, int x, int y, int width, int height) {
        if (source == null || width <= 0 || height <= 0) return null;
        
        // Ensure crop region is within image bounds
        int sourceWidth = (int) source.getWidth();
        int sourceHeight = (int) source.getHeight();
        
        x = Math.max(0, Math.min(x, sourceWidth - 1));
        y = Math.max(0, Math.min(y, sourceHeight - 1));
        width = Math.min(width, sourceWidth - x);
        height = Math.min(height, sourceHeight - y);
        
        if (width <= 0 || height <= 0) return null;
        
        WritableImage cropped = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = cropped.getPixelWriter();
        
        for (int cy = 0; cy < height; cy++) {
            for (int cx = 0; cx < width; cx++) {
                writer.setArgb(cx, cy, reader.getArgb(x + cx, y + cy));
            }
        }
        
        return cropped;
    }
    
    /**
     * Resize an image to the specified dimensions
     */
    public static Image resizeImage(Image source, int newWidth, int newHeight) {
        if (source == null || newWidth <= 0 || newHeight <= 0) return null;
        
        WritableImage resized = new WritableImage(newWidth, newHeight);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = resized.getPixelWriter();
        
        double scaleX = source.getWidth() / (double) newWidth;
        double scaleY = source.getHeight() / (double) newHeight;
        
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int srcX = (int) (x * scaleX);
                int srcY = (int) (y * scaleY);
                writer.setArgb(x, y, reader.getArgb(srcX, srcY));
            }
        }
        
        return resized;
    }
    
    /**
     * Flip an image horizontally
     */
    public static Image flipHorizontal(Image source) {
        if (source == null) return null;
        
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        
        WritableImage flipped = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = flipped.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(width - 1 - x, y, reader.getArgb(x, y));
            }
        }
        
        return flipped;
    }
    
    /**
     * Flip an image vertically
     */
    public static Image flipVertical(Image source) {
        if (source == null) return null;
        
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        
        WritableImage flipped = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = flipped.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(x, height - 1 - y, reader.getArgb(x, y));
            }
        }
        
        return flipped;
    }
    
    /**
     * Rotate an image by 90 degrees clockwise
     */
    public static Image rotate90Clockwise(Image source) {
        if (source == null) return null;
        
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        
        WritableImage rotated = new WritableImage(height, width);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = rotated.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(height - 1 - y, x, reader.getArgb(x, y));
            }
        }
        
        return rotated;
    }
    
    /**
     * Rotate an image by 90 degrees counter-clockwise
     */
    public static Image rotate90CounterClockwise(Image source) {
        if (source == null) return null;
        
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        
        WritableImage rotated = new WritableImage(height, width);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = rotated.getPixelWriter();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(y, width - 1 - x, reader.getArgb(x, y));
            }
        }
        
        return rotated;
    }
    
    /**
     * Create a BufferedImage without alpha channel (RGB format)
     */
    public static BufferedImage toBufferedImageNoAlpha(Image img) {
        BufferedImage src = SwingFXUtils.fromFXImage(img, null);
        BufferedImage rgbImage = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return rgbImage;
    }
} 