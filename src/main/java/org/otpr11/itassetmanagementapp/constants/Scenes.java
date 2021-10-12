package org.otpr11.itassetmanagementapp.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Scenes {
  MAIN("scenes/MainView.fxml", "Laitehallinta", true, false),
  DEVICE_EDITOR("scenes/DeviceEditor.fxml", "Laitehallinta - Laite-editori", false, true);

  @Getter private final String resourcePath;
  @Getter private final String stageTitle;
  private final boolean usesPrimaryStage;
  @Getter private final boolean isPopup;

  // Manual getter because Lombok cannot generate a grammatically correct name for this
  public boolean usesPrimaryStage() {
    return usesPrimaryStage;
  }
}
