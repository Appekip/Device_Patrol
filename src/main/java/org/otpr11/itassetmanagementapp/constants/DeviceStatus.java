package org.otpr11.itassetmanagementapp.constants;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

/** Describes a {@link Device}'s status. */
@RequiredArgsConstructor
public enum DeviceStatus {
  VACANT,
  IN_USE,
  BROKEN,
  IN_REPAIR;

  /**
   * Reverse lookups a (possibly localised) string rendition of a status and returns the relevant
   * enum constant.
   *
   * @param value String representation to lookup
   * @return {@link DeviceStatus}
   */
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

  /**
   * Returns a localised string rendition of a status.
   *
   * @param status {@link DeviceStatus}
   * @return Localised version of status constant
   */
  public static String getLocalised(DeviceStatus status) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_status_%s".formatted(status.toString().toLowerCase()));
  }

  /**
   * Returns a full (=> properly capitalised) string rendition of a status.
   *
   * @param status {@link DeviceStatus}
   * @return Localised full version of status constant
   */
  public static String getLocalisedPretty(DeviceStatus status) {
    val locale = LocaleEngine.getResourceBundle();
    return locale.getString("device_status_%s_pretty".formatted(status.toString().toLowerCase()));
  }
}
