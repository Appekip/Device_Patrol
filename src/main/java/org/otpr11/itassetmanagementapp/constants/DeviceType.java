package org.otpr11.itassetmanagementapp.constants;

import lombok.val;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

/** Describes the type of a {@link Device}. */
public enum DeviceType {
  LAPTOP,
  DESKTOP;

  /**
   * Returns a localised string rendition of a type.
   *
   * @param type {@link DeviceType}
   * @return Localised version of type constant
   */
  public static DeviceType fromString(String type) {
    val locale = LocaleEngine.getResourceBundle();

    // Have to do this with this goofy looking if-else tree because we need the localised variants
    if (type.equals("LAPTOP") || type.equals(locale.getString("device_type_laptop"))) {
      return LAPTOP;
    } else if (type.equals("DESKTOP") || type.equals(locale.getString("device_type_desktop"))) {
      return DESKTOP;
    } else {
      throw new IllegalStateException("Unknown device type %s".formatted(type));
    }
  }

  /**
   * Returns a full (=> properly capitalised) string rendition of a type.
   *
   * @param type {@link DeviceType}
   * @return Localised version of type constant
   */
  public static String getLocalised(DeviceType type) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_type_%s".formatted(type.toString().toLowerCase()));
  }
}
