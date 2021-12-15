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
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

/** Operating system editor controller class. */
@Log4j2
public class OperatingSystemEditorController
    implements Initializable, ViewController, LocaleChangeListener {
  private static boolean IS_EDIT_MODE;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private OperatingSystem operatingSystem = new OperatingSystem();
  private final Validator validator = new Validator();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  @FXML private Text newOsText;

  @FXML private Label nameText;
  @FXML private Label versionText;
  @FXML private Label buildNumberText;

  @FXML private TextField nameField, buildNumberField, versionField;
  @FXML private Button okButton, cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    LocaleEngine.addListener(this);

    createTextFieldValidator(validator, nameField, "name", nameField.textProperty());
    createTextFieldValidator(
        validator, buildNumberField, "build_number", buildNumberField.textProperty());
    createTextFieldValidator(validator, versionField, "version", versionField.textProperty());

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);

    onLocaleChange();
  }

  /**
   * Save button click handler.
   *
   * @param event {@link ActionEvent}
   */
  private void onSave(ActionEvent event) {
    if (validator.containsErrors() || validator.containsWarnings()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("invalid_input"),
          locale.getString("invalid_input_detail"));
    } else {
      // Set basic properties
      operatingSystem.setName(nameField.getText());
      operatingSystem.setBuildNumber(buildNumberField.getText());
      operatingSystem.setVersion(versionField.getText());

      dao.operatingSystems.save(operatingSystem);
      stage.close();
    }
  }

  /**
   * Cancel button click handler.
   *
   * @param event {@link ActionEvent}
   */
  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    newOsText.setText(locale.getString("operating_system"));
    nameText.setText(locale.getString("name"));
    versionText.setText(locale.getString("version"));
    buildNumberText.setText(locale.getString("build_number"));
    cancelButton.setText(locale.getString("cancel"));
    okButton.setText(locale.getString("save"));
  }

  @Override
  public void afterInitialize() {
    val stageTitle = locale.getString("os_editor_stage_title");

    if (sceneChangeData != null
        && sceneChangeData instanceof Long
        && dao.operatingSystems.get((Long) sceneChangeData) != null) {

      IS_EDIT_MODE = true;
      log.trace("Editing existing operating system {}.", sceneChangeData);
      stage.setTitle("Manage operating system %s".formatted(sceneChangeData));

      // Determine operating system to edit
      operatingSystem = dao.operatingSystems.get((Long) sceneChangeData);

      // Fill in data for this operating system

      nameField.setText(operatingSystem.getName());
      buildNumberField.setText(operatingSystem.getBuildNumber());
      versionField.setText(operatingSystem.getVersion());

      stage.setTitle(
          IS_EDIT_MODE
              ? "%s %s".formatted(stageTitle, operatingSystem.toPrettyString())
              : stageTitle);
    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new operating system.");
      stage.setTitle(stageTitle);
    }
  }
}
