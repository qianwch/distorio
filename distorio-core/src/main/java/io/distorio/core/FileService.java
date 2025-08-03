package io.distorio.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;

public class FileService {
    public static Image openImageFile(File file) throws Exception {
        long start = System.currentTimeMillis();
        Image image = OpenCVUtils.openImageWithOpenCV(file);
        long end = System.currentTimeMillis();
        System.out.println("Time taken to read image with optimized OpenCV: " + (end - start) + "ms");
        if (image != null) {
            return image;
        } else {
            throw new Exception("Unsupported Image Format or Failed to Open Image");
        }
    }

    public static boolean isImageFile(File file) {
        return ValidationService.isValidImageFile(file);
    }

    public static boolean saveImageToFile(Image img, File file) throws Exception {
        if (img == null || file == null) return false;
        
        // Use OpenCV for saving images, which provides better performance and format support
        return OpenCVUtils.saveImageWithOpenCV(img, file);
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
        
        // OpenCV supports many image formats, create a comprehensive filter
        String[] opencvFormats = {
            "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.tiff", "*.tif", 
            "*.gif", "*.webp", "*.jp2", "*.pbm", "*.pgm", "*.ppm"
        };
        
        // Create a single filter with all OpenCV supported image formats
        String description = "All Image Files (" + String.join(", ", opencvFormats) + ")";
        filters.add(new FileChooser.ExtensionFilter(description, opencvFormats));
        
        return filters;
    }

    public static List<FileChooser.ExtensionFilter> getWriteFileFilters() {
        // List OpenCV supported output formats
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        filters.add(new FileChooser.ExtensionFilter("JPEG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        filters.add(new FileChooser.ExtensionFilter("JPEG 2000 (*.jp2)", "*.jp2"));
        filters.add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));
        filters.add(new FileChooser.ExtensionFilter("GIF (*.gif)", "*.gif"));
        filters.add(new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp"));
        filters.add(new FileChooser.ExtensionFilter("TIFF (*.tiff, *.tif)", "*.tiff", "*.tif"));
        filters.add(new FileChooser.ExtensionFilter("WebP (*.webp)", "*.webp"));
        filters.add(new FileChooser.ExtensionFilter("PBM/PGM/PPM (*.pbm, *.pgm, *.ppm)", "*.pbm", "*.pgm", "*.ppm"));
        
        return filters;
    }
    
} 