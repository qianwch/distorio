package io.distorio.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import nu.pattern.OpenCV;

public class OpenCVUtils {

  private static boolean opencvLoaded = false;

  static {
    // Load OpenCV native library
    loadOpenCV();
  }

  /**
   * Load OpenCV library if not already loaded
   */
  private static void loadOpenCV() {
    if (!opencvLoaded) {
      try {
        OpenCV.loadLocally();
        opencvLoaded = true;
        System.out.println("OpenCV library loaded successfully");
      } catch (Exception e) {
        System.err.println("Failed to load OpenCV library: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * Ensure OpenCV is loaded before operations
   */
  private static void ensureOpenCVLoaded() {
    if (!opencvLoaded) {
      loadOpenCV();
    }
  }

  /**
   * Preload OpenCV library. Call this during application startup to avoid
   * first-time loading delays when opening images.
   */
  public static void preloadOpenCV() {
    loadOpenCV();
  }

  /**
   * Optimized BufferedImage to Mat conversion, directly manipulates pixel data to avoid PNG
   * encoding/decoding
   */
  public static Mat bufferedImageToMat(BufferedImage bi) {
    // Ensure the image is in BGR format (OpenCV default format)
    BufferedImage bgrImage = bi;
    if (bi.getType() != BufferedImage.TYPE_3BYTE_BGR) {
      bgrImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
      bgrImage.getGraphics().drawImage(bi, 0, 0, null);
    }

    // Directly obtain pixel data
    byte[] pixels = ((DataBufferByte) bgrImage.getRaster().getDataBuffer()).getData();

    // Create Mat object, directly using BGR format
    Mat mat = new Mat(bgrImage.getHeight(), bgrImage.getWidth(), CvType.CV_8UC3);
    mat.put(0, 0, pixels);

    return mat;
  }

  /**
   * Optimized Mat to BufferedImage conversion, directly manipulates pixel data
   */
  public static BufferedImage matToBufferedImage(Mat mat) {
    // Ensure Mat is in BGR format
    Mat bgrMat = mat;
    if (mat.channels() == 3) {
      // If Mat is in RGB format, convert to BGR
      if (mat.type() == CvType.CV_8UC3) {
        bgrMat = new Mat();
        Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_RGB2BGR);
      }
    }

    // Create BufferedImage
    BufferedImage bi = new BufferedImage(bgrMat.cols(), bgrMat.rows(),
        BufferedImage.TYPE_3BYTE_BGR);

    // Get pixel data
    byte[] pixels = new byte[(int) (bgrMat.total() * bgrMat.channels())];
    bgrMat.get(0, 0, pixels);

    // Set pixel data
    bi.getRaster().setDataElements(0, 0, bgrMat.cols(), bgrMat.rows(), pixels);

    // Release temporary Mat
    if (bgrMat != mat) {
      bgrMat.release();
    }

    return bi;
  }

  /**
   * Optimized method to save JavaFX Image using OpenCV
   * Converts directly from JavaFX Image to OpenCV Mat for better performance
   */
  public static boolean saveImageWithOpenCV(Image image, File file) {
    ensureOpenCVLoaded();
    
    if (image == null || file == null) {
      return false;
    }

    try {
      // Convert directly from JavaFX Image to OpenCV Mat
      Mat mat = fxImageToMat(image);
      if (mat == null) {
        return false;
      }

      // Use OpenCV to save the image
      boolean success = Imgcodecs.imwrite(file.getAbsolutePath(), mat);

      // Release Mat resources
      mat.release();

      return success;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Alternative optimized method using OpenCV's built-in color conversion
   * This may be faster for very large images
   */
  public static Image openImageWithOpenCV(File file) {
    ensureOpenCVLoaded();
    
    if (file == null || !file.exists() || !file.canRead()) {
      return null;
    }

    try {
      long startTime = System.currentTimeMillis();
      
      // Use OpenCV to read the image
      Mat mat = Imgcodecs.imread(file.getAbsolutePath());
      if (mat.empty()) {
        return null;
      }

      long readTime = System.currentTimeMillis();
      System.out.println("OpenCV read time: " + (readTime - startTime) + "ms");

      // Use OpenCV's built-in color conversion for better performance
      Image image = matToFXImageOptimized(mat);
      
      long convertTime = System.currentTimeMillis();
      System.out.println("Optimized conversion time: " + (convertTime - readTime) + "ms");
      System.out.println("Total optimized OpenCV open time: " + (convertTime - startTime) + "ms");
      
      // Release Mat resources
      mat.release();
      
      return image;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Optimized direct conversion from OpenCV Mat to JavaFX Image for better performance
   * Uses bulk operations and more efficient data handling
   */
  public static Image matToFXImage(Mat mat) {
    if (mat == null || mat.empty()) {
      return null;
    }

    int width = mat.cols();
    int height = mat.rows();
    int channels = mat.channels();

    // Create WritableImage for direct pixel manipulation
    WritableImage fxImage = new WritableImage(width, height);
    PixelWriter writer = fxImage.getPixelWriter();

    // Get pixel data from Mat
    byte[] pixels = new byte[(int) (mat.total() * channels)];
    mat.get(0, 0, pixels);

    // Use bulk operations for better performance
    if (channels == 3) {
      // BGR to ARGB conversion using bulk operations
      int[] argbPixels = new int[width * height];
      int pixelIndex = 0;
      int argbIndex = 0;
      
      for (int i = 0; i < pixels.length; i += 3) {
        int b = pixels[i] & 0xFF;
        int g = pixels[i + 1] & 0xFF;
        int r = pixels[i + 2] & 0xFF;
        argbPixels[argbIndex++] = (255 << 24) | (r << 16) | (g << 8) | b;
      }
      
      // Bulk write to WritableImage
      writer.setPixels(0, 0, width, height, 
                      javafx.scene.image.PixelFormat.getIntArgbInstance(), 
                      argbPixels, 0, width);
      
    } else if (channels == 4) {
      // BGRA to ARGB conversion using bulk operations
      int[] argbPixels = new int[width * height];
      int pixelIndex = 0;
      int argbIndex = 0;
      
      for (int i = 0; i < pixels.length; i += 4) {
        int b = pixels[i] & 0xFF;
        int g = pixels[i + 1] & 0xFF;
        int r = pixels[i + 2] & 0xFF;
        int a = pixels[i + 3] & 0xFF;
        argbPixels[argbIndex++] = (a << 24) | (r << 16) | (g << 8) | b;
      }
      
      // Bulk write to WritableImage
      writer.setPixels(0, 0, width, height, 
                      javafx.scene.image.PixelFormat.getIntArgbInstance(), 
                      argbPixels, 0, width);
      
    } else if (channels == 1) {
      // Grayscale to ARGB conversion using bulk operations
      int[] argbPixels = new int[width * height];
      int argbIndex = 0;
      
      for (int i = 0; i < pixels.length; i++) {
        int gray = pixels[i] & 0xFF;
        argbPixels[argbIndex++] = (255 << 24) | (gray << 16) | (gray << 8) | gray;
      }
      
      // Bulk write to WritableImage
      writer.setPixels(0, 0, width, height, 
                      javafx.scene.image.PixelFormat.getIntArgbInstance(), 
                      argbPixels, 0, width);
    }

    return fxImage;
  }

  /**
   * Optimized conversion using OpenCV's built-in color conversion functions
   */
  public static Image matToFXImageOptimized(Mat mat) {
    if (mat == null || mat.empty()) {
      return null;
    }

    int width = mat.cols();
    int height = mat.rows();
    int channels = mat.channels();

    // Convert to BGR if needed (OpenCV's default format)
    Mat bgrMat = mat;
    if (channels == 1) {
      // Convert grayscale to BGR
      bgrMat = new Mat();
      Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_GRAY2BGR);
    } else if (channels == 4) {
      // Convert BGRA to BGR
      bgrMat = new Mat();
      Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_BGRA2BGR);
    }

    // Convert BGR to RGB for JavaFX
    Mat rgbMat = new Mat();
    Imgproc.cvtColor(bgrMat, rgbMat, Imgproc.COLOR_BGR2RGB);

    // Create WritableImage
    WritableImage fxImage = new WritableImage(width, height);
    PixelWriter writer = fxImage.getPixelWriter();

    // Get pixel data from RGB Mat
    byte[] pixels = new byte[width * height * 3];
    rgbMat.get(0, 0, pixels);

    // Convert to ARGB format
    int[] argbPixels = new int[width * height];
    int argbIndex = 0;
    
    for (int i = 0; i < pixels.length; i += 3) {
      int r = pixels[i] & 0xFF;
      int g = pixels[i + 1] & 0xFF;
      int b = pixels[i + 2] & 0xFF;
      argbPixels[argbIndex++] = (255 << 24) | (r << 16) | (g << 8) | b;
    }
    
    // Bulk write to WritableImage
    writer.setPixels(0, 0, width, height, 
                    javafx.scene.image.PixelFormat.getIntArgbInstance(), 
                    argbPixels, 0, width);

    // Release temporary Mats
    if (bgrMat != mat) {
      bgrMat.release();
    }
    rgbMat.release();

    return fxImage;
  }

  /**
   * Optimized direct conversion from JavaFX Image to OpenCV Mat for better performance
   * Uses bulk operations for faster conversion
   */
  public static Mat fxImageToMat(Image image) {
    if (image == null) {
      return null;
    }

    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    // Create Mat for BGR format (OpenCV default)
    Mat mat = new Mat(height, width, CvType.CV_8UC3);
    
    // Get pixel data from JavaFX Image using bulk operation
    PixelReader reader = image.getPixelReader();
    int[] argbPixels = new int[width * height];
    reader.getPixels(0, 0, width, height, 
                    javafx.scene.image.PixelFormat.getIntArgbInstance(), 
                    argbPixels, 0, width);
    
    // Convert ARGB to BGR format
    byte[] bgrPixels = new byte[width * height * 3];
    int pixelIndex = 0;
    
    for (int argb : argbPixels) {
      // Extract RGB components from ARGB
      int r = (argb >> 16) & 0xFF;
      int g = (argb >> 8) & 0xFF;
      int b = argb & 0xFF;
      
      // Convert RGB to BGR (OpenCV format)
      bgrPixels[pixelIndex++] = (byte) b;
      bgrPixels[pixelIndex++] = (byte) g;
      bgrPixels[pixelIndex++] = (byte) r;
    }
    
    // Set pixel data to Mat
    mat.put(0, 0, bgrPixels);
    
    return mat;
  }
}
