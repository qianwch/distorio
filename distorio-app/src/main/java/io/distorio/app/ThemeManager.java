package io.distorio.app;

import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.Scene;

public class ThemeManager {

  public enum Theme {
    LIGHT, DARK, SYSTEM
  }

  private static Theme currentTheme = Theme.SYSTEM;
  private static Scene currentScene;
  private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
  private static final String THEME_PREF_KEY = "theme";

  static {
    // Load saved theme preference
    String savedTheme = prefs.get(THEME_PREF_KEY, Theme.SYSTEM.name());
    try {
      currentTheme = Theme.valueOf(savedTheme);
    } catch (IllegalArgumentException e) {
      currentTheme = Theme.SYSTEM;
    }
  }

  public static void setScene(Scene scene) {
    currentScene = scene;
    applyCurrentTheme();
  }

  public static void setTheme(Theme theme) {
    currentTheme = theme;
    prefs.put(THEME_PREF_KEY, theme.name());
    applyCurrentTheme();
  }

  public static Theme getCurrentTheme() {
    return currentTheme;
  }

  public static boolean isDarkMode() {
    if (currentTheme == Theme.SYSTEM) {
      return isSystemDarkMode();
    }
    return currentTheme == Theme.DARK;
  }

  private static boolean isSystemDarkMode() {
    // Check system dark mode preference
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("mac")) {
      // macOS: check for dark mode
      try {
        Process process = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle").start();
        int exitCode = process.waitFor();
        return exitCode == 0;
      } catch (Exception e) {
        return false; // Default to light mode if detection fails
      }
    } else if (os.contains("win")) {
      // Windows: check registry for dark mode
      try {
        Process process = new ProcessBuilder(
          "reg", "query",
          "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
          "/v", "AppsUseLightTheme"
        ).start();
        int exitCode = process.waitFor();
        return exitCode == 0; // If the key exists, it means dark mode is enabled
      } catch (Exception e) {
        return false; // Default to light mode if detection fails
      }
    }
    return false; // Default to light mode for other systems
  }

  private static void applyCurrentTheme() {
    if (currentScene == null) {
      return;
    }

    Platform.runLater(() -> {
      String themeFile = isDarkMode() ? "/style-dark.css" : "/style.css";
      currentScene.getStylesheets().clear();
      currentScene.getStylesheets().add(ThemeManager.class.getResource(themeFile).toExternalForm());
    });
  }
}
