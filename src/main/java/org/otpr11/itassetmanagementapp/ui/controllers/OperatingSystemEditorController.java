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
import org.controlsfx.control.CheckComboBox;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class OperatingSystemEditorController implements Initializable, ViewController {
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static final DeviceStatus DEFAULT_DEVICE_STATUS = DeviceStatus.VACANT;
  private static final String OS_SELECTOR_DEFAULT_TILE = "Select...";
  private static boolean IS_EDIT_MODE;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final OperatingSystem operatingSystem = new OperatingSystem();
  private final Configuration configuration = new Configuration();
  private final Status status = new Status();
  private final Validator validator = new Validator();
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;
  @FXML private TextField nameField, buildNumberField, versionField;
  @FXML private Button okButton, cancelButton;
  @FXML private CheckComboBox<String> osSelector;

  @Override
  public void afterInitialize() {}

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    createTextFieldValidator(nameField, "name", nameField.textProperty());
    createTextFieldValidator(buildNumberField, "buildNumber", buildNumberField.textProperty());
    createTextFieldValidator(versionField, "version", versionField.textProperty());

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void onSave(ActionEvent event) {
    if (validator.containsErrors()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Invalid input",
          "One or more required field values are missing or invalid.");
    } else if (osSelector.getCheckModel().getCheckedItems().size() == 0) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "No operating system selected",
          "No operating system has been selected for this device.");
    } else if (!IS_EDIT_MODE && dao.devices.get(nameField.getText()) != null) {
      AlertUtils.showAlert(
          AlertType.ERROR, "Duplicate ID detected", "A device with this ID already exists.");
    } else {
      // Set basic properties
      operatingSystem.setName(nameField.getText());
      operatingSystem.setBuildNumber(buildNumberField.getText());
      operatingSystem.setVersion(versionField.getText());
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
