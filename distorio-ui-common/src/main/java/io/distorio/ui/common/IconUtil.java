package io.distorio.ui.common;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconUtil {

  /**
   * Load an icon with the default classloader (current module)
   */
  public static ImageView icon(String iconName, double size) {
    return icon(iconName, size, IconUtil.class.getClassLoader());
  }

  /**
   * Load an icon using a specific classloader (for service providers)
   * If classLoader is null, uses the current module's classloader
   */
  public static ImageView icon(String iconName, double size, ClassLoader classLoader) {
    Image img = loadIcon(iconName, classLoader);
    ImageView view = new ImageView(img);
    view.setFitWidth(size);
    view.setFitHeight(size);
    view.setPreserveRatio(true);
    return view;
  }

  /**
   * Unified icon loading logic that tries multiple classloaders in order:
   * 1. Provider classloader (if provided)
   * 2. Current module's classloader (fallback)
   * 3. Placeholder icon (if all else fails)
   */
  private static Image loadIcon(String iconName, ClassLoader classLoader) {
    ClassLoader loader = IconUtil.class.getClassLoader();
    if (classLoader != null) {
      loader = classLoader;
    }
    Image icon = loadIconFromClassLoader(iconName, loader);
    if (icon != null) {
      return icon;
    }
    return createPlaceholderIcon();
  }

  /**
   * Attempts to load an icon from a specific classloader
   */
  private static Image loadIconFromClassLoader(String iconName, ClassLoader classLoader) {
    try {
      InputStream stream = classLoader.getResourceAsStream(iconName);
      if (stream != null) {
        if (iconName.toLowerCase().endsWith(".svg")) {
          return SvgUtils.loadSvg(stream);
        } else {
          return new Image(stream);
        }
      }
    } catch (Exception e) {
      // Log the error but don't fail - fall back to next classloader
      System.err.println("Failed to load icon '" + iconName + "' from classloader: " + e.getMessage());
    }
    return null;
  }

  private static Image createPlaceholderIcon() {
    // Create a simple 16x16 placeholder image
    // This is a minimal fallback to prevent crashes
    return new Image(
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAbwAAAG8B8aLcQwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3Njape.org5vuPBoAAAB8SURBVDiNY2AYBYMRMDIyMjAyMjL8//+f4f///wwsDAwMDP///2f4//8/AwMDA8P///8ZGBgYGP7//8/AwMDA8P//f4b///8z/P//n+H///8M////Z/j//z8DAwMDw////xn+///P8P//f4b///8z/P//n+H///8M////Z/j//z8DAwMDw////xn+//8/AAAgAAGQhqj+AAAAAElFTkSuQmCC");
  }
}

