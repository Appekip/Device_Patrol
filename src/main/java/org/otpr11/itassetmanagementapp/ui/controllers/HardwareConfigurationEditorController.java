package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Arrays;
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
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class HardwareConfigurationEditorController implements Initializable, ViewController {
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Configuration configuration = new Configuration();
  private final DesktopConfiguration desktopConfiguration = new DesktopConfiguration();
  private final LaptopConfiguration laptopConfiguration = new LaptopConfiguration();
  private final Validator validator = new Validator();

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  public Text hwText;
  public Text deviceTypeText;
  public Text cpuText;
  public Text diskText;
  public Text gpuText;
  public Text ramText;

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  @FXML private ChoiceBox<String> deviceTypeField;

  @FXML private TextField cpuField, diskSizeField, screenSizeField, gpuField, memoryField;

  @FXML private Text screenSizeText;

  @FXML private Button okButton, cancelButton;

  private final ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    createTextFieldValidator(cpuField, "cpu", cpuField.textProperty());
    createTextFieldValidator(diskSizeField, "diskSize", diskSizeField.textProperty());
    createTextFieldValidator(gpuField, "gpu", gpuField.textProperty());
    createTextFieldValidator(memoryField, "memory", memoryField.textProperty());
    // createTextFieldValidator(screenSizeField, "screenSize", screenSizeField.textProperty());

    initDropdown(deviceTypeField, deviceTypes, DEFAULT_DEVICE_TYPE.toString());

    deviceTypeField.setOnAction(
        event -> {
          val type = DeviceType.valueOf(deviceTypeField.getSelectionModel().getSelectedItem());
          if (type.toString().equals("DESKTOP")) {
            screenSizeField.setEditable(false);
            screenSizeField.setVisible(false);
            screenSizeText.setVisible(false);
          } else {
            screenSizeField.setEditable(true);
            screenSizeField.setVisible(true);
            screenSizeText.setVisible(true);
          }
        });

    hwText.setText(locale.getString("hwconf"));
    deviceTypeText.setText(locale.getString("deviceType"));
    cpuText.setText(locale.getString("cpu"));
    diskText.setText(locale.getString("disk"));
    gpuText.setText(locale.getString("gpu"));
    ramText.setText(locale.getString("ram"));

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  private void onSave(ActionEvent event) {

    if (validator.containsWarnings() || validator.containsErrors()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Invalid input",
          "One or more required field values are missing or invalid.");
    } else {
      if (deviceTypeField.getValue().equals("LAPTOP")) {
        saveLaptop();
      } else {
        saveDesktop();
      }
    }
  }

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
