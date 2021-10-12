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
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
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
    setScene(Scenes.MAIN, null);
  }

  public void showDeviceEditor(Object sceneChangeData) {
    setScene(Scenes.DEVICE_EDITOR, sceneChangeData);
  }

  public void showHWConfigEditor(Object sceneChangeData) {
    setScene(Scenes.HW_CFG_EDITOR, sceneChangeData);
  }

  public void showOSEditor(Object sceneChangeData) {
    setScene(Scenes.OS_EDITOR, sceneChangeData);
  }

  public void showUserEditor(Object sceneChangeData) {
    setScene(Scenes.USER_EDITOR, sceneChangeData);
  }

  public void showLocationEditor(Object sceneChangeData) {
    setScene(Scenes.LOCATION_EDITOR, sceneChangeData);
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
  }

  /**
   * Initialises a {@link Scene} with application-specific options through a call to {@link
   * Main#initStage}.
   *
   * @param sceneDef The scene definition from {@link Scenes} to initialise.
   * @param sceneChangeData Arbitrary data to pass to the new scene. Useful for context-triggered
   *     modals.
   */
  private void setScene(Scenes sceneDef, Object sceneChangeData) {
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
      controller.setSceneChangeData(sceneChangeData);

      val scene = new Scene(rootScene);
      stage.setScene(scene);
      stage.show();
      controller.afterInitialize();

      log.trace("Opening view {}.", sceneResourcePath);
    } catch (IOException e) {
      log.error("Could not open view {}:", sceneResourcePath, e);
      AlertUtils.showExceptionAlert("Could not open view %s.".formatted(sceneResourcePath), e);
    }
  }
}
