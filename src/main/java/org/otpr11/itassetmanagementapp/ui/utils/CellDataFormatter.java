package org.otpr11.itassetmanagementapp.ui.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn.CellDataFeatures;
import lombok.val;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

/** Formatter to turn nested data types in table cells into their correct string representations. */
public abstract class CellDataFormatter {
  public static SimpleStringProperty formatHWConfig(CellDataFeatures<Device, String> data) {
    val cfg = data.getValue().getConfiguration();
    return new SimpleStringProperty(cfg != null ? StringUtils.getPrettyHWConfig(cfg) : "N/A");
  }

  public static SimpleStringProperty formatOS(CellDataFeatures<Device, String> data) {
    val operatingSystems = data.getValue().getOperatingSystems();

    if (operatingSystems.size() == 0) {
      return new SimpleStringProperty("N/A");
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
    val user = data.getValue().getUser();
    return new SimpleStringProperty(user != null ? user.getId() : "N/A");
  }

  public static SimpleStringProperty formatLocation(CellDataFeatures<Device, String> data) {
    val location = data.getValue().getLocation();
    return new SimpleStringProperty(location != null ? location.getId() : "N/A");
  }

  public static SimpleStringProperty formatDeviceType(CellDataFeatures<Device, String> data) {
    val cfg = data.getValue().getConfiguration();

    return new SimpleStringProperty(cfg != null ? cfg.getDeviceType().toString() : "UNKNOWN");
  }
}
