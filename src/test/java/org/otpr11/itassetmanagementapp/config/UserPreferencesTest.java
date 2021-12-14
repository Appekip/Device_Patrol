package org.otpr11.itassetmanagementapp.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.config.UserPreferences.Settings;

class UserPreferencesTest {
  @SneakyThrows
  @BeforeEach
  void beforeEach() {
    UserPreferences.get().clear();
  }

  @Test
  void testGet() {
    assertFalse(
        UserPreferences.get()
            .getBoolean(UserPreferences.getSettingName(Settings.IS_WINDOW_MAXIMIZED), false));
  }

  @Test
  void testSet() {
    val prefs = UserPreferences.get();
    val setting = UserPreferences.getSettingName(Settings.IS_WINDOW_MAXIMIZED);
    prefs.putBoolean(setting, true);
    assertTrue(prefs.getBoolean(setting, false));
  }
}
