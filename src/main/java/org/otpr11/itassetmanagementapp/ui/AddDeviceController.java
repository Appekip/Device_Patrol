package org.otpr11.itassetmanagementapp.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;
import org.otpr11.itassetmanagementapp.db.dao.GlobalDAO;
import org.otpr11.itassetmanagementapp.db.model.Device;
import org.otpr11.itassetmanagementapp.db.model.Status;
import org.otpr11.itassetmanagementapp.db.model.configuration.Configuration;

public class AddDeviceController implements Initializable {
  ObservableList list1 = FXCollections.observableArrayList();
  ObservableList list2 = FXCollections.observableArrayList();
  @FXML
  private ChoiceBox<String> platformChoicebox, statusChoicebox, user, location, configurationId;

  @FXML
  private TextField deviceidField, manufacturerField, modelidField, modelnameField, modelyearField, nicknameField, operatingsystemField,
      macaddressField, cpuField, gpuField, memoryField, disksizeField, screensizeField;

  @FXML
  private Button okButton, cancelButton;

  private Device device = new Device();
  private Configuration configuration = new Configuration();
  private Status status = new Status();

  private final GlobalDAO dao = GlobalDAO.getInstance();

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    loadPlatform();
    loadStatus();
    addDevice();
  }

  private void loadPlatform(){
    val types = Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());
    list1.addAll(types);
    platformChoicebox.getItems().addAll(list1);
  }

  private void loadStatus(){
    val types = Arrays.stream(DeviceStatus.values()).map(DeviceStatus::toString).collect(Collectors.toList());
    list2.addAll(types);
    statusChoicebox.getItems().addAll(list2);
  }

  private void clearFields(){
    platformChoicebox.setValue(null);
    deviceidField.clear();
    manufacturerField.clear();
    modelidField.clear();
    modelnameField.clear();
    statusChoicebox.setValue(null);
    modelyearField.clear();
    nicknameField.clear();
    operatingsystemField.clear();
    macaddressField.clear();
    cpuField.clear();
    gpuField.clear();
    memoryField.clear();
    disksizeField.clear();
    screensizeField.clear();
  }

  private void addDevice(){
    okButton.setOnAction(e-> {

      device.setId(deviceidField.getText());
      device.setManufacturer(manufacturerField.getText());
      device.setModelID(modelidField.getText());
      device.setModelName(modelnameField.getText());
      device.setModelYear(modelyearField.getText());
      device.setMacAddress(macaddressField.getText());
      //device.setStatus();
      device.setNickname(nicknameField.getText());

      dao.devices.create(device);

      clearFields();
    });

    cancelButton.setOnAction(e-> {
      clearFields();
    });
  }

}
