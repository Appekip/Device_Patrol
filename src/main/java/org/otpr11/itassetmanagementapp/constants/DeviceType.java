package org.otpr11.itassetmanagementapp.constants;

import lombok.val;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

public enum DeviceType {
  LAPTOP,
  DESKTOP;

  public static DeviceType fromString(String value) {
    val locale = LocaleEngine.getResourceBundle();

    // Have to do this with this goofy looking if-else tree because we need the localised variants
    if (value.equals("LAPTOP") || value.equals(locale.getString("device_type_laptop"))) {
      return LAPTOP;
    } else if (value.equals("DESKTOP") || value.equals(locale.getString("device_type_desktop"))) {
      return DESKTOP;
    } else {
      throw new IllegalStateException("Unknown device type %s".formatted(value));
    }
  }

  public static String getLocalised(DeviceType type) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_type_%s".formatted(type.toString().toLowerCase()));
  }
}
