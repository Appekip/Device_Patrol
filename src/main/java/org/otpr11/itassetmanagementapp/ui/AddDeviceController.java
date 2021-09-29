package org.otpr11.itassetmanagementapp.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import lombok.val;
import org.otpr11.itassetmanagementapp.constants.DeviceStatus;
import org.otpr11.itassetmanagementapp.constants.DeviceType;

public class AddDeviceController implements Initializable {
  ObservableList list1 = FXCollections.observableArrayList();
  ObservableList list2 = FXCollections.observableArrayList();
  @FXML
  private ChoiceBox<String> platform;

  @FXML
  private ChoiceBox<String> status;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    loadData();
    loadStatus();
  }

  private void loadData(){
    val types = Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());
    list1.addAll(types);
    platform.getItems().addAll(list1);
  }

  private void loadStatus(){
    val types = Arrays.stream(DeviceStatus.values()).map(DeviceStatus::toString).collect(Collectors.toList());
    list2.addAll(types);
    status.getItems().addAll(list2);
  }

}
