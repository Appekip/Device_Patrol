package org.otpr11.itassetmanagementapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.ui.controllers.ViewController;
import org.otpr11.itassetmanagementapp.ui.utils.WindowSize;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.DevUtils;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

@Log4j2
public class Main extends Application {
  @Getter private final String test = "test";
  private Stage primaryStage;

  public static void main(String[] args) {
    Config.load();
    LogUtils.configureLogger();

    if (Boolean.parseBoolean(Config.getConfig().get("INSERT_TEST_DATA"))) {
      DevUtils.generateTestData();
    }

    launch();
  }

  @Override
  public void start(Stage primary) {
    primaryStage = primary;
    setScene("scenes/MainView.fxml", "Laitehallinta", null, false);
  }

  public void showEditor() {
    setScene("scenes/DeviceEditor.fxml", "Laitehallinta - Laite-editori", null, false);
  }

  /**
   * Internal callback for initialising a stage based on application configuration.
   *
   * @param stage The {@link Stage} to configure.
   * @param title The title of the {@link Stage}.
   * @param windowSizing Sizing of this window.
   */
  private void initStage(Stage stage, String title, WindowSize windowSizing) {
    // Set stage title
    stage.setTitle(title);

    if (windowSizing != null) {
      // Lock window size
      stage.setMinWidth(windowSizing.getWidth());
      stage.setMaxWidth(windowSizing.getHeight());
      stage.setMinHeight(windowSizing.getWidth());
      stage.setMaxHeight(windowSizing.getHeight());
    }
  }

  /**
   * Initialises a {@link Scene} with application-specific options through a call to {@link
   * Main#initStage}.
   *
   * @param sceneResourcePath The resource path for the FXML file of this {@link Scene}.
   * @param stageTitle The title of the stage.
   * @param windowSizing Sizing of this window.
   * @param isPrimary Whether this is the primary scene.
   */
  private void setScene(
      String sceneResourcePath, String stageTitle, WindowSize windowSizing, boolean isPrimary) {
    try {
      Stage stage;

      if (isPrimary) {
        stage = primaryStage;
      } else {
        stage = new Stage();
      }

      initStage(stage, stageTitle, windowSizing);

      val loader = new FXMLLoader();
      loader.setLocation(getClass().getResource(sceneResourcePath));
      Parent rootScene = loader.load();

      ViewController controller = loader.getController();
      controller.setMain(this);
      controller.setStage(stage);

      val scene = new Scene(rootScene);
      stage.setScene(scene);
      stage.show();

      log.trace("Opening view {}.", sceneResourcePath);
    } catch (IOException e) {
      log.error("Could not open view {}:", sceneResourcePath, e);
      AlertUtils.showExceptionAlert("Could not open view %s.".formatted(sceneResourcePath), e);
    }
  }
}
