package org.otpr11.itassetmanagementapp.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Scenes {
  MAIN("scenes/MainView.fxml", "IT Asset Manager", true, false),
  DEVICE_EDITOR("scenes/DeviceEditor.fxml", "Manage device", false, true),
  HW_CFG_EDITOR(
      "scenes/HardwareConfigurationEditor.fxml", "Manage hardware configuration", false, true),
  OS_EDITOR("scenes/OperatingSystemEditor.fxml", "Manage operating system", false, true),
  LOCATION_EDITOR("scenes/LocationEditor.fxml", "Manage location", false, true),
  USER_EDITOR("scenes/UserEditor.fxml", "Manage user", false, true),
  MANAGEMENT_VIEW("scenes/ManagementView.fxml", "Manage existing", false, true);

  @Getter private final String resourcePath;
  @Getter private final String stageTitle;
  private final boolean usesPrimaryStage;
  @Getter private final boolean isPopup;

  // Manual getter because Lombok cannot generate a grammatically correct name for this
  public boolean usesPrimaryStage() {
    return usesPrimaryStage;
  }
}
