package org.otpr11.itassetmanagementapp.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Scenes {
  MAIN("scenes/MainView.fxml", "Laitehallinta", true, false),
  DEVICE_EDITOR("scenes/DeviceEditor.fxml", "Laitehallinta - Laite-editori", false, true);

  @Getter private final String resourcePath;
  @Getter private final String stageTitle;
  // TODO: Do we need this?
  // @Getter private final WindowSize windowSizing;
  private final boolean usesPrimaryStage;
  @Getter private final boolean isTop;

  // Manual getter because Lombok cannot generate a grammatically correct name for this
  public boolean usesPrimaryStage() {
    return usesPrimaryStage;
  }
}
