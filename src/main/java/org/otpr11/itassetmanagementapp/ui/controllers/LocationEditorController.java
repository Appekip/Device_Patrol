package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

public class LocationEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Location location = new Location();
  private final Validator validator = new Validator();

  @FXML
  private Button okButton,
      cancelButton;

  @FXML
  private TextField idField,
      addressField,
      zipCodeField,
      nicknameField;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    createTextFieldValidator(idField, "id", idField.textProperty());
    createTextFieldValidator(addressField, "address", addressField.textProperty());
    createTextFieldValidator(zipCodeField, "zipcode", zipCodeField.textProperty());
    createTextFieldValidator(nicknameField, "nickname", nicknameField.textProperty());

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
      location.setId(idField.getText());
      location.setAddress(addressField.getText());
      location.setNickname(nicknameField.getText());
      location.setZipCode(zipCodeField.getText());

      dao.devices.save(location);
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

  @Override
  public void afterInitialize() {}



}

