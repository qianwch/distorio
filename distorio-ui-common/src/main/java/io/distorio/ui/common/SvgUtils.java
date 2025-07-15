package io.distorio.ui.common;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

/**
 * A utility class for loading SVG files and converting them into JavaFX Images. Allows for dynamic
 * modification of SVG colors during the loading process.
 *
 * @author Gemini
 */
public final class SvgUtils {

  /**
   * Private constructor to prevent instantiation.
   */
  private SvgUtils() {
  }

  /**
   * Loads an SVG from the specified URL and creates an Image without any color replacements.
   *
   * @param ins The URL pointing to the SVG file.
   * @return A configured Image, or null if loading fails.
   */
  public static Image loadSvg(InputStream ins) {
    return loadSvg(ins, Collections.emptyMap());
  }

  /**
   * Loads an SVG from the specified URL and creates an Image, replacing a single specified color.
   *
   * @param ins      The URL pointing to the SVG file.
   * @param oldColor The color string to be replaced (e.g., "#000000" or "black").
   * @param newColor The new color string to use for replacement (e.g., "#FFFFFF" or "white").
   * @return A configured Image, or null if loading fails.
   */
  public static Image loadSvg(InputStream ins, String oldColor, String newColor) {
    return loadSvg(ins, Collections.singletonMap(oldColor, newColor));
  }

  /**
   * Loads an SVG from the specified URL and creates an Image, replacing multiple colors based on
   * the provided map.
   *
   * @param ins               The URL pointing to the SVG file.
   * @param colorReplacements A Map where the key is the old color string to be replaced, and the
   *                          value is the new color string.
   * @return A configured Image, or null if loading fails.
   */
  public static Image loadSvg(InputStream ins, Map<String, String> colorReplacements) {
    try {
      // 1. Read the content of the SVG file into a string
      String svgContent = readStringFromIns(ins);

      // 2. If color replacements are provided, perform string substitution
      if (colorReplacements != null && !colorReplacements.isEmpty()) {
        for (Map.Entry<String, String> entry : colorReplacements.entrySet()) {
          svgContent = svgContent.replace(entry.getKey(), entry.getValue());
        }
      }

      // 3. Convert the modified SVG string into a JavaFX Image using svgSalamander
      Image image = svgToImage(svgContent);

      // 4. Create and return the Image
      return image;

    } catch (IOException | SVGException e) {
      System.err.println("Failed to load or render SVG file: " + ins);
      e.printStackTrace();
      return null; // or throw a runtime exception
    }
  }

  /**
   * Renders a string containing SVG data into a JavaFX Image using svgSalamander.
   *
   * @param svgContent The string containing the SVG data.
   * @return The rendered Image object.
   * @throws SVGException if the svgSalamander rendering fails.
   * @throws IOException  if the in-memory stream operations fail.
   */
  private static Image svgToImage(String svgContent) throws SVGException, IOException {
    SVGUniverse universe = new SVGUniverse();
    // Load SVG from string
    URI uri = universe.loadSVG(new StringReader(svgContent), "icon");
    SVGDiagram diagram = universe.getDiagram(uri);
    // Set a default size if not specified
    int width = (int) Math.ceil(diagram.getWidth() > 0 ? diagram.getWidth() : 32);
    int height = (int) Math.ceil(diagram.getHeight() > 0 ? diagram.getHeight() : 32);
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    diagram.setIgnoringClipHeuristic(true); // For better compatibility
    diagram.render(bufferedImage.createGraphics());
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }

  /**
   * Reads all content from a InputStream and returns it as a single string.
   *
   * @param ins The InputStream to read from.
   * @return The content of the URL as a string.
   * @throws IOException if an I/O error occurs.
   */
  private static String readStringFromIns(InputStream ins) throws IOException {
    try (BufferedReader reader = new BufferedReader(
      new InputStreamReader(ins, StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }
}
