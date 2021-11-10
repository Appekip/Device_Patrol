package org.otpr11.itassetmanagementapp;

import static org.otpr11.itassetmanagementapp.config.UserPreferences.getSettingName;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.config.Config;
import org.otpr11.itassetmanagementapp.config.UserPreferences;
import org.otpr11.itassetmanagementapp.config.UserPreferences.Settings;
import org.otpr11.itassetmanagementapp.constants.Scenes;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.DevUtils;
import org.otpr11.itassetmanagementapp.utils.LogUtils;

@Log4j2
public class Main extends Application {
  private Stage primaryStage;

  public static void main(String[] args) {
    LogUtils.configureLogger();

    if (Boolean.parseBoolean(Config.getConfig().get("INSERT_TEST_DATA"))) {
      DevUtils.generateTestData();
    }

    launch();
  }

  @Override
  public void start(Stage primary) {
    primaryStage = primary;

    // Remember window positions across program reboots
    // FIXME: This does not yet apply to popups, they still go to the primary monitor every time

    val preferences = UserPreferences.get();

    val wPosX = getSettingName(Settings.WINDOW_POSITION_X);
    val wPosY = getSettingName(Settings.WINDOW_POSITION_Y);
    val wWidth = getSettingName(Settings.WINDOW_WIDTH);
    val wHeight = getSettingName(Settings.WINDOW_HEIGHT);
    val isMax = getSettingName(Settings.IS_WINDOW_MAXIMIZED);

    val screenBounds = Screen.getPrimary().getBounds();
    val x = preferences.getDouble(wPosX, screenBounds.getMinX());
    val y = preferences.getDouble(wPosY, screenBounds.getMinY());
    val width = preferences.getDouble(wWidth, screenBounds.getWidth());
    val height = preferences.getDouble(wHeight, screenBounds.getHeight());
    val isMaximized = preferences.getBoolean(isMax, primaryStage.isMaximized());

    primaryStage.setX(x);
    primaryStage.setY(y);
    primaryStage.setWidth(width);
    primaryStage.setHeight(height);
    primaryStage.setMaximized(isMaximized);

    // Save window position on program shutdown
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  preferences.putDouble(wPosX, primaryStage.getX());
                  preferences.putDouble(wPosY, primaryStage.getY());
                  preferences.putDouble(wWidth, primaryStage.getWidth());
                  preferences.putDouble(wHeight, primaryStage.getHeight());
                  preferences.putBoolean(isMax, primaryStage.isMaximized());
                }));

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
