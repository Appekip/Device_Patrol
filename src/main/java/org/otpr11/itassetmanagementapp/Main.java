package org.otpr11.itassetmanagementapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

public class Main extends Application {
  @Getter private final String test = "test";

  public static void main(String[] args) {
    Config.load();
    LogUtils.configureLogger();
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("scenes/addDevice.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Hello World, this is a " + getTest());
    stage.setScene(scene);
    stage.show();

    // FIXME: Temp so that the DB inits on program boot, feel free to remove once actually
    // reference in application code
    val dao = GlobalDAO.getInstance();
  }
}
