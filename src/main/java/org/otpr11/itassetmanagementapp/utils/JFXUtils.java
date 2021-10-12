package org.otpr11.itassetmanagementapp.utils;

import javafx.scene.control.SingleSelectionModel;

public abstract class JFXUtils {
  public static Integer getSelectedIndex(
      @SuppressWarnings("rawtypes") SingleSelectionModel selectionModel) {
    return selectionModel.getSelectedIndex();
  }
}
