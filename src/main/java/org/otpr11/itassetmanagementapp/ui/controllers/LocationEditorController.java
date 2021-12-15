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
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
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
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  // FXML for the attributes and boxes of the location view
  @FXML private Label zipText, nickText, addressText, idText;
  @FXML private Button okButton, cancelButton;
  @FXML private TextField idField, addressField, zipCodeField, nicknameField;

  /** Text field validation */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    LocaleEngine.addListener(this);

    createTextFieldValidator(validator, idField, "id", idField.textProperty());
    createTextFieldValidator(validator, addressField, "address", addressField.textProperty());
    createTextFieldValidator(validator, zipCodeField, "zipcode", zipCodeField.textProperty());
    createTextFieldValidator(validator, nicknameField, "nickname", nicknameField.textProperty());

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
      location.setId(idField.getText());
      location.setAddress(addressField.getText());
      location.setNickname(nicknameField.getText());
      location.setZipCode(zipCodeField.getText());

      dao.locations.save(location);
      stage.close();
    }
  }

  /** Cancel button event */
  private void onCancel(ActionEvent event) {
    stage.close();
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



  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    zipText.setText(locale.getString("zip_code"));
    nickText.setText(locale.getString("nickname"));
    addressText.setText(locale.getString("address"));
    idText.setText(locale.getString("id"));
  }
}
