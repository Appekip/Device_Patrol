package org.otpr11.itassetmanagementapp.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Declares all the scenes used in the application, and metadata for them. */
@RequiredArgsConstructor
public enum Scenes {
  MAIN("scenes/MainView.fxml", true, false),
  DEVICE_EDITOR("scenes/DeviceEditor.fxml", false, true),
  HW_CFG_EDITOR("scenes/HardwareConfigurationEditor.fxml", false, true),
  OS_EDITOR("scenes/OperatingSystemEditor.fxml", false, true),
  LOCATION_EDITOR("scenes/LocationEditor.fxml", false, true),
  USER_EDITOR("scenes/UserEditor.fxml", false, true),
  MANAGEMENT_VIEW("scenes/ManagementView.fxml", false, true);

  @Getter private final String resourcePath;
  private final boolean usesPrimaryStage;
  @Getter private final boolean isPopup;

  // Manual getter because Lombok cannot generate a grammatically correct name for this
  public boolean usesPrimaryStage() {
    return usesPrimaryStage;
  }
}
