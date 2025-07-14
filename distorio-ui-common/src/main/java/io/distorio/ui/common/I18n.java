package io.distorio.ui.common;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

  private static Locale currentLocale = Locale.ENGLISH;
  private static ResourceBundle bundle = loadBundle(currentLocale);

  private static ResourceBundle loadBundle(Locale locale) {
    return ResourceBundle.getBundle("i18n/messages", locale);
  }

  public static void setLocale(Locale locale) {
    currentLocale = locale;
    bundle = loadBundle(locale);
  }

  public static String get(String key) {
    return bundle.getString(key);
  }

  public static Locale getCurrentLocale() {
    return currentLocale;
  }
}
