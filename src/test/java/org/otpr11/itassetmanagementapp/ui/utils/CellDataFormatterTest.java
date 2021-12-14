package org.otpr11.itassetmanagementapp.ui.utils;

import static org.mockito.MockitoAnnotations.openMocks;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.model.Configuration;
import org.otpr11.itassetmanagementapp.db.model.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Location;
import org.otpr11.itassetmanagementapp.db.model.OperatingSystem;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.User;
import org.otpr11.itassetmanagementapp.utils.StringUtils;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.TableViewMatchers;

@ExtendWith(ApplicationExtension.class)
class CellDataFormatterTest {
  @Mock private Status mockStatus;
  private DesktopConfiguration mockDesktop;
  private OperatingSystem mockOS;
  private User mockUser;
  private Location mockLocation;
  private Device mockDevice;
  private Device mockPartialDevice;

  @SuppressWarnings("FieldCanBeLocal")
  private Scene scene;

  @SuppressWarnings("FieldCanBeLocal")
  private BorderPane root;

  private TableView<Device> table;
  private TableColumn<Device, String> cfgColumn, osColumn, userColumn, locationColumn, typeColumn;

  @Start
  public void start(Stage stage) {
    openMocks(this);

    root = new BorderPane();
    table = new TableView<>();

    cfgColumn = new TableColumn<>();
    osColumn = new TableColumn<>();
    userColumn = new TableColumn<>();
    locationColumn = new TableColumn<>();
    typeColumn = new TableColumn<>();

    cfgColumn.setCellValueFactory(CellDataFormatter::formatHWConfig);
    osColumn.setCellValueFactory(CellDataFormatter::formatOS);
    userColumn.setCellValueFactory(CellDataFormatter::formatUser);
    locationColumn.setCellValueFactory(CellDataFormatter::formatLocation);
    typeColumn.setCellValueFactory(CellDataFormatter::formatDeviceType);

    table.getColumns().add(cfgColumn);
    table.getColumns().add(osColumn);
    table.getColumns().add(userColumn);
    table.getColumns().add(locationColumn);
    table.getColumns().add(typeColumn);

    root.setCenter(table);

    val mockCfg = new Configuration();
    mockDesktop = new DesktopConfiguration("cpu", "gpu", "memory", "disk");
    mockOS = new OperatingSystem("name", "version", "buildNumber");
    mockUser = new User("id", "firstName", "lastName", "phone", "email");
    mockLocation = new Location("id", "nickname", "address", "zip");
    mockCfg.setDesktopConfiguration(mockDesktop);
    mockDevice =
        new Device(
            "id",
            "nickname",
            "manufacturer",
            "modelID",
            "modelName",
            "modelYear",
            "macAddress",
            mockUser,
            mockCfg,
            mockStatus,
            mockLocation,
            List.of(mockOS, mockOS));

    mockPartialDevice =
        new Device(
            "id2",
            "nickname",
            "manufacturer",
            "modelID",
            "modelName",
            "modelYear",
            "macAddress",
            mockUser,
            mockCfg,
            mockStatus,
            mockLocation,
            List.of());

    mockPartialDevice.setUser(null);
    mockPartialDevice.setConfiguration(null);
    mockPartialDevice.setLocation(null);

    table.getItems().add(mockDevice);
    table.getItems().add(mockPartialDevice);

    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  @Test
  void testFormatHWConfig() {
    FxAssert.verifyThat(table, TableViewMatchers.hasTableCell(mockDesktop.toPrettyString()));
  }

  @Test
  void testFormatOS() {
    FxAssert.verifyThat(
        table,
        TableViewMatchers.hasTableCell(StringUtils.joinPrettyStrings(List.of(mockOS, mockOS))));
  }

  @Test
  void testFormatUser() {
    FxAssert.verifyThat(table, TableViewMatchers.hasTableCell(mockUser.getId()));
  }

  @Test
  void testFormatLocation() {
    FxAssert.verifyThat(table, TableViewMatchers.hasTableCell(mockLocation.getId()));
  }

  @Test
  void testFormatDeviceType() {
    FxAssert.verifyThat(table, TableViewMatchers.hasTableCell(DeviceType.DESKTOP.toString()));
  }
}
