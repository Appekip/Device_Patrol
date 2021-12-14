package org.otpr11.itassetmanagementapp.locale;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.config.UserPreferences;
import org.otpr11.itassetmanagementapp.config.UserPreferences.Settings;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;

/**
 * This class manages the application's locale settings. It is furthermore responsible for serving
 * as an event propagator, informing registered listeners of locale changes.
 *
 * <p>Listeners are registered by calling {@link LocaleEngine#addListener}. Classes can implement
 * the {@link LocaleChangeListener} interface, after which they can declare an <code>
 * onLocaleChange()</code> method.
 *
 * <p>Please note: Due to limitations of Java, merely implementing the interface does not guarantee
 * event delivery; classes still have to call <code>LocaleEngine.addListener(this)</code>, should
 * they wish to actually receive events.
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class LocaleEngine {
  private static final String PREFERRED_LOCALE_SETTING = Settings.PREFERRED_LOCALE.toString();
  private static final Preferences settings = UserPreferences.get();
  private static final ArrayList<LocaleChangeListener> listeners = new ArrayList<>();

  @Getter private static Locale userLocale;
  @Getter private static ResourceBundle resourceBundle;

  static {
    val settings = UserPreferences.get();
    var defaultLocale = Locale.getDefault();
    var preferredLocale =
        new Locale(settings.get(PREFERRED_LOCALE_SETTING, defaultLocale.getLanguage()));
    setUserLocale(preferredLocale);
  }

  public static void setUserLocale(Locale locale) {
    if (!isLocaleSupported(locale)) {
      log.warn("User selected locale {} is not supported. Falling back to English.", locale);
      locale = Locale.ENGLISH;
    } else {
      log.trace("Setting locale to {}.", locale);
    }

    userLocale = locale;
    loadLocale();
    settings.put(PREFERRED_LOCALE_SETTING, locale.getLanguage());
    listeners.forEach(LocaleChangeListener::onLocaleChange);
  }

  private static void loadLocale() {
    resourceBundle =
        ResourceBundle.getBundle("org/otpr11/itassetmanagementapp/locale/locale", userLocale);
  }

  private static boolean isLocaleSupported(Locale locale) {
    val localePath =
        Main.class.getResource("locale/locale_%s.properties".formatted(locale.getLanguage()));
    return localePath != null;
  }

  public static void addListener(LocaleChangeListener listener) {
    listeners.add(listener);
  }
}
