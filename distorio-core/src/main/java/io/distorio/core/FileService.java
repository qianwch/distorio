package io.distorio.core;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileService {
    public static Image openImageFile(File file) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(file);
        if (bufferedImage != null) {
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } else {
            throw new Exception("Unsupported Image Format");
        }
    }

    public static boolean isImageFile(File file) {
        return ValidationService.isValidImageFile(file);
    }

    public static boolean saveImageToFile(Image img, File file) throws Exception {
        String ext = getImageExtension(file.getName());
        if (img == null || file == null) return false;
        if (ext.equals("jpg") || ext.equals("jpeg")) {
            BufferedImage rgbImage = toBufferedImageNoAlpha(img);
            ImageIO.write(rgbImage, "jpg", file);
        } else {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), ext, file);
        }
        return true;
    }

    public static String getImageExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot >= 0) ? fileName.substring(dot + 1).toLowerCase() : "png";
    }

    public static BufferedImage toBufferedImageNoAlpha(Image img) {
        return ImageUtils.toBufferedImageNoAlpha(img);
    }

    public static List<FileChooser.ExtensionFilter> getImageExtensionFilters() {
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        
        // Create a single filter for all image formats that ImageIO can read
        String[] readerSuffixes = ImageIO.getReaderFileSuffixes();
        List<String> extensions = new ArrayList<>();
        for (String ext : readerSuffixes) {
            extensions.add("*." + ext.toLowerCase());
        }
        
        // Create a single filter with all readable image formats
        String description = "All Image Files (" + String.join(", ", extensions) + ")";
        filters.add(new FileChooser.ExtensionFilter(description, extensions.toArray(new String[0])));
        
        return filters;
    }

    public static List<FileChooser.ExtensionFilter> getWriteFileFilters() {
        // List common types first
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        filters.add(new FileChooser.ExtensionFilter("JPEG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        filters.add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));
        filters.add(new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp"));
        filters.add(new FileChooser.ExtensionFilter("GIF (*.gif)", "*.gif"));
    
        // Add less common types from ImageIO, skipping those already added
        Set<String> commonExts = Set.of("png", "jpg", "jpeg", "bmp", "gif");
        String[] suffixes = ImageIO.getWriterFileSuffixes();
        for (String ext : suffixes) {
          String lower = ext.toLowerCase();
          if (!commonExts.contains(lower)) {
            filters.add(new FileChooser.ExtensionFilter((lower.toUpperCase() + " (*." + lower + ")"), "*." + lower));
          }
        }
        return filters;
      }
    
} 