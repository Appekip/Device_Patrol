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
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

public class UserEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final User user = new User();
  private final Validator validator = new Validator();

  @FXML
  private TextField firstNameField,
  lastNameField,
  phoneNumberField,
  emailField,
  employeeIdField;

  @FXML
  private Button okButton,
      cancelButton;

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
