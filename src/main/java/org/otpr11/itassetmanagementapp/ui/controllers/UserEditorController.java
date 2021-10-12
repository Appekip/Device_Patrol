package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Setter;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;

public class UserEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}
}
