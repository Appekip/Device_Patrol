package org.otpr11.itassetmanagementapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import lombok.Getter;

public class Main extends Application {
  @Getter private final String test = "test";

  /**
   * This is a test Javadoc.
   *
   * @param stage {@link Stage}
   * @throws IOException on I/O error.
   */
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
    stage.setTitle("Hello World, this is a " + getTest());
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
