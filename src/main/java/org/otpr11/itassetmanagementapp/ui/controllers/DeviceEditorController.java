package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.getChoiceIndex;
import static org.otpr11.itassetmanagementapp.utils.JFXUtils.select;

import java.net.URL;
import java.time.Year;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.synedra.validatorfx.Validator;
import org.controlsfx.control.CheckComboBox;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

/**
 * Device editor controller class.
 *
 * <p>This class accepts a device ID as scene change data, to change the title of window based on
 * whether a device is being created or edited.
 */
@Log4j2
public class DeviceEditorController implements Initializable, ViewController {
  private final ResourceBundle locale = LocaleEngine.getResourceBundle();
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static final DeviceStatus DEFAULT_DEVICE_STATUS = DeviceStatus.VACANT;
  private static final String SELECTOR_DEFAULT_TITLE = "Select...";
  private static boolean IS_EDIT_MODE;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Validator validator = new Validator();
  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());
  private final List<String> deviceStatuses =
      dao.statuses.getAll().stream().map(Status::toString).collect(Collectors.toList());
  private final List<String> users =
      dao.users.getAll().stream().map(User::getId).collect(Collectors.toList());
  private final List<String> locations =
      dao.locations.getAll().stream().map(Location::getId).collect(Collectors.toList());
  private final List<String> operatingSystems =
      dao.operatingSystems.getAll().stream()
          .map(OperatingSystem::toPrettyString)
          .collect(Collectors.toList());
  public Text deviceType;
  public Text basicText;
  public Text deviceIdText;
  public Text nicknameText;
  public Text manufacturerText;
  public Text modelText;
  public Text modelIdText;
  public Text macText;
  public Text hwText;
  public Text osText;
  public Text metaText;
  public Text userText;
  public Text locaText;
  public Text status;


  private List<String> configs = formatRelevantHWConfigs(DEFAULT_DEVICE_TYPE);

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  /**
   * FXML for the attributes and boxes of the device view
    */


  @FXML private CheckComboBox<String> osSelector;
  @FXML private ChoiceBox<String> deviceTypeSelector;
  @FXML
  private TextField deviceIDField,
      manufacturerField,
      modelIDField,
      modelNameField,
      nicknameField,
      macAddressField;
  @FXML
  private Button addHWConfigButton,
      addOSButton,
      addUserButton,
      addLocationButton,
      okButton,
      cancelButton;
  @FXML private ComboBox<String> configSelector, statusSelector, userSelector, locationSelector;
  @FXML private ComboBox<Integer> modelYearSelector;
  @FXML private Text configSelectorTitle, osSelectorTitle;

  /**
   * Text field validations for text fields
   * Initializing the start of the device editor view
    */


  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Init freeform text field validators


    createTextFieldValidator(deviceIDField, "deviceID", deviceIDField.textProperty());
    createTextFieldValidator(manufacturerField, "manufacturer", manufacturerField.textProperty());
    createTextFieldValidator(modelIDField, "modelID", modelIDField.textProperty());
    createTextFieldValidator(modelNameField, "modelName", modelNameField.textProperty());
    createTextFieldValidator(nicknameField, "nickname", nicknameField.textProperty());
    createTextFieldValidator(macAddressField, "macAddress", macAddressField.textProperty());

    // Init dropdowns


    initDropdown(statusSelector, deviceStatuses, DEFAULT_DEVICE_STATUS.toString());
    initDropdown(deviceTypeSelector, deviceTypes, DEFAULT_DEVICE_TYPE.toString());
    initDropdown(userSelector, users, true);
    initDropdown(locationSelector, locations, true);
    initDropdown(configSelector, configs, false);

    //Set texts to selected language
    deviceType.setText(locale.getString("devicetype"));
    basicText.setText(locale.getString("basicInfo"));
    deviceIdText.setText(locale.getString("deviceid"));
    nicknameText.setText(locale.getString("nickname"));
    manufacturerText.setText(locale.getString("manufacturer"));
    modelText.setText(locale.getString("modelname"));
    modelIdText.setText(locale.getString("modelid"));
    macText.setText(locale.getString("mac"));
    hwText.setText(locale.getString("hwconf"));
    osText.setText(locale.getString("os"));
    metaText.setText(locale.getString("meta"));
    userText.setText(locale.getString("user"));
    locaText.setText(locale.getString("location"));
    status.setText(locale.getString("status"));
    cancelButton.setText(locale.getString("cancel"));
    okButton.setText(locale.getString("ok"));

    // Update HW configs when device type changes

    deviceTypeSelector.setOnAction(
        event -> {
          val type = DeviceType.valueOf(deviceTypeSelector.getSelectionModel().getSelectedItem());
          updateAvailableHWConfigs(type);
        });

    // Init OS selector
    // Needed for reassignment in lambda

    var ref =
        new Object() {
          String lastChange = "";
        };

    osSelector.getItems().addAll(operatingSystems);
    osSelector.setTitle(SELECTOR_DEFAULT_TITLE);
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
                    // FIXME: This doesn't work correctly if two OSes have the same pretty string,
                    // but we'll have to address that some other way
                    if (!changeListString.equals(ref.lastChange)) {
                      ref.lastChange = changeListString;

                      if (changeList.size() != 0) {
                        osSelector.setTitle(String.join(", ", changeList));
                      } else {
                        osSelector.setTitle(SELECTOR_DEFAULT_TITLE);
                      }
                    }
                  }
                });

    // Listen for status being set to VACANT, and remove user if selected

    statusSelector
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (options, oldValue, newValue) -> {
              if (newValue.equals(DeviceStatus.VACANT.toString())) {
                val result =
                    AlertUtils.showAlert(
                        AlertType.CONFIRMATION,
                        "Set status to VACANT and remove user?",
                        "Setting device status to VACANT will remove its current user. Are you sure you want to proceed?");

                if (result.getButtonData() == ButtonData.OK_DONE) {
                  userSelector.getSelectionModel().selectFirst();
                }
              }
            });

    // Making the list of selectable manufacturer years into the dropdown view starting from current year.

    for (int year = Year.now().getValue(); year >= 1970; year--) {
      modelYearSelector.getItems().add(year);
    }

    // Setting default values for fields

    select(modelYearSelector, Year.now().getValue());
    configSelector.setValue(SELECTOR_DEFAULT_TITLE);
    userSelector.setValue(SELECTOR_DEFAULT_TITLE);
    locationSelector.setValue(SELECTOR_DEFAULT_TITLE);

    // Actions for adding buttons

    addHWConfigButton.setOnAction(event -> main.showHWConfigEditor(null));
    addOSButton.setOnAction(event -> main.showOSEditor(null));
    addUserButton.setOnAction(event -> main.showUserEditor(null));
    addLocationButton.setOnAction(event -> main.showLocationEditor(null));
    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ChoiceBox dropdown, List items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ComboBox dropdown, List items, boolean isNullable) {
    if (isNullable) {
      items.add(0, SELECTOR_DEFAULT_TITLE);
    }

    dropdown.getItems().setAll(items);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ComboBox dropdown, List items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  private void onSave(ActionEvent event) {
    // Reset old colorings
    configSelectorTitle.setFill(null);
    osSelectorTitle.setFill(null);

    val noConfig = configSelector.getSelectionModel().getSelectedIndex() == -1;
    val noOS = osSelector.getCheckModel().getCheckedItems().size() == 0;

    if (validator.containsWarnings() || validator.containsErrors() || noConfig || noOS) {
      AlertUtils.showAlert(
          AlertType.ERROR, "Invalid input", "One or more required values are missing or invalid.");

      if (noConfig) {
        // FIXME: Replace with localised version, this won't reset now, but it doesn't matter as we
        // need to get it from locale anyway
        configSelectorTitle.setText("⚠️ " + configSelectorTitle.getText());
        configSelectorTitle.setFill(Color.RED);
      }

      if (noOS) {
        // FIXME: Replace with localised version, this won't reset now, but it doesn't matter as we
        // need to get it from locale anyway
        osSelectorTitle.setText("⚠️ " + osSelectorTitle.getText());
        osSelectorTitle.setFill(Color.RED);
      }
    } else if (statusSelector
            .getSelectionModel()
            .getSelectedItem()
            .equals(DeviceStatus.IN_USE.toString())
        && getChoiceIndex(userSelector) <= 0) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Status set to IN_USE, but device has no user",
          "A device cannot be set to status IN_USE without a user being selected.");
    } else if (!IS_EDIT_MODE && dao.devices.get(deviceIDField.getText()) != null) {
      // FIXME: This might allow overwriting, need to check it in a more sophisticated manner
      AlertUtils.showAlert(
          AlertType.ERROR, "Duplicate ID detected", "A device with this ID already exists.");
    } else if (IS_EDIT_MODE && !deviceIDField.getText().equals(device.getId())) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          "Attempted device overwrite detected",
          "A device with the ID you're changing this device's ID to already exists.");
    } else {
      // Set basic properties
      device.setId(deviceIDField.getText());
      device.setManufacturer(manufacturerField.getText());
      device.setModelID(modelIDField.getText());
      device.setModelName(modelNameField.getText());
      device.setModelYear(modelYearSelector.getSelectionModel().getSelectedItem().toString());
      device.setMacAddress(macAddressField.getText());
      device.setNickname(nicknameField.getText());

      // Determine selected hardware configuration
      val hwConfig =
          getRelevantHWConfigs(DeviceType.valueOf(deviceTypeSelector.getValue()))
              .get(getChoiceIndex(configSelector));

      // Determine selected operating systems
      val oses = dao.operatingSystems.getAll();
      val osList = new ArrayList<OperatingSystem>();

      for (val selectedIndex : osSelector.getCheckModel().getCheckedIndices()) {
        osList.add(oses.get(selectedIndex));
      }

      // Determine metadata like user, location and status
      val status = dao.statuses.getAll().get(getChoiceIndex(statusSelector));

      // Update device object
      device.setConfiguration(hwConfig);
      device.setOperatingSystems(osList);

      // If user is selected (optional)
      if (getChoiceIndex(userSelector) > 0) {
        // Since this field is nullable, our indices get pushed 1 index forwards
        val user = dao.users.getAll().get(getChoiceIndex(userSelector) - 1);
        device.setUser(user);
      }

      // If location is selected (optional)
      if (getChoiceIndex(locationSelector) > 0) {
        // Since this field is nullable, our indices get pushed 1 index forwards
        val location = dao.locations.getAll().get(getChoiceIndex(locationSelector) - 1);
        device.setLocation(location);
      }

      device.setStatus(status);

      // Save
      val success = dao.devices.save(device);

      if (!success) {
        AlertUtils.showAlert(AlertType.ERROR, "Error", "Could not save device.");
      } else {
        stage.close();
      }
    }
  }

  /**
   * Functional cancel button
    */

  private void onCancel(ActionEvent event) {
    stage.close();
  }

  private void updateAvailableHWConfigs(DeviceType deviceType) {
    configs = formatRelevantHWConfigs(deviceType);
    initDropdown(configSelector, configs, configs.get(0));
  }

  private List<String> formatRelevantHWConfigs(DeviceType deviceType) {
    return getRelevantHWConfigs(deviceType).stream()
        .map(StringUtils::getPrettyHWConfig)
        .collect(Collectors.toList());
  }

  private List<Configuration> getRelevantHWConfigs(DeviceType deviceType) {
    return dao.configurations.getAll().stream()
        .filter(cfg -> cfg.getDeviceType() == deviceType)
        .collect(Collectors.toList());
  }

  // TODO: More sophisticated validation for MAC addresses, number fields, etc.
  // Text field validation.
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
  public void afterInitialize() {
    if (sceneChangeData != null
        && sceneChangeData instanceof String
        && dao.devices.get((String) sceneChangeData) != null) {
      IS_EDIT_MODE = true;
      log.trace("Editing existing device {}.", sceneChangeData);
      stage.setTitle("Manage device %s".formatted(sceneChangeData));

      // Determine device to edit
      device = dao.devices.get((String) sceneChangeData);

      // Fill in data for this device
      val cfg = device.getConfiguration();
      select(
          deviceTypeSelector,
          cfg != null ? cfg.getDeviceType().toString() : DeviceType.LAPTOP.toString());

      deviceIDField.setText(device.getId());
      nicknameField.setText(device.getNickname());
      manufacturerField.setText(device.getManufacturer());
      modelNameField.setText(device.getModelName());
      modelIDField.setText(device.getModelID());
      select(modelYearSelector, Integer.parseInt(device.getModelYear()));
      macAddressField.setText(device.getMacAddress());

      if (device.getConfiguration() != null) {
        select(configSelector, StringUtils.getPrettyHWConfig(device.getConfiguration()));
      }

      val checkModel = osSelector.getCheckModel();

      for (val os : device.getOperatingSystems()) {
        checkModel.check(os.toPrettyString());
      }

      if (device.getUser() != null) {
        select(userSelector, device.getUser().getId());
      }

      if (device.getLocation() != null) {
        select(locationSelector, device.getLocation().getId());
      }

      select(statusSelector, device.getStatus().toString());
    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new device.");
      stage.setTitle(locale.getString("device_editor_stage_title"));
    }
  }
}
