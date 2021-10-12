package org.otpr11.itassetmanagementapp.interfaces;

import javafx.stage.Stage;
import org.otpr11.itassetmanagementapp.Main;

public interface ViewController {
  void setMain(Main main);

  void setStage(Stage stage);

  void setSceneChangeData(Object o);

  void afterInitialize();
}
