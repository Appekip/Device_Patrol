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
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class LocationEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;
  private static boolean IS_EDIT_MODE;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private Location location = new Location();
  private final Validator validator = new Validator();

  /**
   * FXML for the attributes and boxes of the location view
    */

  @FXML
  private Button okButton,
      cancelButton;

  @FXML
  private TextField idField,
      addressField,
      zipCodeField,
      nicknameField;


  /**
   * Text field validation
    */

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    createTextFieldValidator(idField, "id", idField.textProperty());
    createTextFieldValidator(addressField, "address", addressField.textProperty());
    createTextFieldValidator(zipCodeField, "zipcode", zipCodeField.textProperty());
    createTextFieldValidator(nicknameField, "nickname", nicknameField.textProperty());

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  /**
   * Save button event
    */

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

  /**
   * Cancel button event
    */

  private void onCancel(ActionEvent event) {
    stage.close();
  }

  /**
   * Text field validation
   * Initializing the start of the location view
    */

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
  public void afterInitialize() {
    if (sceneChangeData != null
        && sceneChangeData instanceof String
        && dao.locations.get((String) sceneChangeData) != null) {
      IS_EDIT_MODE = true;
      log.trace("Editing existing user {}.", sceneChangeData);
      stage.setTitle("Manage user %s".formatted(sceneChangeData));

      // Determine location to edit
      location = dao.locations.get((String) sceneChangeData);

      // Fill in data for this location

      idField.setText(location.getId());
      addressField.setText(location.getAddress());
      nicknameField.setText(location.getNickname());
      zipCodeField.setText(location.getZipCode());

    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new location.");
      stage.setTitle("Create location");
    }
  }



}

