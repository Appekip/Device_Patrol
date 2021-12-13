package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createTextFieldValidator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

public class UserEditorController implements Initializable, ViewController, LocaleChangeListener {

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final User user = new User();
  private final Validator validator = new Validator();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  // FXML for the attributes and buttons of the user view
  @FXML
  private TextField firstNameField, lastNameField, phoneNumberField, emailField, employeeIdField;
  @FXML private Label firstName;
  @FXML private Label lastName;
  @FXML private Label phoneNumber;
  @FXML private Label email;
  @FXML private Label id;
  @FXML private Button okButton, cancelButton;

  /** Initializing the start of the user view */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    createTextFieldValidator(
        validator, firstNameField, "first_name", firstNameField.textProperty());
    createTextFieldValidator(validator, lastNameField, "last_name", lastNameField.textProperty());
    createTextFieldValidator(validator, phoneNumberField, "phone", phoneNumberField.textProperty());
    createTextFieldValidator(validator, emailField, "email", emailField.textProperty());
    createTextFieldValidator(validator, employeeIdField, "user_id", employeeIdField.textProperty());

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);

    onLocaleChange();
  }

  /** Save button event */
  private void onSave(ActionEvent event) {
    if (validator.containsErrors() || validator.containsWarnings()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("invalid_input"),
          locale.getString("invalid_input_detail"));
    } else {
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

  /** Cancel button event */
  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void afterInitialize() {}

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    firstName.setText(locale.getString("first_name"));
    lastName.setText(locale.getString("last_name"));
    phoneNumber.setText(locale.getString("phone"));
    email.setText(locale.getString("email"));
    id.setText(locale.getString("id"));
  }
}
