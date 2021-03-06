package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createTextFieldValidator;
import static org.otpr11.itassetmanagementapp.utils.JFXUtils.getChoiceIndex;
import static org.otpr11.itassetmanagementapp.utils.JFXUtils.select;

import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.PrettyStringifiable;
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
public class DeviceEditorController
    implements Initializable, ViewController, LocaleChangeListener, DatabaseEventListener {
  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;
  private static final DeviceStatus DEFAULT_DEVICE_STATUS = DeviceStatus.VACANT;
  private static ResourceBundle locale = LocaleEngine.getResourceBundle();
  private static final String SELECTOR_DEFAULT_TITLE = locale.getString("selector_default_title");
  private static boolean IS_EDIT_MODE;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Validator validator = new Validator();

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values())
          .map(DeviceType::toString)
          .map(type -> DeviceType.getLocalised(DeviceType.fromString(type)))
          .toList();
  private final List<String> deviceStatuses =
      dao.statuses.getAll().stream()
          .map(Status::toString)
          .map(status -> DeviceStatus.getLocalised(DeviceStatus.fromString(status)))
          .toList();
  private List<String> locations =
      dao.locations.getAll().stream().map(Location::getId).collect(Collectors.toList());
  private List<String> users =
      dao.users.getAll().stream().map(User::getId).collect(Collectors.toList());
  private List<String> operatingSystems =
      dao.operatingSystems.getAll().stream().map(OperatingSystem::toPrettyString).toList();

  private Device device = new Device();
  private List<String> configs = formatRelevantHWConfigs(DEFAULT_DEVICE_TYPE);

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  // FXML for the attributes and boxes of the device view
  @FXML
  private Text deviceTypeText,
      basicInfoText,
      deviceIDText,
      nicknameText,
      manufacturerText,
      modelNameText,
      modelIDText,
      modelYearText,
      macAddressText,
      configSelectorText,
      osSelectorText,
      metaText,
      userText,
      locationText,
      statusText;
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
  @FXML private CheckComboBox<String> osSelector;
  @FXML private ChoiceBox<String> deviceTypeSelector;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    DatabaseEventPropagator.addListener(this);
    LocaleEngine.addListener(this);

    // Init freeform text field validators
    createTextFieldValidator(validator, deviceIDField, "deviceID", deviceIDField.textProperty());
    createTextFieldValidator(
        validator, manufacturerField, "manufacturer", manufacturerField.textProperty());
    createTextFieldValidator(validator, modelIDField, "modelID", modelIDField.textProperty());
    createTextFieldValidator(validator, modelNameField, "modelName", modelNameField.textProperty());
    createTextFieldValidator(validator, nicknameField, "nickname", nicknameField.textProperty());
    createTextFieldValidator(
        validator, macAddressField, "macAddress", macAddressField.textProperty());

    // Init dropdowns

    initDropdown(statusSelector, deviceStatuses, DeviceStatus.getLocalised(DEFAULT_DEVICE_STATUS));
    initDropdown(deviceTypeSelector, deviceTypes, DeviceType.getLocalised(DEFAULT_DEVICE_TYPE));
    initDropdown(userSelector, users, true);
    initDropdown(locationSelector, locations, true);
    initDropdown(configSelector, configs, false);

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

    // FIXME: These could be moved to onLocaleChange() eventually, but that's not necessary for now
    configSelector.setValue(SELECTOR_DEFAULT_TITLE);
    userSelector.setValue(SELECTOR_DEFAULT_TITLE);
    locationSelector.setValue(SELECTOR_DEFAULT_TITLE);

    // Listen for status being set to VACANT, and remove user if selected
    statusSelector
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (options, oldValue, newValue) -> {
              if (newValue.equals(DeviceStatus.getLocalised(DeviceStatus.VACANT))) {
                val result =
                    AlertUtils.showAlert(
                        AlertType.CONFIRMATION,
                        locale
                            .getString("status_vacant_confirmation")
                            .formatted(DeviceStatus.getLocalised(DeviceStatus.VACANT)),
                        locale
                            .getString("status_vacant_confirmation_detail")
                            .formatted(DeviceStatus.getLocalised(DeviceStatus.VACANT)));

                if (result.getButtonData() == ButtonData.OK_DONE) {
                  userSelector.getSelectionModel().selectFirst();
                }
              }
            });

    // Input the list of selectable manufacturer years into the dropdown view starting from current
    // year.
    for (int year = Year.now().getValue(); year >= 1970; year--) {
      modelYearSelector.getItems().add(year);
    }

    // Set default values for fields
    select(modelYearSelector, Year.now().getValue());
    configSelector.setValue(SELECTOR_DEFAULT_TITLE);
    userSelector.setValue(SELECTOR_DEFAULT_TITLE);
    locationSelector.setValue(SELECTOR_DEFAULT_TITLE);

    // Button event listeners
    addHWConfigButton.setOnAction(
        event -> main.showHWConfigEditor(DeviceType.fromString(deviceTypeSelector.getValue())));
    addOSButton.setOnAction(event -> main.showOSEditor(null));
    addUserButton.setOnAction(event -> main.showUserEditor(null));
    addLocationButton.setOnAction(event -> main.showLocationEditor(null));
    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);

    onLocaleChange();
  }

  /**
   * Initialises a {@link ChoiceBox} with a list of items and an initial value.
   *
   * @param dropdown {@link ChoiceBox}
   * @param items {@link List}
   * @param initialValue Initial value for dropdown
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ChoiceBox dropdown, List items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  /**
   * Initialises a {@link ComboBox} without a default value, optionally making it possible to select
   * empty.
   *
   * @param dropdown {@link ComboBox}
   * @param items {@link List}
   * @param isNullable Whether to declare this dropdown as empty-selectable
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ComboBox dropdown, List items, boolean isNullable) {
    if (isNullable) {
      items.add(0, SELECTOR_DEFAULT_TITLE);
    }

    dropdown.getItems().setAll(items);
  }

  /**
   * Initialises a {@link ComboBox} with a list of items and an initial value.
   *
   * @param dropdown {@link ChoiceBox}
   * @param items {@link List}
   * @param initialValue Initial value for dropdown
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void initDropdown(ComboBox dropdown, List items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  /**
   * Save button click handler.
   *
   * @param event {@link ActionEvent}
   */
  private void onSave(ActionEvent event) {
    // Reset old colorings
    configSelectorText.setFill(null);
    osSelectorText.setFill(null);

    val noConfig = configSelector.getSelectionModel().getSelectedIndex() == -1;
    val noOS = osSelector.getCheckModel().getCheckedItems().size() == 0;

    if (validator.containsWarnings() || validator.containsErrors() || noConfig || noOS) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("invalid_input"),
          locale.getString("invalid_input_detail"));

      if (noConfig) {
        configSelectorText.setText("?????? " + locale.getString("hardware_configuration"));
        configSelectorText.setFill(Color.RED);
      } else {
        configSelectorText.setText(locale.getString("hardware_configuration"));
        configSelectorText.setFill(Color.BLACK);
      }

      if (noOS) {
        osSelectorText.setText("?????? " + locale.getString("operating_system"));
        osSelectorText.setFill(Color.RED);
      } else {
        osSelectorText.setText(locale.getString("operating_system"));
        osSelectorText.setFill(Color.BLACK);
      }
    } else if (statusSelector
            .getSelectionModel()
            .getSelectedItem()
            .equals(DeviceStatus.getLocalised(DeviceStatus.IN_USE))
        && getChoiceIndex(userSelector) <= 0) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale
              .getString("status_in_use_no_user")
              .formatted(DeviceStatus.getLocalised(DeviceStatus.IN_USE)),
          locale
              .getString("status_in_use_no_user_detail")
              .formatted(DeviceStatus.getLocalised(DeviceStatus.IN_USE)));
    } else if (!IS_EDIT_MODE && dao.devices.get(deviceIDField.getText()) != null) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("duplicate_id"),
          locale.getString("duplicate_id_detail"));
    } else if (IS_EDIT_MODE && !deviceIDField.getText().equals(device.getId())) {
      AlertUtils.showAlert(
          AlertType.ERROR,
          locale.getString("id_overwrite"),
          locale.getString("id_overwrite_detail"));
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
          getRelevantHWConfigs(DeviceType.fromString(deviceTypeSelector.getValue()))
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
      } else if (IS_EDIT_MODE) {
        device.setUser(null);
      }

      // If location is selected (optional)
      if (getChoiceIndex(locationSelector) > 0) {
        // Since this field is nullable, our indices get pushed 1 index forwards
        val location = dao.locations.getAll().get(getChoiceIndex(locationSelector) - 1);
        device.setLocation(location);
      } else if (IS_EDIT_MODE) {
        device.setLocation(null);
      }

      device.setStatus(status);

      // Save
      val success = dao.devices.save(device);

      if (!success) {
        AlertUtils.showAlert(
            AlertType.ERROR, locale.getString("error"), locale.getString("could_not_save_device"));
      } else {
        stage.close();
      }
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

  /**
   * Update available hardware configurations for a specific {@link DeviceType}.
   *
   * @param deviceType {@link DeviceType}
   */
  private void updateAvailableHWConfigs(DeviceType deviceType) {
    configs = formatRelevantHWConfigs(deviceType);
    initDropdown(configSelector, configs, configs.get(0));
  }

  /**
   * This alternate update function monkey-patches in support for adding a newly created entity to
   * the available hardware configurations. This is because Hibernate events propagate before a
   * database query will respond with the newly created entity it just told you it created, because
   * that totally makes sense.
   *
   * @param deviceType {@link DeviceType} to list hardware configurations for.
   * @param newEntity A newly created hardware configuration
   */
  private void updateAvailableHWConfigs(DeviceType deviceType, DTO newEntity) {
    configs = formatRelevantHWConfigs(deviceType);
    val stringTarget = (PrettyStringifiable) newEntity;
    configs.add(stringTarget.toPrettyString());
    initDropdown(configSelector, configs, stringTarget.toPrettyString());
  }

  /**
   * Formats all available hardware configurations for the selected {@link DeviceType}.
   *
   * @param deviceType {@link DeviceType}
   * @return {@link List} of formatted hardware configuration strings
   */
  private List<String> formatRelevantHWConfigs(DeviceType deviceType) {
    return getRelevantHWConfigs(deviceType).stream()
        .map(StringUtils::getPrettyHWConfig)
        .collect(Collectors.toList()); // Need mutability for this one
  }

  /**
   * Fetches all available hardware configurations for the selected {@link DeviceType}.
   *
   * @param deviceType {@link DeviceType}
   * @return {@link List} of hardware configurations
   */
  private List<Configuration> getRelevantHWConfigs(DeviceType deviceType) {
    return dao.configurations.getAll().stream()
        .filter(cfg -> cfg.getDeviceType() == deviceType)
        .collect(Collectors.toList()); // Need mutability for this one
  }

  @Override
  public void afterInitialize() {
    if (sceneChangeData != null
        && sceneChangeData instanceof String
        && dao.devices.get((String) sceneChangeData) != null) {
      IS_EDIT_MODE = true;
      log.trace("Editing existing device {}.", sceneChangeData);

      // Determine device to edit
      device = dao.devices.get((String) sceneChangeData);

      // Fill in data for this device
      val cfg = device.getConfiguration();
      select(
          deviceTypeSelector,
          DeviceType.getLocalised(cfg != null ? cfg.getDeviceType() : DEFAULT_DEVICE_TYPE));

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

      select(
          statusSelector,
          DeviceStatus.getLocalised(DeviceStatus.fromString(device.getStatus().toString())));
    } else {
      IS_EDIT_MODE = false;
      log.trace("Registering new device.");
    }

    val stageTitle = locale.getString("device_editor_stage_title");
    stage.setTitle(IS_EDIT_MODE ? "%s %s".formatted(stageTitle, sceneChangeData) : stageTitle);
  }

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    deviceTypeText.setText(locale.getString("device_type"));
    basicInfoText.setText(locale.getString("basic_information"));
    deviceIDText.setText(locale.getString("device_id"));
    nicknameText.setText(locale.getString("nickname"));
    manufacturerText.setText(locale.getString("manufacturer"));
    modelNameText.setText(locale.getString("model_name"));
    modelYearText.setText(locale.getString("model_year"));
    modelIDText.setText(locale.getString("model_id"));
    macAddressText.setText(locale.getString("mac_address"));
    configSelectorText.setText(locale.getString("hardware_configuration"));
    osSelectorText.setText(locale.getString("operating_system"));
    metaText.setText(locale.getString("metadata"));
    userText.setText(locale.getString("user"));
    locationText.setText(locale.getString("location"));
    statusText.setText(locale.getString("status"));
    cancelButton.setText(locale.getString("cancel"));
    okButton.setText(locale.getString("save"));
  }

  @Override
  public void onDatabaseEvent(DatabaseEvent event, DTO entity) {
    if (event == DatabaseEvent.POST_PERSIST) {
      if (entity instanceof DesktopConfiguration) {
        updateAvailableHWConfigs(DeviceType.DESKTOP, entity);
      } else if (entity instanceof LaptopConfiguration) {
        updateAvailableHWConfigs(DeviceType.LAPTOP, entity);
      } else if (entity instanceof OperatingSystem) {
        val list =
            dao.operatingSystems.getAll().stream()
                .map(OperatingSystem::toPrettyString)
                .collect(Collectors.toList());

        val newEntity = ((PrettyStringifiable) entity).toPrettyString();
        list.add(newEntity);
        operatingSystems = list;

        osSelector.getItems().add(newEntity);

        // Restore checks
        val checked = osSelector.getCheckModel();
        checked.check(newEntity);

        for (val item : checked.getCheckedItems()) {
          osSelector.getCheckModel().check(item);
        }
      } else if (entity instanceof User) {
        val list = dao.users.getAll().stream().map(User::getId).collect(Collectors.toList());
        val user = ((User) entity).getId();

        list.add(user);
        users = list;
        userSelector.getItems().add(user);

        select(userSelector, user);
      } else if (entity instanceof Location) {
        val list =
            dao.locations.getAll().stream().map(Location::getId).collect(Collectors.toList());
        val location = ((Location) entity).getId();

        list.add(location);
        locations = list;
        locationSelector.getItems().add(location);

        select(locationSelector, location);
      }
    }
  }
}
