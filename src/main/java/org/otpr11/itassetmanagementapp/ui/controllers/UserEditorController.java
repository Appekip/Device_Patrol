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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class UserEditorController implements Initializable, ViewController, LocaleChangeListener {
  private static boolean IS_EDIT_MODE;
  // private Device device = new Device();
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private User user = new User();
  private final Validator validator = new Validator();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  // FXML for the attributes and buttons of the user view
  @FXML
  private TextField firstNameField, lastNameField, phoneNumberField, emailField, employeeIdField;
  @FXML private Text title;
  @FXML private Label firstNameText, lastNameText, phoneNumberText, emailText, idText;
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

      dao.users.save(user);
      stage.close();
    }
  }

  /** Cancel button event */
  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();

    title.setText(locale.getString("user"));
    firstNameText.setText(locale.getString("first_name"));
    lastNameText.setText(locale.getString("last_name"));
    phoneNumberText.setText(locale.getString("phone"));
    emailText.setText(locale.getString("email"));
    idText.setText(locale.getString("id"));
    cancelButton.setText(locale.getString("cancel"));
    okButton.setText(locale.getString("save"));
  }

  @Override
  public void afterInitialize() {
    val stageTitle = locale.getString("user_editor_stage_title");

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

      stage.setTitle(IS_EDIT_MODE ? "%s %s".formatted(stageTitle, user.getId()) : stageTitle);
    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new user.");
      stage.setTitle(stageTitle);
    }
  }
}
