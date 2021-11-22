package org.otpr11.itassetmanagementapp.ui.controllers;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.val;
import org.otpr11.itassetmanagementapp.Main;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;
import org.otpr11.itassetmanagementapp.db.model.configuration.DesktopConfiguration;
import org.otpr11.itassetmanagementapp.db.model.configuration.LaptopConfiguration;
import org.otpr11.itassetmanagementapp.interfaces.ViewController;

public class HardwareConfigurationEditorController implements Initializable, ViewController {
  @Setter private Main main;
  @Setter private Stage stage;
  @Setter private Object sceneChangeData;

  private static final DeviceType DEFAULT_DEVICE_TYPE = DeviceType.LAPTOP;

  private final GlobalDAO dao = GlobalDAO.getInstance();
  private final Configuration configuration = new Configuration();
  private final DesktopConfiguration desktopConfiguration = new DesktopConfiguration();
  private final LaptopConfiguration laptopConfiguration = new LaptopConfiguration();

  private final List<String> deviceTypes =
      Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());

  @FXML
  private ChoiceBox<String> deviceTypeField;

  @FXML
  private TextField cpuField,
      gpuField,
      memoryField,
      diskSizeField,
      screenSizeField;

  @FXML
  private Text screenSizeText;

  @FXML
  private Button okButton,
      cancelButton;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    initDropdown(deviceTypeField, deviceTypes, DEFAULT_DEVICE_TYPE.toString());

    deviceTypeField.setOnAction(
        event -> {
          val type = DeviceType.valueOf(deviceTypeField.getSelectionModel().getSelectedItem());
          if (deviceTypeField.getValue() == "DESKTOP") {
            screenSizeField.setEditable(false);
            screenSizeField.setVisible(false);
            screenSizeText.setVisible(false);
          }
          else {
            screenSizeField.setEditable(true);
            screenSizeField.setVisible(true);
            screenSizeText.setVisible(true);
          }
        });

    okButton.setOnAction(this::onSave);
    cancelButton.setOnAction(this::onCancel);
  }

  private void initDropdown(ChoiceBox<String> dropdown, List<String> items, String initialValue) {
    dropdown.getItems().setAll(items);
    dropdown.setValue(initialValue);
  }

  private void onSave(ActionEvent event){

    if (deviceTypeField.getValue() == "LAPTOP"){
      saveLaptop();
    }
    else {
      saveDesktop();
    }

    dao.devices.save(configuration);
    stage.close();
  }

  private void saveLaptop(){
    laptopConfiguration.setCpu(cpuField.getText());
    laptopConfiguration.setDiskSize(diskSizeField.getText());
    laptopConfiguration.setGpu(gpuField.getText());
    laptopConfiguration.setMemory(memoryField.getText());
    laptopConfiguration.setScreenSize(Integer.parseInt(screenSizeField.getText()));

    dao.devices.save(laptopConfiguration);
    configuration.setLaptopConfiguration(laptopConfiguration);
  }

  private void saveDesktop(){
    desktopConfiguration.setCpu(cpuField.getText());
    desktopConfiguration.setDiskSize(diskSizeField.getText());
    desktopConfiguration.setGpu(gpuField.getText());
    desktopConfiguration.setMemory(memoryField.getText());

    dao.devices.save(desktopConfiguration);
    configuration.setDesktopConfiguration(desktopConfiguration);
  }

  private void onCancel(ActionEvent event) {
    stage.close();
  }

  @Override
  public void afterInitialize() {}
}
