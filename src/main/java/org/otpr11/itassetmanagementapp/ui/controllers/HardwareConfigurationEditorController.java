package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.StringUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils;

@Log4j2
public class HardwareConfigurationEditorController
    implements Initializable, ViewController, LocaleChangeListener {
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static boolean IS_EDIT_MODE;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private Device device = new Device();
  private Configuration configuration = new Configuration();
  private DesktopConfiguration desktopConfiguration = new DesktopConfiguration();
  private LaptopConfiguration laptopConfiguration = new LaptopConfiguration();
  private final Validator validator = new Validator();
  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values())
          .map(DeviceType::toString)
          .map(type -> DeviceType.getLocalised(DeviceType.fromString(type)))
          .toList();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  // Text field validation and dropdown Initializing the start of the hardware configuration view
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  //  FXML for the attributes and boxes of the hardware view
  @FXML private Text title, deviceTypeText, cpuText, gpuText, ramText, diskText, screenSizeText;
  @FXML private ChoiceBox<String> deviceTypeField;
  @FXML private TextField cpuField, diskSizeField, screenSizeField, gpuField, memoryField;
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

    initDropdown(deviceTypeField, deviceTypes, DeviceType.getLocalised(DEFAULT_DEVICE_TYPE));

    deviceTypeField.setOnAction(
        event -> {
          val type = DeviceType.fromString(deviceTypeField.getSelectionModel().getSelectedItem());
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

    title.setText(locale.getString("hardware_configuration"));
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
      switch (DeviceType.fromString(deviceTypeField.getValue())) {
        case LAPTOP -> saveLaptop();
        case DESKTOP -> saveDesktop();
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
  public void afterInitialize() {
    // Support passing device type as scene change data
    if (sceneChangeData != null && sceneChangeData instanceof DeviceType) {
      JFXUtils.select(deviceTypeField, DeviceType.getLocalised((DeviceType) sceneChangeData));
    }
  }

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    title.setText(locale.getString("hardware_configuration"));
    deviceTypeText.setText(locale.getString("device_type"));
    cpuText.setText(locale.getString("cpu"));
    diskText.setText(locale.getString("disk"));
    screenSizeText.setText(locale.getString("screen_size"));
    gpuText.setText(locale.getString("gpu"));
    ramText.setText(locale.getString("ram"));
  }
  public void afterInitialize() {
    if (sceneChangeData != null
        && sceneChangeData instanceof String
        && dao.laptopConfigurations.get((String) sceneChangeData) != null) {
      IS_EDIT_MODE = true;
      log.trace("Editing existing laptop configuration {}.", sceneChangeData);
      stage.setTitle("Manage laptop configuration %s".formatted(sceneChangeData));

      // Determine laptop configuration to edit
      laptopConfiguration = dao.laptopConfigurations.get((String) sceneChangeData);

      cpuField.setText(laptopConfiguration.getCpu());
      diskSizeField.setText(laptopConfiguration.getDiskSize());
      gpuField.setText(laptopConfiguration.getGpu());
      memoryField.setText(laptopConfiguration.getMemory());
      //screenSizeField.setText(laptopConfiguration.getScreenSize());

      //laptopConfiguration.setScreenSize(Integer.parseInt(screenSizeField.getText()));

    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new laptop configuration.");
      stage.setTitle("Create laptop configuration");
    }
  }
}