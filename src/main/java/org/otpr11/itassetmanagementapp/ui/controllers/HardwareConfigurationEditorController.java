package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createTextFieldValidator;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class HardwareConfigurationEditorController
    implements Initializable, ViewController, LocaleChangeListener {
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Configuration configuration = new Configuration();
  private final DesktopConfiguration desktopConfiguration = new DesktopConfiguration();
  private final LaptopConfiguration laptopConfiguration = new LaptopConfiguration();
  private final Validator validator = new Validator();
  private final ResourceBundle locale = LocaleEngine.getResourceBundle();
  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  // Text field validation and dropdown Initializing the start of the hardware configuration view
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  //  FXML for the attributes and boxes of the hardware view
  @FXML private Text hwText;
  @FXML private Text deviceTypeText;
  @FXML private Text cpuText;
  @FXML private Text diskText;
  @FXML private Text gpuText;
  @FXML private Text ramText;
  @FXML private ChoiceBox<String> deviceTypeField;
  @FXML private TextField cpuField, diskSizeField, screenSizeField, gpuField, memoryField;
  @FXML private Text screenSizeText;
  @FXML private Button okButton, cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    LocaleEngine.addListener(this);

    createTextFieldValidator(validator, cpuField, "cpu", cpuField.textProperty());
    createTextFieldValidator(validator, diskSizeField, "diskSize", diskSizeField.textProperty());
    createTextFieldValidator(validator, gpuField, "gpu", gpuField.textProperty());
    createTextFieldValidator(validator, memoryField, "memory", memoryField.textProperty());
    createTextFieldValidator(
        validator, screenSizeField, "screenSize", screenSizeField.textProperty());

    initDropdown(deviceTypeField, deviceTypes, DEFAULT_DEVICE_TYPE.toString());

    deviceTypeField.setOnAction(
        event -> {
          val type = DeviceType.valueOf(deviceTypeField.getSelectionModel().getSelectedItem());
          if (type == DeviceType.DESKTOP) {
            screenSizeField.setEditable(false);
            screenSizeField.setVisible(false);
            screenSizeText.setVisible(false);
          } else {
            screenSizeField.setEditable(true);
            screenSizeField.setVisible(true);
            screenSizeText.setVisible(true);
          }
        });

    hwText.setText(locale.getString("hardware_configuration"));
    deviceTypeText.setText(locale.getString("device_type"));
    cpuText.setText(locale.getString("cpu"));
    diskText.setText(locale.getString("disk"));
    gpuText.setText(locale.getString("gpu"));
    ramText.setText(locale.getString("ram"));

    // Saving and canceling
    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);

    onLocaleChange();
  }

  /** Configuring dropdown */
  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  /** Functional saving */
  private void onSave(ActionEvent event) {
    if (validator.containsWarnings() || validator.containsErrors()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("invalid_input"),
          locale.getString("invalid_input_detail"));
    } else {
      if (deviceTypeField.getValue().equals("LAPTOP")) {
        saveLaptop();
      } else {
        saveDesktop();
      }
    }
  }

  /** Saving laptop data */
  private void saveLaptop() {
    laptopConfiguration.setCpu(cpuField.getText());
    laptopConfiguration.setDiskSize(diskSizeField.getText());
    laptopConfiguration.setGpu(gpuField.getText());
    laptopConfiguration.setMemory(memoryField.getText());
    laptopConfiguration.setScreenSize(Integer.parseInt(screenSizeField.getText()));

    dao.devices.save(laptopConfiguration);
    configuration.setLaptopConfiguration(laptopConfiguration);
    dao.devices.save(configuration);
    stage.close();
  }

  /** Saving desktop data */
  private void saveDesktop() {
    desktopConfiguration.setCpu(cpuField.getText());
    desktopConfiguration.setDiskSize(diskSizeField.getText());
    desktopConfiguration.setGpu(gpuField.getText());
    desktopConfiguration.setMemory(memoryField.getText());

    dao.devices.save(desktopConfiguration);
    configuration.setDesktopConfiguration(desktopConfiguration);
    dao.devices.save(configuration);
    stage.close();
  }

  /** Functional cancel button */
  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void afterInitialize() {}

  @Override
  public void onLocaleChange() {
    hwText.setText(locale.getString("hardware_configuration"));
    deviceTypeText.setText(locale.getString("device_type"));
    cpuText.setText(locale.getString("cpu"));
    diskText.setText(locale.getString("disk"));
    screenSizeText.setText(locale.getString("screen_size"));
    gpuText.setText(locale.getString("gpu"));
    ramText.setText(locale.getString("ram"));
  }
}
