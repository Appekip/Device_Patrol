package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.select;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
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
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

@Log4j2
public class UserEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;
  private static boolean IS_EDIT_MODE;
  //private Device device = new Device();
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private User user = new User();
  private final Validator validator = new Validator();

  /**
   * FXML for the attributes and buttons of the user view
   */
  @FXML
  private TextField firstNameField,
  lastNameField,
  phoneNumberField,
  emailField,
  employeeIdField;

  @FXML
  private Button okButton,
      cancelButton;

  /**
   * Initializing the start of the user view
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    createTextFieldValidator(firstNameField, "firstname", firstNameField.textProperty());
    createTextFieldValidator(lastNameField, "lastname", lastNameField.textProperty());
    createTextFieldValidator(phoneNumberField, "phonenumber", phoneNumberField.textProperty());
    createTextFieldValidator(emailField, "email", emailField.textProperty());
    createTextFieldValidator(employeeIdField, "employeeid", employeeIdField.textProperty());

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
      user.setFirstName(firstNameField.getText());
      user.setLastName(lastNameField.getText());
      user.setPhone(phoneNumberField.getText());
      user.setEmail(emailField.getText());
      user.setId(employeeIdField.getText());

      dao.devices.save(user);
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
        && dao.users.get((String) sceneChangeData) != null) {
      IS_EDIT_MODE = true;
      log.trace("Editing existing user {}.", sceneChangeData);
      stage.setTitle("Manage user %s".formatted(sceneChangeData));

      // Determine user to edit
      user = dao.users.get((String) sceneChangeData);

      // Fill in data for this user

      firstNameField.setText(user.getFirstName());
      lastNameField.setText(user.getLastName());
      phoneNumberField.setText(user.getPhone());
      emailField.setText(user.getEmail());
      employeeIdField.setText(user.getId());

    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new user.");
      stage.setTitle("Create user");
    }
  }
}
