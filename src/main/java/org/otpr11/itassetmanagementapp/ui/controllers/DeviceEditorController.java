package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.val;
import org.controlsfx.control.CheckComboBox;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

public class DeviceEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Device device = new Device();
  private final Configuration configuration = new Configuration();
  private final Status status = new Status();

  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static final DeviceStatus DEFAULT_DEVICE_STATUS = DeviceStatus.VACANT;
  private static final String OS_SELECTOR_DEFAULT_TILE = "Select...";

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  private final List<String> deviceStatuses =
      Arrays.stream(DeviceStatus.values()).map(DeviceStatus::toString).collect(Collectors.toList());

  private final List<String> users =
      dao.users.getAll().stream().map(User::getId).collect(Collectors.toList());

  private final List<String> locations =
      dao.locations.getAll().stream().map(Location::getId).collect(Collectors.toList());

  private List<String> configs = getAvailableHWConfigs(DEFAULT_DEVICE_TYPE);

  private final List<String> operatingSystems =
      dao.operatingSystems.getAll().stream()
          .map(OperatingSystem::toPrettyString)
          .collect(Collectors.toList());

  @FXML private CheckComboBox<String> osSelector;

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
  @FXML private Button addHWConfigButton, addOSButton, okButton, cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    initDropdown(statusSelector, deviceStatuses, DEFAULT_DEVICE_STATUS.toString());
    initDropdown(deviceTypeSelector, deviceTypes, DEFAULT_DEVICE_TYPE.toString());
    initDropdown(userSelector, users, users.get(0));
    initDropdown(locationSelector, locations, locations.get(0));
    initDropdown(configSelector, configs, configs.get(0));

    // Update HW configs when device type changes
    deviceTypeSelector.setOnAction(
        event -> {
          val type =
              DeviceType.fromString(deviceTypeSelector.getSelectionModel().getSelectedItem());
          updateAvailableHWConfigs(type);
        });

    // Init OS selector
    // Needed for reassignment in lambda
    var ref =
        new Object() {
          String lastChange = "";
        };

    osSelector.getItems().addAll(operatingSystems);
    osSelector.setTitle(OS_SELECTOR_DEFAULT_TILE);
    osSelector
        .getCheckModel()
        .getCheckedItems()
        .addListener(
            (ListChangeListener<String>)
                change -> {
                  while (change.next()) {
                    val changeList = change.getList();
                    val changeListString = changeList.toString();

                    // Workaround for https://github.com/controlsfx/controlsfx/issues/1030
                    // FIXME: This doesn't work if two OSes have the same pretty string, but we'll
                    // have to address that some other way
                    if (!changeListString.equals(ref.lastChange)) {
                      ref.lastChange = changeListString;

                      if (changeList.size() != 0) {
                        //noinspection unchecked
                        osSelector.setTitle(StringUtils.joinStrings((List<String>) changeList));
                      } else {
                        osSelector.setTitle(OS_SELECTOR_DEFAULT_TILE);
                      }
                    }
                  }
                });

    addHWConfigButton.setOnAction(event -> main.showHWConfigEditor());
    addOSButton.setOnAction(event -> main.showOSEditor());
    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
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

  private void updateAvailableHWConfigs(DeviceType deviceType) {
    configs = getAvailableHWConfigs(deviceType);
    initDropdown(configSelector, configs, configs.get(0));
  }

  private List<String> getAvailableHWConfigs(DeviceType deviceType) {
    return dao.configurations.getAll().stream()
        .filter(cfg -> cfg.getDeviceType() == deviceType)
        .map(StringUtils::getPrettyDeviceString)
        .collect(Collectors.toList());
  }
}
