package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;

public class HardwareConfigurationEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Configuration configuration = new Configuration();

  @FXML
  private TextField configurationIDField,
      cpuField,
      gpuField,
      memoryField,
      diskSizeField,
      screenSizeField;

  @FXML
  private Button okButton,
      cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    //okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void onSave(){
    //configuration.setLaptopConfiguration();
  }

  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void afterInitialize() {}
}
