package org.otpr11.itassetmanagementapp.utils;

import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;

/** Generic JavaFX utilities. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class JFXUtils {
  private static final ResourceBundle locale = LocaleEngine.getResourceBundle();

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
   * Shortcut to get the selection index of a {@link ComboBox}.
   *
   * @param comboBox {@link ComboBox}
   * @return Selection index.
   */
  public static Integer getChoiceIndex(@SuppressWarnings("rawtypes") ComboBox comboBox) {
    return comboBox.getSelectionModel().getSelectedIndex();
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
   * Shortcut to pre-select an item from a {@link ComboBox}.
   *
   * @param comboBox {@link ComboBox}
   * @param toSelect Item to select.
   */
  public static void select(@SuppressWarnings("rawtypes") ComboBox comboBox, Object toSelect) {
    //noinspection unchecked
    comboBox.getSelectionModel().select(toSelect);
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

  public static void createTextFieldValidator(
      Validator validator, TextField field, String key, StringProperty prop) {
    val edited = new AtomicBoolean(false);

    validator
        .createCheck()
        .dependsOn(key, prop)
        .withMethod(
            ctx -> {
              val warn = locale.getString("required_field");
              val error = locale.getString("please_provide_value");
              String value = ctx.get(key);

              if (value == null || value.trim().equals("")) {
                if (!edited.get()) { // Not yet edited, show only warning
                  ctx.warn(warn);
                  edited.set(true);
                } else { // Already edited, show error now
                  ctx.error(error);
                }
              }
            })
        .decorates(field)
        .immediate();
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
