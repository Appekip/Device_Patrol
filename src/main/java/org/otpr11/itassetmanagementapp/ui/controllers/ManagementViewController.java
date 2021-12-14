package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
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
  // @FXML private TableColumn<Device, Device> actionColumn;
  @FXML private TableColumn<Device, Device> action1Column;
  @FXML private TableColumn<Device, Device> action2Column;
  @FXML private TableColumn<Device, Device> action3Column;
  @FXML private TableColumn<Device, Device> action4Column;
  @FXML private BorderPane managementViewPane;
  // private String userID;

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

          // Detect row double click
          // row.setOnMouseClicked(
          // event -> {
          // if (event.getClickCount() == 2 && !row.isEmpty()) {
          // handleViewClick(row.getItem().getId(), true);
          // }
          // });

          return row;
        });

    // configColumn.setCellValueFactory(new PropertyValueFactory<>("config"));
    configColumn.setCellValueFactory(CellDataFormatter::formatHWConfig);
    // osColumn.setCellValueFactory(new PropertyValueFactory<>("os"));
    osColumn.setCellValueFactory(CellDataFormatter::formatOS);
    // userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
    userColumn.setCellValueFactory(CellDataFormatter::formatUser);
    // locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    locationColumn.setCellValueFactory(CellDataFormatter::formatLocation);

    action1Column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    action1Column.setCellFactory(
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
                    case DESKTOP -> setGraphic(delete1Button(cfg.getDesktopConfiguration().getId()));
                    case LAPTOP -> setGraphic(delete1Button(cfg.getLaptopConfiguration().getId()));
                  }
                } else {
                  setGraphic(delete1Button(null));
                }
              }
            });

    action2Column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    action2Column.setCellFactory(
        param ->
            new TableCell<>() {
              @Override
              protected void updateItem(Device device, boolean b) {
                super.updateItem(device, b);

                if (device == null) {
                  setGraphic(null);
                  return;
                }

                // TODO: Multiple operating systems, how do we decide which to pick?
                setGraphic(delete2Button(device.getId()));
              }
            });

    action3Column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    action3Column.setCellFactory(
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
                    delete3Button(device.getUser() != null ? device.getUser().getId() : null));
              }
            });

    action4Column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    action4Column.setCellFactory(
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
                    delete4Button(
                        device.getLocation() != null ? device.getLocation().getId() : null));
              }
            });

    // action4Column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    // action4Column.setCellFactory(
    // param ->
    // new TableCell<>() {
    // @Override
    // protected void updateItem(Location location, boolean b) {
    // super.updateItem(location, b);

    // if (location == null) {
    // setGraphic(null);
    // return;
    // }

    // setGraphic(delete4Button(location.getId()));
    // }
    // });

    updateConfigs(dao.devices.getAll());
    // updateUsers(dao.users.getAll());
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

  private void handleEditOS(String osID) {
    main.showOSEditor(osID);
  }

  private void handleEditUser(String userID) {
    main.showUserEditor(userID);
  }

  private void handleEditLocation(String locationID) {
    main.showLocationEditor(locationID);
  }

  private void handleDelete1Click(Long configID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete device %s?".formatted(configID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.configurations.delete(dao.configurations.get(configID));
    }
  }

  private void handleDelete2Click(String deviceID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete device %s?".formatted(deviceID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.operatingSystems.delete(dao.operatingSystems.get(deviceID));
    }
  }

  private void handleDelete3Click(String userID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete device %s?".formatted(userID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.users.delete(dao.users.get(userID));
    }
  }

  private void handleDelete4Click(String locationID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete location %s?".formatted(locationID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {

      dao.locations.delete(dao.locations.get(locationID));
    }
  }

  private SplitMenuButton delete1Button(Long configID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditHWConfig(configID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDelete1Click(configID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton delete2Button(String osID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditOS(osID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDelete2Click(osID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton delete3Button(String userID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditUser(userID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDelete3Click(userID));
    items.add(deleteItem);

    return button;
  }

  private SplitMenuButton delete4Button(String locationID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditLocation(locationID));
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDelete4Click(locationID));
    items.add(deleteItem);

    return button;
  }

  private void updateConfigs(List<Device> devices) {
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
                  .collect(Collectors.toList());

          updateConfigs(filtered);
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
          updateConfigs(devices);
        }
      }
      case POST_PERSIST -> {
        // Hibernate results take some time to become 100% accurate, so let's add our new device
        // to the list manually in the meantime
        if (entity instanceof Device device) {
          val devices = dao.devices.getAll();
          devices.add(device);
          updateConfigs(devices);
        }
      }
    }
  }

  @Override
  public void afterInitialize() {}
}
