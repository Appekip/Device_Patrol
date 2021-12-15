package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.interfaces.DatabaseEventListener;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.ui.utils.CellDataFormatter;
import org.otpr11.itassetmanagementapp.utils.AlertUtils;
import org.otpr11.itassetmanagementapp.utils.JFXUtils;

/**
 * Location, user etc. management view.
 */
@Log4j2
public class ManagementViewController
    implements Initializable, ViewController, DatabaseEventListener, LocaleChangeListener {
  private final GlobalDAO dao = GlobalDAO.getInstance();
  private ResourceBundle locale = LocaleEngine.getResourceBundle();

  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  @FXML private TableView<Device> managementTable;
  @FXML private TableColumn<Device, String> configColumn, osColumn, userColumn, locationColumn;
  @FXML private TableColumn<Device, Device> configActionColumn, osActionColumn, userActionColumn, locationActionColumn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    DatabaseEventPropagator.addListener(this);
    onLocaleChange();

    configColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));
    osColumn.setMaxWidth(JFXUtils.getPercentageWidth(50));

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
                setGraphic(createConfigActionButton(cfg != null ? cfg.getId() : null));
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

                setGraphic(createOSActionButton(device.getId()));
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
                    createUserActionButton(device.getUser() != null ? device.getUser().getId() : null));
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
                    createLocationActionButton(
                        device.getLocation() != null ? device.getLocation().getId() : null));
              }
            });

    updateItems(dao.devices.getAll());
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
    if (confirmAction("hardware configuration", configID).getButtonData() == ButtonData.OK_DONE) {
      dao.configurations.delete(dao.configurations.get(configID));
    }
  }

  private void handleDeleteOS(String deviceID) {
    val toDelete = determineOS(dao.devices.get(deviceID));

    if (confirmAction("operating system", deviceID).getButtonData() == ButtonData.OK_DONE) {
      dao.operatingSystems.delete(dao.operatingSystems.get(toDelete));
    }
  }

  private void handleDeleteUser(String userID) {
    if (confirmAction("user", userID).getButtonData() == ButtonData.OK_DONE) {
      dao.users.delete(dao.users.get(userID));
    }
  }

  private void handleDeleteLocation(String locationID) {
    if (confirmAction("location", locationID).getButtonData() == ButtonData.OK_DONE) {
      dao.locations.delete(dao.locations.get(locationID));
    }
  }

  private SplitMenuButton createConfigActionButton(
      @SuppressWarnings("SameParameterValue") Long configID) {
    return createButton(
        event -> handleEditHWConfig(configID), event -> handleDeleteConfig(configID));
  }

  private SplitMenuButton createOSActionButton(String deviceID) {
    return createButton(event -> handleEditOS(deviceID), event -> handleDeleteOS(deviceID));
  }

  private SplitMenuButton createUserActionButton(String userID) {
    return createButton(event -> handleEditUser(userID), event -> handleDeleteUser(userID));
  }

  private SplitMenuButton createLocationActionButton(String locationID) {
    return createButton(
        event -> handleEditLocation(locationID), event -> handleDeleteLocation(locationID));
  }

  /**
   * Updater function for the main table.
   *
   * @param devices {@link Device} objects from database
   */
  private void updateItems(List<Device> devices) {
    devices.sort(Comparator.comparing(Device::getId));
    managementTable.setItems(FXCollections.observableArrayList(devices));
  }

  /**
   * Creates a {@link SplitMenuButton} with Edit and Delete buttons, with customisable handler functions.
   *
   * @param editHandler {@link Runnable}
   * @param deleteHandler {@link Runnable}
   * @return {@link SplitMenuButton}
   */
  private SplitMenuButton createButton(
      EventHandler<ActionEvent> editHandler, EventHandler<ActionEvent> deleteHandler) {
    val button = new SplitMenuButton();
    button.setText(locale.getString("edit"));
    button.setOnAction(editHandler);

    val items = button.getItems();

    val deleteItem = new MenuItem();
    deleteItem.setText(locale.getString("delete"));
    deleteItem.setOnAction(deleteHandler);
    items.add(deleteItem);

    return button;
  }

  /**
   * Confirms the performing of an action on an entity by showing an {@link Alert}.
   *
   * @param entity Type of entity to confirm (see locale)
   * @param id ID of target entity
   * @return Alert result
   */
  private ButtonType confirmAction(String entity, String id) {
    return AlertUtils.showAlert(
        AlertType.CONFIRMATION,
        locale.getString("are_you_sure"),
        locale
            .getString("entity_action_confirmation")
            .formatted(locale.getString("entity_target_%s".formatted(entity)), id));
  }

  /**
   * Confirms the performing of an action on an entity by showing an {@link Alert}.
   *
   * @param entity Type of entity to confirm (see locale)
   * @param id ID of target entity
   * @return Alert result
   */
  private ButtonType confirmAction(String entity, Long id) {
    return AlertUtils.showAlert(
        AlertType.CONFIRMATION,
        locale.getString("are_you_sure"),
        locale
            .getString("entity_action_confirmation")
            .formatted(locale.getString("entity_target_%s".formatted(entity)), id));
  }

  /**
   * Determines the {@link OperatingSystem} to edit (as there can be multiple per {@link Device}).
   *
   * @param device {@link Device}
   * @return ID of OS selected for editing
   */
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
              AlertType.CONFIRMATION,
              locale.getString("which_os_to_edit"),
              customButtons.toArray(new ButtonType[] {}));
      alert.setHeaderText(null);
      alert.setContentText(
          locale.getString("which_os_to_edit_detail"));
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

  @Override
  public void onLocaleChange() {
    locale = LocaleEngine.getResourceBundle();
    osColumn.setText(locale.getString("operating_system"));
    userColumn.setText(locale.getString("user"));
    locationColumn.setText(locale.getString("location"));
    configColumn.setText(locale.getString("hardware_configuration"));
    osActionColumn.setText(locale.getString("action"));
    userActionColumn.setText(locale.getString("action"));
    locationActionColumn.setText(locale.getString("action"));
    configActionColumn.setText(locale.getString("action"));
  }
}
