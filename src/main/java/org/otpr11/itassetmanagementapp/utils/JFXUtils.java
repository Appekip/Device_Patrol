package org.otpr11.itassetmanagementapp.utils;

import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

/** Generic JavaFX utilities. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

  public static HBox createText(String content, TextProperties properties) {
    val text = new Text(content);

    // Workaround for JFX not allowing margins on Text elements
    val container = new HBox();
    container.getChildren().add(text);

    val style = new StringBuilder();

    if (properties.getFontSize() != null) {
      style.append(String.format("-fx-font-size: %s;", properties.getFontSize()));
    }

    if (properties.getFontWeight() != null) {
      style.append(String.format("-fx-font-weight: %s;", properties.getFontWeight()));
    }

    text.setStyle(style.toString());

    return container;
  }

  @NoArgsConstructor
  @AllArgsConstructor
  public static class TextProperties {
    @Getter @Setter public Integer fontSize;
    @Getter @Setter public String fontWeight;

    // Adding optional combinatory constructors
    // Christ why can't Java just add simple key/value objects already

    public TextProperties(Integer fontSize) {
      this.fontSize = fontSize;
    }
  }
}
