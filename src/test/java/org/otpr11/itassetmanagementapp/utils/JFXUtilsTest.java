package org.otpr11.itassetmanagementapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.otpr11.itassetmanagementapp.utils.JFXUtils.TextProperties;
import org.testfx.framework.junit5.ApplicationTest;

class JFXUtilsTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    super.start(stage);
  }

  @Test
  void testSelectAndGetChoiceIndex() {
    val choiceBox = new ChoiceBox<String>();
    val comboBox = new ComboBox<String>();
    val testString = "test";
    choiceBox.getItems().add(testString);
    comboBox.getItems().add(testString);

    JFXUtils.select(choiceBox, 0);
    JFXUtils.select(comboBox, 1);

    assertEquals(-1, JFXUtils.getChoiceIndex(choiceBox));
    assertEquals(-1, JFXUtils.getChoiceIndex(comboBox));
  }

  @Test
  void testGetPercentageWidth() {
    assertEquals(2.147483648E11, JFXUtils.getPercentageWidth(100));
  }

  @Test
  void testCreateText() {
    val testString = "test";
    val text = JFXUtils.createText(testString);
    assertEquals(testString, text.getText());
  }

  @Test
  void testCreateTextWithProps() {
    val testString = "test";
    val fontSize = 13;
    val fontWeight = "bold";

    val textProps = new TextProperties(fontSize - 1);
    textProps.setFontSize(fontSize);
    textProps.setFontWeight(fontWeight);
    val container = JFXUtils.createText(testString, textProps);

    val text = (Text) container.getChildren().get(0);
    assertEquals(testString, text.getText());
    assertTrue(text.getStyle().contains("-fx-font-size: %s".formatted(fontSize)));
    assertTrue(text.getStyle().contains("-fx-font-weight: %s".formatted(fontWeight)));
  }
}
