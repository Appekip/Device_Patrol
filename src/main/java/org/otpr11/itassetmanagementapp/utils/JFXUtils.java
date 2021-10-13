package org.otpr11.itassetmanagementapp.utils;

import javafx.scene.control.ChoiceBox;

public abstract class JFXUtils {

  /**
   * Shortcut to get the selection index of a {@link ChoiceBox}.
   *
   * @param choiceBox {@link ChoiceBox}
   * @return Selection index.
   */
  public static Integer getChoiceIndex(@SuppressWarnings("rawtypes") ChoiceBox choiceBox) {
    return choiceBox.getSelectionModel().getSelectedIndex();
  }

  /**
   * Shortcut to pre-select an item from a {@link ChoiceBox}.
   *
   * @param choiceBox {@link ChoiceBox}
   * @param toSelect Item to select.
   */
  public static void select(@SuppressWarnings("rawtypes") ChoiceBox choiceBox, Object toSelect) {
    //noinspection unchecked
    choiceBox.getSelectionModel().select(toSelect);
  }

  /**
   * Get a width as a percentage of the total available width.
   *
   * @param percentage Percentage of width to get
   * @return Floating-point integer representing the desired percentage
   */
  public static double getPercentageWidth(double percentage) {
    return 1f * Integer.MAX_VALUE * percentage;
  }
}
