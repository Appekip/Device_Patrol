package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.ui.utils.CellDataFormatter;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils;

/** Main application view controller class. */
@Log4j2
public class MainViewController
    implements Initializable, ViewController, DatabaseEventListener, LocaleChangeListener {

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final BorderPane prettyDevicePane = new BorderPane();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();
  private List<String> statuses; // Will be populated eventually
  private Tooltip moreInfoTooltip = new Tooltip(locale.getString("show_device_info_tooltip"));

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  @FXML
  private TableColumn<Device, String> idColumn,
      nicknameColumn,
      manufacturerColumn,
      modelNameColumn,
      modelIDColumn,
      modelYearColumn,
      deviceTypeColumn,
      hwConfigurationColumn,
      userColumn,
      locationColumn,
      osColumn;
  @FXML
  private MenuItem menuItemAbout,
      menuItemDevice,
      menuItemHW,
      menuItemOS,
      menuItemUser,
      menuItemLocation,
      menuItemManage;
  @FXML private TableView<Device> deviceTable;
  @FXML private TableColumn<Device, Device> statusColumn, actionColumn;
  @FXML private BorderPane deviceViewPane;
  @FXML private Menu menuNew, menuFile, menuHelp, menuLang;
  @FXML private MenuItem langEng, langFin, langSwe;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    LocaleEngine.addListener(this);
    DatabaseEventPropagator.addListener(this);
    PrettyDeviceViewerController.init(deviceViewPane, prettyDevicePane);

    hwConfigurationColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));
    osColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));

    deviceTable.setRowFactory(
        tableView -> {
          TableRow<Device> row = new TableRow<>();

          row.setOnMouseEntered(
              event -> {
                // Show pointer cursor and tooltip when hovering over rows that have items
                // We have to it on hover because we can't set the pointer style properly at
                // startup, and instead must do it dynamically at runtime because JavaFX
                // HACK: If someone from the future is trying to style rows and wonders why their
                // styles keep resetting, this is why
                if (row.getItem() != null) {
                  // Also thanks JavaFX for renaming all the cursors for no reason
                  row.setStyle("-fx-cursor: hand;");
                  row.setTooltip(moreInfoTooltip);
                }
              });

          // Detect row double click
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                  handleViewClick(row.getItem().getId(), true);
                }
              });

          return row;
        });

    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
    manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
    modelNameColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
    modelIDColumn.setCellValueFactory(new PropertyValueFactory<>("modelID"));
    modelYearColumn.setCellValueFactory(new PropertyValueFactory<>("modelYear"));
    deviceTypeColumn.setCellValueFactory(CellDataFormatter::formatDeviceType);
    hwConfigurationColumn.setCellValueFactory(CellDataFormatter::formatHWConfig);
    osColumn.setCellValueFactory(CellDataFormatter::formatOS);
    userColumn.setCellValueFactory(CellDataFormatter::formatUser);
    locationColumn.setCellValueFactory(CellDataFormatter::formatLocation);

    updateStatusDropdowns();

    actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    actionColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(createDeviceActionButton(device.getId()));
              }
            });
    updateItems(dao.devices.getAll());

    onLocaleChange();
  }

  @FXML
  private void handleNewDeviceClick() {
    main.showDeviceEditor(null);
  }

  @FXML
  private void handleNewHWConfigurationClick() {
    main.showHWConfigEditor(null);
  }

  @FXML
  private void handleNewOSClick() {
    main.showOSEditor(null);
  }

  @FXML
  private void handleNewUserClick() {
    main.showUserEditor(null);
  }

  @FXML
  private void handleNewLocationClick() {
    main.showLocationEditor(null);
  }

  @FXML
  private void handleManageClick() {
    main.showManagementView(null);
  }

  @FXML
  private void handleAboutClick() {
    log.trace("Open about");
  }

  /**
   * (Double-) click handler for device rows.
   *
   * @param deviceID ID of device in this row
   * @param wasDoubleClick Whether two clicks occurred in quick succession
   */
  private void handleViewClick(String deviceID, boolean wasDoubleClick) {
    // Hide on double click of same item
    if (wasDoubleClick
        && PrettyDeviceViewerController.isOpen()
        && PrettyDeviceViewerController.getCurrentDeviceID().equals(deviceID)) {
      PrettyDeviceViewerController.setCurrentDeviceID(null);
      PrettyDeviceViewerController.hide();
    } else {
      PrettyDeviceViewerController.setCurrentDeviceID(deviceID);
      PrettyDeviceViewerController.show(deviceID);
    }
  }

  private void handleEditClick(String deviceID) {
    main.showDeviceEditor(deviceID);
  }

  private void handleDeleteClick(String deviceID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            locale.getString("are_you_sure"),
            "%s %s?".formatted(locale.getString("device_delete_confirmation"), deviceID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {
      dao.devices.delete(dao.devices.get(deviceID));
    }
  }

  /** Menu button splitting and functionality */
  private SplitMenuButton createDeviceActionButton(String deviceID) {
    val button = new SplitMenuButton();
    button.setText(locale.getString("edit"));
    button.setOnAction(event -> handleEditClick(deviceID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val viewItem = new MenuItem();
    viewItem.setText(locale.getString("view"));
    viewItem.setOnAction(event -> handleViewClick(deviceID, false));
    items.add(viewItem);

    val deleteItem = new MenuItem();
    deleteItem.setText(locale.getString("delete"));
    deleteItem.setOnAction(event -> handleDeleteClick(deviceID));
    items.add(deleteItem);

    return button;
  }

  /** Status dropdown */
  private ChoiceBox<String> createStatusDropdown(String deviceID) {
    val dropdown = new ChoiceBox<String>();
    statuses.forEach(status -> dropdown.getItems().add(status));

    // Because the dropdown items are localised, we need to do a reverse lookup first
    JFXUtils.select(
        dropdown,
        DeviceStatus.getLocalised(
            DeviceStatus.fromString(dao.devices.get(deviceID).getStatus().toString())));

    dropdown.setOnAction(
        event -> {
          // As the dropdown items are localised, we need to do a reverse localisation before we
          // can find the correct one in the DB
          val delocalised = DeviceStatus.fromString(dropdown.getValue()).toString();
          updateDeviceStatus(deviceID, dao.statuses.get(delocalised));
        });

    // Don't show pointer cursor or tooltip for this element
    dropdown.setCursor(Cursor.DEFAULT);
    dropdown.setTooltip(null);

    return dropdown;
  }

  private void updateStatusDropdowns() {
    statuses =
        dao.statuses.getAll().stream()
            .map(status -> DeviceStatus.getLocalised(DeviceStatus.fromString(status.toString())))
            .toList();

    statusColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    statusColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(createStatusDropdown(device.getId()));
              }
            });
  }

  /** Updating items and device statuses */
  private void updateItems(List<Device> devices) {

    devices.sort(Comparator.comparing(Device::getId));
    deviceTable.getItems().clear();
    deviceTable.setItems(FXCollections.observableArrayList(devices));
  }

  private void updateDeviceStatus(String deviceID, Status status) {
    val device = dao.devices.get(deviceID);
    device.setStatus(status);
    dao.devices.save(device);
  }

  /** Updating items to database */
  @Override
  public void onDatabaseEvent(DatabaseEvent event, DTO entity) {
    switch (event) {
      case PRE_REMOVE, POST_REMOVE -> {
        if (entity instanceof Device device) {
          // Hibernate results may take some time to become fully accurate, let's remove the deleted
          // device in the meantime
          val filtered =
              dao.devices.getAll().stream()
                  .filter(d -> !d.getId().equals(device.getId()))
                  .toList();

          updateItems(filtered);
        }
      }
      case POST_UPDATE -> {
        if (entity instanceof Device device) {
          val devices = dao.devices.getAll();
          val updatedIndex = devices.indexOf(device);
          devices.set(updatedIndex, device);
          updateItems(devices);
        }
      }
      case POST_PERSIST -> {
        // Hibernate results take some time to become 100% accurate, so let's add our new device
        // to the list manually in the meantime
        if (entity instanceof Device device) {
          val devices = dao.devices.getAll();
          devices.add(device);
          updateItems(devices);
        }
      }
    }
  }

  public void handleEngClick(ActionEvent actionEvent) {
    LocaleEngine.setUserLocale(Locale.ENGLISH);
  }

  public void handleFinClick(ActionEvent actionEvent) {
    LocaleEngine.setUserLocale(Locale.forLanguageTag("fi-FI"));
  }

  public void handleSweClick(ActionEvent actionEvent) {
    LocaleEngine.setUserLocale(Locale.forLanguageTag("sv-SV"));
  }

  @Override
  public void afterInitialize() {}

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    updateStatusDropdowns();
    moreInfoTooltip = new Tooltip(locale.getString("show_device_info_tooltip"));
    idColumn.setText(locale.getString("id"));
    nicknameColumn.setText(locale.getString("nickname"));
    manufacturerColumn.setText(locale.getString("manufacturer"));
    modelNameColumn.setText(locale.getString("model_name"));
    modelIDColumn.setText(locale.getString("model_id"));
    modelYearColumn.setText(locale.getString("model_year"));
    deviceTypeColumn.setText(locale.getString("device_type"));
    hwConfigurationColumn.setText(locale.getString("hardware_configuration"));
    osColumn.setText(locale.getString("operating_system"));
    userColumn.setText(locale.getString("user"));
    locationColumn.setText(locale.getString("location"));
    actionColumn.setText(locale.getString("action"));
    statusColumn.setText(locale.getString("status"));
    menuFile.setText(locale.getString("file"));
    menuNew.setText(locale.getString("new"));
    menuItemDevice.setText(locale.getString("device"));
    menuItemHW.setText(locale.getString("hardware_configuration"));
    menuItemOS.setText(locale.getString("operating_system"));
    menuItemUser.setText(locale.getString("user"));
    menuItemLocation.setText(locale.getString("location"));
    menuItemManage.setText(locale.getString("manage"));

    menuHelp.setText(locale.getString("help"));
    menuItemAbout.setText(locale.getString("about"));

    menuLang.setText(locale.getString("lang"));
    langEng.setText(locale.getString("lang_en"));
    langFin.setText(locale.getString("lang_fi"));
    langSwe.setText(locale.getString("lang_sv"));

    PrettyDeviceViewerController.init(deviceViewPane, prettyDevicePane);
  }
}
