package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DatabaseEvent;
import org.otpr11.itassetmanagementapp.db.core.DTO;
import org.otpr11.itassetmanagementapp.db.core.DatabaseEventPropagator;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.ui.utils.CellDataFormatter;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils;

@Log4j2
public class ManagementViewController
    implements Initializable, ViewController, DatabaseEventListener {

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final BorderPane prettyDevicePane = new BorderPane();
  private Device device = new Device();
  private User user = new User();
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;
  @FXML private TableView<Device> managementTable;
  @FXML private TableColumn<Device, String> configColumn;
  @FXML private TableColumn<Device, String> osColumn;
  @FXML private TableColumn<Device, String> userColumn;
  @FXML private TableColumn<Device, String> locationColumn;
  @FXML private TableColumn<Device, Device> configActionColumn;
  @FXML private TableColumn<Device, Device> osActionColumn;
  @FXML private TableColumn<Device, Device> userActionColumn;
  @FXML private TableColumn<Device, Device> locationActionColumn;
  @FXML private BorderPane managementViewPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DatabaseEventPropagator.addListener(this);

    PrettyDeviceViewerController.init(managementViewPane, prettyDevicePane);

    configColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));
    osColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));

    val moreInfoTooltip = new Tooltip("Double-click show information about device");

    managementTable.setRowFactory(
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

          return row;
        });

    configColumn.setCellValueFactory(CellDataFormatter::formatHWConfig);
    osColumn.setCellValueFactory(CellDataFormatter::formatOS);
    userColumn.setCellValueFactory(CellDataFormatter::formatUser);
    locationColumn.setCellValueFactory(CellDataFormatter::formatLocation);

    configActionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    configActionColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                val cfg = device.getConfiguration();

                if (cfg != null) {
                  switch (cfg.getDeviceType()) {
                    case DESKTOP -> setGraphic(deleteConfigButton(cfg.getDesktopConfiguration().getId()));
                    case LAPTOP -> setGraphic(deleteConfigButton(cfg.getLaptopConfiguration().getId()));
                  }
                } else {
                  setGraphic(deleteConfigButton(null));
                }
              }
            });

    osActionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    osActionColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(deleteOSButton(device.getId()));
              }
            });

    userActionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    userActionColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(
                    deleteUserButton(device.getUser() != null ? device.getUser().getId() : null));
              }
            });

    locationActionColumn.setCellValueFactory(
        param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    locationActionColumn.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                setGraphic(
                    deleteLocationButton(
                        device.getLocation() != null ? device.getLocation().getId() : null));
              }
            });

    updateItems(dao.devices.getAll());
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

  private void handleEditHWConfig(Long configID) {
    main.showHWConfigEditor(configID);
  }

  private void handleEditOS(String deviceID) {
    val toEdit = determineOS(dao.devices.get(deviceID));
    main.showOSEditor(toEdit);
  }

  private void handleEditUser(String userID) {
    main.showUserEditor(userID);
  }

  private void handleEditLocation(String locationID) {
    main.showLocationEditor(locationID);
  }

  private void handleDeleteConfig(Long configID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete configuration %s?".formatted(configID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.configurations.delete(dao.configurations.get(configID));
    }
  }

  private void handleDeleteOS(String deviceID) {
    val toDelete = determineOS(dao.devices.get(deviceID));

    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete operating system %s?".formatted(deviceID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {
      dao.operatingSystems.delete(dao.operatingSystems.get(toDelete));
    }
  }

  private void handleDeleteUser(String userID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete user %s?".formatted(userID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.users.delete(dao.users.get(userID));
    }
  }

  private void handleDeleteLocation(String locationID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete location %s?".formatted(locationID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.locations.delete(dao.locations.get(locationID));
    }
  }

  private SplitMenuButton deleteConfigButton(Long configID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditHWConfig(configID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDeleteConfig(configID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton deleteOSButton(String deviceID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditOS(deviceID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDeleteOS(deviceID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton deleteUserButton(String userID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditUser(userID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDeleteUser(userID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton deleteLocationButton(String locationID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditLocation(locationID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDeleteLocation(locationID));
    items.add(deleteItem);

    return button;
  }

  private void updateItems(List<Device> devices) {
    devices.sort(Comparator.comparing(Device::getId));
    managementTable.setItems(FXCollections.observableArrayList(devices));
  }

  private void updateOS(List<Device> devices) {
    devices.sort(Comparator.comparing(Device::getId));
    // managementTable.setItems(FXCollections.observableArrayList(devices));
  }

  private void updateUsers(List<User> users) {
    users.sort(Comparator.comparing(User::getId));
    managementTable.getItems().clear();
    // managementTable.setItems(FXCollections.observableArrayList(devices));
  }

  private void updateLocations(List<Location> locations) {
    locations.sort(Comparator.comparing(Location::getId));
    managementTable.getItems().clear();
    // managementTable.setItems(FXCollections.observableArrayList(locations));
  }

  private Long determineOS(Device device) {
    val oses = device.getOperatingSystems();
    Long toEdit = null;

    if (oses.size() == 1) {
      toEdit = oses.get(0).getId();
    } else if (oses.size() > 1) {
      // HACK: This is such a megahack, but desperate times call for desperate measures
      // This means we can only have 9 operating systems per device, but that should be
      // enough given the time we have available
      val buttonDataTypes = ButtonData.values();
      val customButtons = new ArrayList<ButtonType>();

      for (int i = 0; i < oses.size(); i++) {
        customButtons.add(new ButtonType(oses.get(i).toPrettyString(), buttonDataTypes[i]));
      }

      // Figure out what operating system to edit
      val alert =
          new Alert(
              AlertType.CONFIRMATION, "Which operating system do you want to edit?", customButtons.toArray(new ButtonType[]{}));
      alert.setHeaderText(null);
      alert.setContentText(
          "There are multiple operating systems associated with this computer. Which one do you want to edit?");
      alert.showAndWait();
      val result = alert.getResult();

      toEdit = oses.get(customButtons.indexOf(result)).getId();
    }

    return toEdit;
  }

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
          Integer updatedIndex = null;

          for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getId().equals(((Device) entity).getId())) {
              updatedIndex = i;
            }
          }

          if (updatedIndex == null) {
            throw new IllegalStateException("Non-existent device %s updated");
          }

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

  @Override
  public void afterInitialize() {}
}
