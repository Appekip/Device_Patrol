/*
  Code reused from https://github.com/mergemocha/assembly-robot-simulator. Licensed under the MIT license.

  Copyright (c) 2021 Linus Willner, Panu Eronen and Anna Smati.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/

package org.otpr11.itassetmanagementapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/** Generic alert utilities. */
public abstract class AlertUtils {

  /**
   * Shows an {@link Alert} and waits for the user to click OK.
   *
   * @param type The {@link AlertType} to use.
   * @param title The title for the alert.
   * @param message The message body of the alert.
   * @return {@link ButtonType} that shows what the user clicked in the alert.
   */
  public static ButtonType showAlert(
      @NotNull AlertType type, @NotNull String title, @NotNull String message) {
    val alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    return alert.getResult();
  }

  /**
   * Shows an {@link Alert} with support for including exceptions to display.
   *
   * @param message The message body of the alert.
   * @param e The {@link Exception} that caused the alert.
   */
  public static void showExceptionAlert(@NotNull String message, @NotNull Exception e) {
    createExceptionAlert("Error", message, e).showAndWait();
  }

  /**
   * Shows an {@link Alert} with support for including exceptions to display.
   *
   * @param title The title of the alert.
   * @param message The message body of the alert.
   * @param e The {@link Exception} that caused the alert.
   */
  public static void showExceptionAlert(
      @NotNull String title, @NotNull String message, @NotNull Exception e) {
    createExceptionAlert(title, message, e).showAndWait();
  }

  /**
   * Shows an {@link Alert} with support for including exceptions to display.
   *
   * @param title The title of the alert.
   * @param message The message body of the alert.
   * @param e The {@link Exception} that caused the alert.
   * @return {@link Alert}
   */
  private static Alert createExceptionAlert(
      @NotNull String title, @NotNull String message, @NotNull Exception e) {
    val alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    val label = new Label("The exception stack trace was:");

    val textArea = new TextArea(StringUtils.getFullExceptionStack(e));
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    val content = new GridPane();
    content.setMaxWidth(Double.MAX_VALUE);
    content.add(label, 0, 0);
    content.add(textArea, 0, 1);

    alert.getDialogPane().setExpandableContent(content);

    return alert;
  }
}
