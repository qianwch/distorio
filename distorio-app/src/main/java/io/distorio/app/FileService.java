package io.distorio.app;

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
        String name = file.getName().toLowerCase();
        String[] suffixes = ImageIO.getReaderFileSuffixes();
        for (String ext : suffixes) {
            if (name.endsWith("." + ext.toLowerCase())) {
                return true;
            }
        }
        return false;
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

    public static List<FileChooser.ExtensionFilter> getImageExtensionFilters() {
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        Set<String> added = new java.util.HashSet<>();
        String[] suffixes = ImageIO.getWriterFileSuffixes();
        for (String ext : suffixes) {
            String lower = ext.toLowerCase();
            if (!added.contains(lower)) {
                filters.add(new FileChooser.ExtensionFilter((lower.toUpperCase() + " (*." + lower + ")"), "*." + lower));
                added.add(lower);
            }
        }
        return filters;
    }
} 