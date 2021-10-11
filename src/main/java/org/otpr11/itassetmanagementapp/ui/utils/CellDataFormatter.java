package org.otpr11.itassetmanagementapp.ui.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn.CellDataFeatures;
import lombok.val;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

public abstract class CellDataFormatter {
  public static SimpleStringProperty formatHWConfig(CellDataFeatures<Device, String> data) {
    val cfg = data.getValue().getConfiguration();
    return new SimpleStringProperty(StringUtils.getPrettyDeviceString(cfg));
  }

  public static SimpleStringProperty formatOS(CellDataFeatures<Device, String> data) {
    val operatingSystems = data.getValue().getOperatingSystems();

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
    return new SimpleStringProperty(data.getValue().getUser().getId());
  }

  public static SimpleStringProperty formatLocation(CellDataFeatures<Device, String> data) {
    return new SimpleStringProperty(data.getValue().getLocation().getId());
  }
}
