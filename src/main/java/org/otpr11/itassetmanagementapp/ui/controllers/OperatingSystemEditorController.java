package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class OperatingSystemEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;


  @Override
  public void afterInitialize() {}

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final OperatingSystem operatingSystem = new OperatingSystem();
  private final Validator validator = new Validator();

  @FXML
  private TextField nameField,
    buildNumberField,
    versionField;

  @FXML
  private Button okButton,
    cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    createTextFieldValidator(nameField, "name", nameField.textProperty());
    createTextFieldValidator(buildNumberField, "buildNumber", buildNumberField.textProperty());
    createTextFieldValidator(versionField, "version", versionField.textProperty());

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void onSave(ActionEvent event) {
    if (validator.containsErrors() || validator.containsWarnings()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Invalid input",
          "One or more required field values are missing or invalid.");
    }
    else {
      // Set basic properties
      operatingSystem.setName(nameField.getText());
      operatingSystem.setBuildNumber(buildNumberField.getText());
      operatingSystem.setVersion(versionField.getText());

      dao.devices.save(operatingSystem);
      stage.close();

    }
  }
  private void onCancel(ActionEvent event) {
    stage.close();
  }

  private void createTextFieldValidator(TextField field, String key, StringProperty prop) {
    val edited = new AtomicBoolean(false);

    validator
        .createCheck()
        .dependsOn(key, prop)
        .withMethod(
            ctx -> {
              val warn = "Required field.";
              val error = "Please provide a value.";
              String value = ctx.get(key);

              if (value == null || value.trim().equals("")) {
                if (!edited.get()) { // Not yet edited, show only warning
                  ctx.warn(warn);
                  edited.set(true);
                } else { // Already edited, show error now
                  ctx.error(error);
                }
              }
            })
        .decorates(field)
        .immediate();
  }
}
