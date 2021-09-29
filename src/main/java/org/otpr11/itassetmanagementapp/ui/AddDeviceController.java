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
import org.otpr11.itassetmanagementapp.constants.DeviceType;

public class AddDeviceController implements Initializable {
  ObservableList list= FXCollections.observableArrayList();
  @FXML
  private ChoiceBox<String> platform;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    loadData();
  }

  @FXML
  private void displayValue(ActionEvent event) {

  }

  private void loadData(){
    val types = Arrays.stream(DeviceType.values()).map(DeviceType::toString).collect(Collectors.toList());
    list.addAll(types);
    platform.getItems().addAll(list);
  }

}
