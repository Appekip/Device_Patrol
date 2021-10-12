package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.getSelectedIndex;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.val;
import net.synedra.validatorfx.Validator;
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
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

public class DeviceEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Device device = new Device();
  private final Configuration configuration = new Configuration();
  private final Status status = new Status();
  private final Validator validator = new Validator();

  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static final DeviceStatus DEFAULT_DEVICE_STATUS = DeviceStatus.VACANT;
  private static final String OS_SELECTOR_DEFAULT_TILE = "Select...";

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  private final List<String> deviceStatuses =
      dao.statuses.getAll().stream().map(Status::toString).collect(Collectors.toList());

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
      macAddressField;
  @FXML
  private Button addHWConfigButton,
      addOSButton,
      addUserButton,
      addLocationButton,
      okButton,
      cancelButton;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Init freeform text field validators
    createTextFieldValidator(deviceIDField, "deviceID", deviceIDField.textProperty());
    createTextFieldValidator(manufacturerField, "manufacturer", manufacturerField.textProperty());
    createTextFieldValidator(modelIDField, "modelID", modelIDField.textProperty());
    createTextFieldValidator(modelNameField, "modelName", modelNameField.textProperty());
    createTextFieldValidator(modelYearField, "modelYear", modelYearField.textProperty());
    createTextFieldValidator(nicknameField, "nickname", nicknameField.textProperty());
    createTextFieldValidator(macAddressField, "macAddress", macAddressField.textProperty());

    // Init dropdowns
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
    addUserButton.setOnAction(event -> main.showUserEditor());
    addLocationButton.setOnAction(event -> main.showLocationEditor());
    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  private void onSave(ActionEvent event) {
    if (validator.containsErrors()) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Invalid input",
          "One or more required field values are missing or invalid.");
    } else if (osSelector.getCheckModel().getCheckedItems().size() == 0) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "No operating system selected",
          "No operating system has been selected for this device.");
    } else {
      // Set basic properties
      device.setId(deviceIDField.getText());
      device.setManufacturer(manufacturerField.getText());
      device.setModelID(modelIDField.getText());
      device.setModelName(modelNameField.getText());
      device.setModelYear(modelYearField.getText());
      device.setMacAddress(macAddressField.getText());
      device.setNickname(nicknameField.getText());

      // Determine selected hardware configuration
      val hwConfig =
          dao.configurations.getAll().get(getSelectedIndex(configSelector.getSelectionModel()));

      // Determine selected operating systems
      val oses = dao.operatingSystems.getAll();
      val osList = new ArrayList<OperatingSystem>();

      for (val selectedIndex : osSelector.getCheckModel().getCheckedIndices()) {
        osList.add(oses.get(selectedIndex));
      }

      // Determine metadata like user, location and status
      val user = dao.users.getAll().get(getSelectedIndex(userSelector.getSelectionModel()));
      val location =
          dao.locations.getAll().get(getSelectedIndex(locationSelector.getSelectionModel()));
      val status = dao.statuses.getAll().get(getSelectedIndex(statusSelector.getSelectionModel()));

      // Update device object
      device.setConfiguration(hwConfig);
      device.setOperatingSystems(osList);
      device.setUser(user);
      device.setLocation(location);
      device.setStatus(status);

      // Save
      dao.devices.save(device);

      // Close modal
      stage.close();
    }
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

  // TODO: More sophisticated validation for MAC addresses
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
}
