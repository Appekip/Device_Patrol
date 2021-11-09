package org.otpr11.itassetmanagementapp.utils;

import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import lombok.val;

/** Generic JavaFX utilities. */
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

  public static Text createText(String content) {
    return new Text(content);
  }

  public static Text createText(String content, int fontSize) {
    val text = new Text(content);
    text.setStyle(String.format("-fx-font-size: %s", fontSize));
    return text;
  }

  public static Text createText(String content, int fontSize, String fontWeight) {
    val text = new Text(content);
    text.setStyle(String.format("-fx-font-size: %d; -fx-font-weight: %s", fontSize, fontWeight));
    return text;
  }
}
