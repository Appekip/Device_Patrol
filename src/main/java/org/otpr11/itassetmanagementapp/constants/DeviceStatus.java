package org.otpr11.itassetmanagementapp.constants;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

@RequiredArgsConstructor
public enum DeviceStatus {
  VACANT,
  IN_USE,
  BROKEN,
  IN_REPAIR;

  public static DeviceStatus fromString(String value) {
    val locale = LocaleEngine.getResourceBundle();

    // Have to do this with this goofy looking if-else tree because we need the localised variants
    if (value.equals("VACANT") || value.equals(locale.getString("device_status_vacant"))) {
      return VACANT;
    } else if (value.equals("IN_USE") || value.equals(locale.getString("device_status_in_use"))) {
      return IN_USE;
    } else if (value.equals("BROKEN") || value.equals(locale.getString("device_status_broken"))) {
      return BROKEN;
    } else if (value.equals("IN_REPAIR")
        || value.equals(locale.getString("device_status_in_repair"))) {
      return IN_REPAIR;
    } else {
      throw new IllegalStateException("Unknown device status %s".formatted(value));
    }
  }

  public static String getLocalised(DeviceStatus status) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_status_%s".formatted(status.toString().toLowerCase()));
  }

  public static String getLocalisedPretty(DeviceStatus status) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_status_%s_pretty".formatted(status.toString().toLowerCase()));
  }
}
