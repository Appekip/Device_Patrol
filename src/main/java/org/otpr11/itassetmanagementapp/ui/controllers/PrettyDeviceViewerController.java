package org.otpr11.itassetmanagementapp.ui.controllers;

import static org.otpr11.itassetmanagementapp.utils.JFXUtils.createText;

import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.interfaces.LocaleChangeListener;
import org.otpr11.itassetmanagementapp.locale.LocaleEngine;
import org.otpr11.itassetmanagementapp.utils.JFXUtils.TextProperties;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PrettyDeviceViewerController implements LocaleChangeListener {
  private static final int TITLE_TEXT_SIZE = 22;
  private static final int SUBTITLE_TEXT_SIZE = 16;
  private static final int BODY_TEXT_SIZE = 14;
  private static final TextProperties TITLE_STYLE = new TextProperties(TITLE_TEXT_SIZE, "bolder");
  private static final TextProperties SUBTITLE_STYLE = new TextProperties(SUBTITLE_TEXT_SIZE);
  private static final TextProperties BODY_STYLE = new TextProperties(BODY_TEXT_SIZE);
  private static final ResourceBundle locale = LocaleEngine.getResourceBundle();
  private static final GlobalDAO dao = GlobalDAO.getInstance();
  private static final GridPane deviceInfoGrid = new GridPane();
  @Getter private static boolean isOpen = false;
  @Getter @Setter private static String currentDeviceID = null;
  private static BorderPane mainViewPane = null;
  private static BorderPane prettyDevicePane = null;
  private static int lastRowIndex;

  /**
   * Borders and grid
   */
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

  /**
   * Showing device
   */
  public static void show(String deviceID) {
    isOpen = true;
    update(deviceID);

    if (mainViewPane.getLeft() == null) {
      mainViewPane.setLeft(prettyDevicePane);
    }
  }

  /**
   * Hiding device
   */
  public static void hide() {
    isOpen = false;
    mainViewPane.setLeft(null);
  }

  /**
   * Updating device
   */
  private static void update(String deviceID) {
    val device = dao.devices.get(deviceID);

    // Reset
    deviceInfoGrid.getChildren().clear();
    lastRowIndex = 0;

    // Device title
    addRow(
        createText("%s %s (%s)".formatted(locale.getString("device"), device.getId(), device.getNickname()), TITLE_STYLE));

    // Device description
    val configuration = device.getConfiguration();

    addRow(
        createText(
            "%s %s %s %s (%s)"
                .formatted(
                    device.getModelYear(),
                    device.getManufacturer(),
                    device.getModelName(),
                    configuration != null ? configuration.getDeviceType().toString().toLowerCase() : locale.getString("unknown_device_type"),
                    device.getModelID()),
            SUBTITLE_STYLE));

    // Status
    addRow(
        createText(
            "Status: %s"
                .formatted(DeviceStatus.fromString(device.getStatus().toString()).getPrettyName()),
            SUBTITLE_STYLE));

    addSpacer();

    // HW config
    addRow(createText("%s:".formatted(locale.getString("hardware_details")), SUBTITLE_STYLE));
    addRow(createText("%s: %s".formatted(locale.getString("mac_address"), device.getMacAddress()), BODY_STYLE));

    if (configuration != null) {
      switch (configuration.getDeviceType()) {
        // Turning off dupe inspections here because it's more convenient to dupe once than to write
        // tons of code to create a generic type for both desktop and laptop and whatever other
        // future configurations
        case DESKTOP -> {
          val cfg = configuration.getDesktopConfiguration();
          //noinspection DuplicatedCode
          addRow(createText("%s: %s".formatted(locale.getString("cpu"), cfg.getCpu()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("gpu"), cfg.getGpu()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("ram"), cfg.getMemory()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("disk"), cfg.getDiskSize()), BODY_STYLE));
        }
        case LAPTOP -> {
          val cfg = configuration.getLaptopConfiguration();
          //noinspection DuplicatedCode
          addRow(createText("%s: %s".formatted(locale.getString("cpu"), cfg.getCpu()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("gpu"), cfg.getGpu()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("ram"), cfg.getMemory()), BODY_STYLE));
          addRow(createText("%s: %s".formatted(locale.getString("disk"), cfg.getDiskSize()), BODY_STYLE));
          addRow(createText("%s: %s\"".formatted(locale.getString("display_size"), cfg.getScreenSize()), BODY_STYLE));
        }
        default -> throw new IllegalStateException(
            "Support for device type %s not yet implemented"
                .formatted(configuration.getDeviceType()));
      }
    }

    String osString;

    if (device.getOperatingSystems().size() > 0) {
      val str = new StringBuilder();

      device
          .getOperatingSystems()
          .forEach(os -> str.append("\n• %s".formatted(os.toPrettyString())));

      osString = str.toString();
    } else {
      osString = "N/A";
    }

    addRow(createText("%s: %s".formatted(locale.getString("operating_system"), osString), BODY_STYLE));
    addSpacer();

    // User info
    val user = device.getUser();

    if (device.getUser() != null) {
      addRow(
          createText(
              "%s: %s %s (%s)"
                  .formatted(locale.getString("in_use_by"), user.getFirstName(), user.getLastName(), user.getId()),
              SUBTITLE_STYLE));
      // TODO: Click to copy on these?
      addRow(createText("%s: %s".formatted(locale.getString("email"), user.getEmail()), BODY_STYLE));
      addRow(createText("%s: %s".formatted(locale.getString("phone"), user.getPhone()), BODY_STYLE));
    } else {
      addRow(createText("%s: N/A".formatted(locale.getString("in_use_by")), BODY_STYLE));
    }

    val location = device.getLocation();
    val locString = location != null ? "%s (%s)".formatted(location.getId(), location.getNickname()) : "N/A";

    addRow(createText("%s: %s".formatted(locale.getString("located_at"), locString), BODY_STYLE));

    if (location != null) {
      // TODO: Click to copy on this?
      addRow(
          createText(
              "%s: %s %s".formatted(locale.getString("address"), location.getAddress(), location.getZipCode()), BODY_STYLE));
    }

    prettyDevicePane.setCenter(deviceInfoGrid);
  }

  /**
   * Adding rows and spaces
   */
  private static void addRow(HBox row) {
    lastRowIndex++;
    deviceInfoGrid.add(row, 0, lastRowIndex);
  }

  private static void addSpacer() {
    addRow(createText("", BODY_STYLE));
  }
}
