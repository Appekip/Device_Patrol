package org.otpr11.itassetmanagementapp.interfaces;

import javafx.stage.Stage;
import org.otpr11.itassetmanagementapp.Main;

/** Declares a class to be an FXML controller class. */
public interface ViewController {
  void setMain(Main main);

  void setStage(Stage stage);

  /**
   * Use this method to pass any kind of important data over a scene change, similar to Android's
   * usage of ResourceBundles.
   *
   * @param o Object to transfer
   */
  void setSceneChangeData(Object o);

  void afterInitialize();
}
