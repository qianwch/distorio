package io.distorio.core;

import java.util.prefs.Preferences;

public class ConfigService {
    
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigService.class);
    
    // Configuration keys
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_ICON_MODE = "icon_mode";
    public static final String KEY_ZOOM_LEVEL = "zoom_level";
    public static final String KEY_WINDOW_WIDTH = "window_width";
    public static final String KEY_WINDOW_HEIGHT = "window_height";
    public static final String KEY_WINDOW_X = "window_x";
    public static final String KEY_WINDOW_Y = "window_y";
    
    // Default values
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_ICON_MODE = "ICON_TEXT";
    public static final String DEFAULT_ZOOM_LEVEL = "100";
    
    public static void setString(String key, String value) {
        prefs.put(key, value);
    }
    
    public static String getString(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }
    
    public static void setInt(String key, int value) {
        prefs.putInt(key, value);
    }
    
    public static int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
    
    public static void setDouble(String key, double value) {
        prefs.putDouble(key, value);
    }
    
    public static double getDouble(String key, double defaultValue) {
        return prefs.getDouble(key, defaultValue);
    }
    
    public static void setBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
    
    public static void remove(String key) {
        prefs.remove(key);
    }
    
    public static void clear() {
        try {
            prefs.clear();
        } catch (Exception e) {
            // Ignore exceptions when clearing preferences
        }
    }
} 