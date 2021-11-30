package org.otpr11.itassetmanagementapp.locale;

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

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class LocaleEngine {
  private static final String PREFERRED_LOCALE_SETTING = Settings.PREFERRED_LOCALE.toString();
  private static final Preferences settings = UserPreferences.get();

  @Getter private static Locale userLocale;
  @Getter private static ResourceBundle resourceBundle;

  static {
    val settings = UserPreferences.get();
    var defaultLocale = Locale.getDefault();
    var preferredLocale =
        new Locale(settings.get(PREFERRED_LOCALE_SETTING, defaultLocale.getLanguage()));

    if (!isLocaleSupported(preferredLocale)) {
      log.warn("User locale {} is not supported. Falling back to English.", preferredLocale);
      preferredLocale = Locale.ENGLISH;
    } else {
      log.trace("Using locale {}.", preferredLocale);
    }

    userLocale = preferredLocale;
    loadLocale();
  }

  public static void setUserLocale(Locale locale) {
    userLocale = locale;
    loadLocale();
    settings.put(PREFERRED_LOCALE_SETTING, locale.getLanguage());
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
}
