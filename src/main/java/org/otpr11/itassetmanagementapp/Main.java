package org.otpr11.itassetmanagementapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.utils.DevUtils;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

public class Main extends Application {
  @Getter private final String test = "test";

  public static void main(String[] args) {
    Config.load();
    LogUtils.configureLogger();

    if (Boolean.parseBoolean(Config.getConfig().get("INSERT_TEST_DATA"))) {
      DevUtils.generateTestData();
    }

    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/MainView.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Laitehallinta");
    stage.setScene(scene);
    stage.show();
  }
}
