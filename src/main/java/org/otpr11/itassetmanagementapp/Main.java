package org.otpr11.itassetmanagementapp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.constants.Scenes;
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
    setScene(Scenes.MAIN);
  }

  public void showDeviceEditor() {
    setScene(Scenes.DEVICE_EDITOR);
  }

  public void showHWConfigEditor() {
    System.out.println("Shows HW config editor");
  }

  public void showOSEditor() {
    System.out.println("Shows OS editor");
  }

  public void showUserEditor() {
    System.out.println("Shows user editor");
  }

  public void showLocationEditor() {
    System.out.println("Shows location editor");
  }

  /**
   * Internal callback for initialising a stage based on application configuration.
   *
   * @param stage The {@link Stage} to configure.
   * @param sceneDef The scene definition from {@link Scenes} to use in this {@link Stage}.
   */
  private void initStage(Stage stage, Scenes sceneDef) {
    // Set stage title
    stage.setTitle(sceneDef.getStageTitle());

    WindowSize windowSizing = null;

    // FIXME: Do we actually need this?
    //noinspection ConstantConditions
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
   * @param sceneDef The scene definition from {@link Scenes} to initialise.
   */
  private void setScene(Scenes sceneDef) {
    val sceneResourcePath = sceneDef.getResourcePath();

    try {
      Stage stage;

      if (sceneDef.usesPrimaryStage()) {
        stage = primaryStage;
      } else {
        stage = new Stage();
      }

      if (sceneDef.isPopup()) {
        stage.initModality(Modality.APPLICATION_MODAL);
      }

      initStage(stage, sceneDef);

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
