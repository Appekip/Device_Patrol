package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createText;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.ui.utils.CellDataFormatter;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils.TextProperties;
import org.otpr11.itassetmanagementapp.utils.StringUtils;

/** Main application view controller class. */
@Log4j2
public class MainViewController implements Initializable, ViewController, DatabaseEventListener {
  private static final int TITLE_TEXT_SIZE = 22;
  private static final int SUBTITLE_TEXT_SIZE = 16;
  private static final int BODY_TEXT_SIZE = 14;
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final List<Status> statuses = dao.statuses.getAll();
  private final BorderPane prettyDevicePane = new BorderPane();
  private final GridPane deviceInfoGrid = new GridPane();
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
  @FXML private TableColumn<Device, Device> statusColumn;
  @FXML private TableColumn<Device, String> locationColumn;
  @FXML private TableColumn<Device, String> osColumn;
  @FXML private TableColumn<Device, Device> actionColumn;
  @FXML private BorderPane viewPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DatabaseEventPropagator.addListener(this);

    initDeviceViewer();

    hwConfigurationColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));
    osColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));

    val moreInfoTooltip = new Tooltip("Double-click show information about device");

    deviceTable.setRowFactory(
        tableView -> {
          TableRow<Device> row = new TableRow<>();

          row.setOnMouseEntered(event -> {
            // Show pointer cursor and tooltip when hovering over rows that have items
            // We have to it on hover because we can't set the pointer style properly at startup,
            // and instead must do it dynamically at runtime because JavaFX
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
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  handleViewClick(row.getItem().getId());
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
    updateDeviceViewer(deviceID);

    if (viewPane.getBottom() == null) {
      viewPane.setBottom(prettyDevicePane);
    }
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
    // Don't show pointer cursor or tooltip for this element
    button.setCursor(Cursor.DEFAULT);
    button.setTooltip(null);

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

  private ChoiceBox<String> createStatusDropdown(String deviceID) {
    val dropdown = new ChoiceBox<String>();
    JFXUtils.select(dropdown, dao.devices.get(deviceID).getStatus());
    statuses.forEach(status -> dropdown.getItems().add(status.toString()));
    dropdown.setOnAction(
        event -> updateDeviceStatus(deviceID, dao.statuses.get(dropdown.getValue())));

    // Don't show pointer cursor or tooltip for this element
    dropdown.setCursor(Cursor.DEFAULT);
    dropdown.setTooltip(null);

    return dropdown;
  }

  // FIXME: Style in this file might be broken
  private void updateDeviceViewer(String deviceID) {
    val device = dao.devices.get(deviceID);

    // Reset
    deviceInfoGrid.getChildren().clear();

    // Device title
    deviceInfoGrid.add(
        createText(
            "Device %s (%s)".formatted(device.getId(), device.getNickname()),
            new TextProperties(TITLE_TEXT_SIZE, "bolder")),
        0,
        0);

    // Device description
    deviceInfoGrid.add(
        createText(
            "%s %s %s %s (%s)"
                .formatted(
                    device.getModelYear(),
                    device.getManufacturer(),
                    device.getModelName(),
                    device.getConfiguration().getDeviceType().toString().toLowerCase(),
                    device.getModelID()),
            new TextProperties(SUBTITLE_TEXT_SIZE)),
        0,
        1);

    // HW config
    val configuration = device.getConfiguration();
    val bodyProps = new TextProperties(BODY_TEXT_SIZE);
    int lastRowIndex;

    switch (configuration.getDeviceType()) {
      // Turning off dupe inspections here because it's more convenient to dupe once than to write
      // tons of code to create a generic type for both desktop and laptop and whatever other
      // future configurations
      case DESKTOP -> {
        val cfg = configuration.getDesktopConfiguration();
        //noinspection DuplicatedCode
        deviceInfoGrid.add(createText("CPU: %s".formatted(cfg.getCpu()), bodyProps), 0, 2);
        deviceInfoGrid.add(createText("GPU: %s".formatted(cfg.getGpu()), bodyProps), 0, 3);
        deviceInfoGrid.add(createText("RAM: %s".formatted(cfg.getMemory()), bodyProps), 0, 4);
        deviceInfoGrid.add(
            createText("Disk: %s".formatted(cfg.getDiskSize()), bodyProps), 0, 5);
        lastRowIndex = 5;
      }
      case LAPTOP -> {
        val cfg = configuration.getLaptopConfiguration();
        //noinspection DuplicatedCode
        deviceInfoGrid.add(createText("CPU: %s".formatted(cfg.getCpu()), bodyProps), 0, 2);
        deviceInfoGrid.add(createText("GPU: %s".formatted(cfg.getGpu()), bodyProps), 0, 3);
        deviceInfoGrid.add(createText("RAM: %s".formatted(cfg.getMemory()), bodyProps), 0, 4);
        deviceInfoGrid.add(
            createText("Disk: %s".formatted(cfg.getDiskSize()), bodyProps), 0, 5);
        deviceInfoGrid.add(
            createText("Display size: %s\"".formatted(cfg.getScreenSize()), bodyProps), 0, 6);
        lastRowIndex = 6;
      }
      default -> throw new IllegalStateException(
          "Support for device type %s not yet implemented"
              .formatted(configuration.getDeviceType()));
    }

    deviceInfoGrid.add(createText("OS: %s".formatted(StringUtils.joinPrettyStrings(device.getOperatingSystems())), bodyProps), 0, lastRowIndex + 1);
  }

  private void initDeviceViewer() {
    prettyDevicePane.setPrefHeight(200);
    prettyDevicePane.setPrefWidth(JFXUtils.getPercentageWidth(100));
    prettyDevicePane.setCenter(deviceInfoGrid);

    deviceInfoGrid.setPadding(new Insets(20, 20, 20, 20));
    deviceInfoGrid.setHgap(10);
    deviceInfoGrid.setVgap(1);
  }

  private void updateItems(List<Device> devices) {
    deviceTable.setItems(FXCollections.observableArrayList(devices));
  }

  private void updateDeviceStatus(String deviceID, Status status) {
    val device = dao.devices.get(deviceID);
    device.setStatus(status);
    dao.devices.save(device);
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
  public void afterInitialize() {}
}