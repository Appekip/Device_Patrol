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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.ui.utils.CellDataFormatter;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;

@Log4j2
public class MainViewController implements Initializable, ViewController, DatabaseEventListener {
  private final GlobalDAO dao = GlobalDAO.getInstance();
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  @FXML private TableView<Device> deviceTable;
  @FXML private TableColumn<Device, String> idColumn;
  @FXML private TableColumn<Device, String> nicknameColumn;
  @FXML private TableColumn<Device, String> manufacturerColumn;
  @FXML private TableColumn<Device, String> modelNameColumn;
  @FXML private TableColumn<Device, String> modelIDColumn;
  @FXML private TableColumn<Device, String> modelYearColumn;
  @FXML private TableColumn<Device, String> deviceTypeColumn;
  @FXML private TableColumn<Device, String> hwConfigurationColumn;
  @FXML private TableColumn<Device, String> userColumn;
  @FXML private TableColumn<Device, String> statusColumn;
  @FXML private TableColumn<Device, String> locationColumn;
  @FXML private TableColumn<Device, String> osColumn;
  @FXML private TableColumn<Device, Device> actionColumn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DatabaseEventPropagator.addListener(this);

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
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    locationColumn.setCellValueFactory(CellDataFormatter::formatLocation);

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
  private void handleAboutClick() {
    log.trace("Open about");
  }

  private void handleViewClick(String deviceID) {
    // TODO: Pretty device view
    log.trace("Opening view menu for device {}.", deviceID);
  }

  private void handleEditClick(String deviceID) {
    main.showDeviceEditor(deviceID);
  }

  private void handleDeleteClick(String deviceID) {
    val actionResult =
        AlertUtils.showAlert(
            AlertType.CONFIRMATION,
            "Are you sure?",
            "Are you sure you want to delete device %s?".formatted(deviceID));

    if (actionResult.getButtonData() == ButtonData.OK_DONE) {
      dao.devices.delete(dao.devices.get(deviceID));
    }
  }

  private SplitMenuButton createDeviceActionButton(String deviceID) {
    val button = new SplitMenuButton();
    button.setText("Edit");
    button.setOnAction(event -> handleEditClick(deviceID));
    val items = button.getItems();

    val viewItem = new MenuItem();
    viewItem.setText("View");
    viewItem.setOnAction(event -> handleViewClick(deviceID));
    items.add(viewItem);

    val deleteItem = new MenuItem();
    deleteItem.setText("Delete");
    deleteItem.setOnAction(event -> handleDeleteClick(deviceID));
    items.add(deleteItem);

    return button;
  }

  private void updateItems(List<Device> devices) {
    deviceTable.setItems(FXCollections.observableArrayList(devices));
  }

  @Override
  public void onDatabaseEvent(DatabaseEvent event, DTO entity) {
    switch (event) {
      case PRE_REMOVE, POST_REMOVE -> {
        if (entity instanceof Device device) {
          // Hibernate results may take some time to become fully accurate, let's remove the deleted device in the meantime
          val filtered = dao
              .devices
              .getAll()
              .stream()
              .filter(d -> !d.getId().equals(device.getId()))
              .collect(Collectors.toList());

          updateItems(filtered);
        }
      }
      case POST_UPDATE -> {
        if (entity instanceof Device device) {
          val items = dao.devices.getAll();
          Integer updatedIndex = null;

          for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(((Device) entity).getId())) {
              updatedIndex = i;
            }
          }

          if (updatedIndex == null) {
            throw new IllegalStateException("Non-existent device %s updated");
          }

          items.set(updatedIndex, device);
          items.sort(Comparator.comparing(Device::getId));
          updateItems(items);
        }
      }
      case POST_PERSIST -> updateItems(dao.devices.getAll());
    }
  }

  @Override
  public void afterInitialize() {

  }
}