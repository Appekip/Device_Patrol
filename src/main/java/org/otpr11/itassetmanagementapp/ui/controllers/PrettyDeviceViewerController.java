package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createText;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.utils.JFXUtils.TextProperties;

public abstract class PrettyDeviceViewerController {
  private static final int TITLE_TEXT_SIZE = 22;
  private static final int SUBTITLE_TEXT_SIZE = 16;
  private static final int BODY_TEXT_SIZE = 14;
  private static final TextProperties TITLE_STYLE = new TextProperties(TITLE_TEXT_SIZE, "bolder");
  private static final TextProperties SUBTITLE_STYLE = new TextProperties(SUBTITLE_TEXT_SIZE);
  private static final TextProperties BODY_STYLE = new TextProperties(BODY_TEXT_SIZE);
  private static final GlobalDAO dao = GlobalDAO.getInstance();
  private static final GridPane deviceInfoGrid = new GridPane();
  @Getter private static boolean isOpen = false;
  @Getter @Setter
  private static String currentDeviceID = null;
  private static BorderPane mainViewPane = null;
  private static BorderPane prettyDevicePane = null;
  private static int lastRowIndex;

  public static void init(BorderPane _mainViewPane, BorderPane _prettyDevicePane) {
    mainViewPane = _mainViewPane;
    prettyDevicePane = _prettyDevicePane;

    prettyDevicePane.setMaxWidth(100);
    prettyDevicePane.setCenter(deviceInfoGrid);

    deviceInfoGrid.setPadding(new Insets(10, 10, 10, 10));
    deviceInfoGrid.setHgap(10);
    deviceInfoGrid.setVgap(1);

    val anchorPane = new AnchorPane();

    val closeButton = new Button("\uD83D\uDFA9");
    closeButton.setAlignment(Pos.TOP_RIGHT);
    closeButton.setOnAction(event -> hide());

    AnchorPane.setTopAnchor(closeButton, 5d);
    AnchorPane.setRightAnchor(closeButton, 5d);
    anchorPane.getChildren().add(closeButton);

    prettyDevicePane.setRight(anchorPane);
  }

  public static void show(String deviceID) {
    isOpen = true;
    update(deviceID);

    if (mainViewPane.getLeft() == null) {
      mainViewPane.setLeft(prettyDevicePane);
    }
  }

  public static void hide() {
    isOpen = false;
    mainViewPane.setLeft(null);
  }

  private static void update(String deviceID) {
    val device = dao.devices.get(deviceID);

    // Reset
    deviceInfoGrid.getChildren().clear();
    lastRowIndex = 0;

    // Device title
    addRow(createText(
        "Device %s (%s)".formatted(device.getId(), device.getNickname()),
        TITLE_STYLE));

    // Device description
    addRow(createText(
        "%s %s %s %s (%s)"
            .formatted(
                device.getModelYear(),
                device.getManufacturer(),
                device.getModelName(),
                device.getConfiguration().getDeviceType().toString().toLowerCase(),
                device.getModelID()),
        SUBTITLE_STYLE));

    // Status
    addRow(createText("Status: %s".formatted(
        DeviceStatus.fromString(device.getStatus().toString()).getPrettyName()), SUBTITLE_STYLE));

    addSpacer();

    // HW config
    val configuration = device.getConfiguration();

    addRow(createText("Hardware details:", SUBTITLE_STYLE));
    addRow(createText("MAC address: %s".formatted(device.getMacAddress()), BODY_STYLE));

    switch (configuration.getDeviceType()) {
      // Turning off dupe inspections here because it's more convenient to dupe once than to write
      // tons of code to create a generic type for both desktop and laptop and whatever other
      // future configurations
      case DESKTOP -> {
        val cfg = configuration.getDesktopConfiguration();
        //noinspection DuplicatedCode
        addRow(createText("CPU: %s".formatted(cfg.getCpu()), BODY_STYLE));
        addRow(createText("GPU: %s".formatted(cfg.getGpu()), BODY_STYLE));
        addRow(createText("RAM: %s".formatted(cfg.getMemory()), BODY_STYLE));
        addRow(createText("Disk: %s".formatted(cfg.getDiskSize()), BODY_STYLE));
      }
      case LAPTOP -> {
        val cfg = configuration.getLaptopConfiguration();
        //noinspection DuplicatedCode
        addRow(createText("CPU: %s".formatted(cfg.getCpu()), BODY_STYLE));
        addRow(createText("GPU: %s".formatted(cfg.getGpu()), BODY_STYLE));
        addRow(createText("RAM: %s".formatted(cfg.getMemory()), BODY_STYLE));
        addRow(createText("Disk: %s".formatted(cfg.getDiskSize()), BODY_STYLE));
        addRow(createText("Display size: %s\"".formatted(cfg.getScreenSize()), BODY_STYLE));
      }
      default -> throw new IllegalStateException(
          "Support for device type %s not yet implemented"
              .formatted(configuration.getDeviceType()));
    }

    val osString = new StringBuilder();
    osString.append("Operating systems:");
    device.getOperatingSystems().forEach(os -> osString.append("\n• %s".formatted(os.toPrettyString())));
    addRow(createText(osString.toString(), BODY_STYLE));
    addSpacer();

    // User info
    val user = device.getUser();

    if (device.getUser() != null) {
      addRow(createText("In use by: %s %s (%s)".formatted(user.getFirstName(), user.getLastName(), user.getId()), SUBTITLE_STYLE));
      // TODO: Click to copy on these?
      addRow(createText("Email: %s".formatted(user.getEmail()), BODY_STYLE));
      addRow(createText("Phone: %s".formatted(user.getPhone()), BODY_STYLE));
    } else {
      addRow(createText("In use by: (No user at present)", BODY_STYLE));
    }

    val location = device.getLocation();
    addRow(createText("Located at: %s (%s)".formatted(location.getId(), location.getNickname()), BODY_STYLE));
    // TODO: Click to copy on this?
    addRow(createText("Address: %s %s".formatted(location.getAddress(), location.getZipCode()), BODY_STYLE));

    prettyDevicePane.setCenter(deviceInfoGrid);
  }

  private static void addRow(HBox row) {
    lastRowIndex++;
    deviceInfoGrid.add(row, 0, lastRowIndex);
  }

  private static void addSpacer() {
    addRow(createText("", BODY_STYLE));
  }
}
