package org.otpr11.itassetmanagementapp.ui.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn.CellDataFeatures;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

/** Formatter to turn nested data types in table cells into their correct string representations. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CellDataFormatter {
  public static SimpleStringProperty formatHWConfig(CellDataFeatures<Device, String> data) {
    val locale = LocaleEngine.getResourceBundle();
    val cfg = data.getValue().getConfiguration();
    return new SimpleStringProperty(
        cfg != null ? StringUtils.getPrettyHWConfig(cfg) : locale.getString("not_applicable"));
  }

  public static SimpleStringProperty formatOS(CellDataFeatures<Device, String> data) {
    val locale = LocaleEngine.getResourceBundle();
    val operatingSystems = data.getValue().getOperatingSystems();

    if (operatingSystems.size() == 0) {
      return new SimpleStringProperty(locale.getString("not_applicable"));
    }

    val content = new StringBuilder();

    for (int i = 0; i < operatingSystems.size(); i++) {
      val os = operatingSystems.get(i);
      content.append(os.toPrettyString());

      // Append commas where relevant
      if (operatingSystems.size() > 1 && i < operatingSystems.size() - 1) {
        content.append(", ");
      }
    }

    return new SimpleStringProperty(content.toString());
  }

  public static SimpleStringProperty formatUser(CellDataFeatures<Device, String> data) {
    val locale = LocaleEngine.getResourceBundle();
    val user = data.getValue().getUser();
    return new SimpleStringProperty(
        user != null ? user.getId() : locale.getString("not_applicable"));
  }

  public static SimpleStringProperty formatLocation(CellDataFeatures<Device, String> data) {
    val locale = LocaleEngine.getResourceBundle();
    val location = data.getValue().getLocation();
    return new SimpleStringProperty(
        location != null ? location.getId() : locale.getString("not_applicable"));
  }

  public static SimpleStringProperty formatDeviceType(CellDataFeatures<Device, String> data) {
    val cfg = data.getValue().getConfiguration();

    return new SimpleStringProperty(
        cfg != null
            ? DeviceType.getLocalised(cfg.getDeviceType())
            : LocaleEngine.getResourceBundle().getString("unknown_device_type"));
  }
}
