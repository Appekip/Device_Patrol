package org.otpr11.itassetmanagementapp.config;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class UserPreferences {
  private static final String NODE_NAME = "org.otpr11.itassetmanagementapp.config";
  private static final Preferences preferences = Preferences.userRoot().node(NODE_NAME);

  static {
    if (Boolean.parseBoolean(Config.getConfig().get("FORCE_USERPREF_RESET"))) {
      try {
        preferences.clear();
      } catch (BackingStoreException e) {
        AlertUtils.showExceptionAlert("Could not reset user preferences.", e);
      }
    }
  }

  public static Preferences get() {
    return preferences;
  }

  public static String getSettingName(Settings setting) {
    return setting.toString();
  }

  @RequiredArgsConstructor
  public enum Settings {
    WINDOW_POSITION_X,
    WINDOW_POSITION_Y,
    WINDOW_WIDTH,
    WINDOW_HEIGHT
  }
}
