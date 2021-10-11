package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

public class DeviceEditorController implements Initializable, ViewController {
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Device device = new Device();
  private final Configuration configuration = new Configuration();
  private final Status status = new Status();

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  private final List<String> deviceStatuses =
      Arrays.stream(DeviceStatus.values()).map(DeviceStatus::toString).collect(Collectors.toList());

  private final List<String> users =
      dao.users.getAll().stream().map(User::getId).collect(Collectors.toList());

  private final List<String> locations =
      dao.locations.getAll().stream().map(Location::getId).collect(Collectors.toList());

  private final List<String> configs =
      dao.configurations.getAll().stream()
          .map(StringUtils::getPrettyDeviceString)
          .collect(Collectors.toList());

  @Setter private Main main;
  @Setter private Stage stage;

  @FXML
  private ChoiceBox<String> deviceTypeSelector,
      statusSelector,
      userSelector,
      locationSelector,
      configSelector;
  @FXML
  private TextField deviceIDField,
      manufacturerField,
      modelIDField,
      modelNameField,
      modelYearField,
      nicknameField,
      osField,
      macAddressField,
      cpuField,
      gpuField,
      memoryField,
      diskSizeField,
      screenSizeField;
  @FXML private Button okButton, cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    initDropdown(statusSelector, deviceStatuses, DeviceStatus.VACANT.toString());
    initDropdown(deviceTypeSelector, deviceTypes, DeviceType.LAPTOP.toString());
    initDropdown(userSelector, users, users.get(0));
    initDropdown(locationSelector, locations, locations.get(0));
    initDropdown(configSelector, configs, configs.get(0));

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().addAll(items);
    dropdown.setValue(initialValue);
  }

  private void clearFields() {
    deviceTypeSelector.setValue(null);
    deviceIDField.clear();
    manufacturerField.clear();
    modelIDField.clear();
    modelNameField.clear();
    statusSelector.setValue(null);
    modelYearField.clear();
    nicknameField.clear();
    osField.clear();
    macAddressField.clear();
    cpuField.clear();
    gpuField.clear();
    memoryField.clear();
    diskSizeField.clear();
    screenSizeField.clear();
  }

  private void onSave(ActionEvent event) {
    device.setId(deviceIDField.getText());
    device.setManufacturer(manufacturerField.getText());
    device.setModelID(modelIDField.getText());
    device.setModelName(modelNameField.getText());
    device.setModelYear(modelYearField.getText());
    device.setMacAddress(macAddressField.getText());
    device.setNickname(nicknameField.getText());

    // TODO: Values from dropdowns

    dao.devices.create(device);

    stage.close();
  }

  private void onCancel(ActionEvent event) {
    stage.close();
  }
}
