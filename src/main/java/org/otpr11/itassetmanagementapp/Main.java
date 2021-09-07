package org.otpr11.itassetmanagementapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.db.DAO;

public class Main extends Application {
  @Getter private final String test = "test";

  public static void main(String[] args) {
    Config.load();
    // FIXME: Temp
    val dao = DAO.getInstance();
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/main.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Hello World, this is a " + getTest());
    stage.setScene(scene);
    stage.show();
  }
}
