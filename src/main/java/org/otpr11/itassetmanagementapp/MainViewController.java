package org.otpr11.itassetmanagementapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainViewController implements Initializable {



    @FXML
    private TableView<data>  TV;
    @FXML
    private TableColumn<data, Integer> idColumn;
    @FXML
    private TableColumn<data, String> ownerColumn;
    @FXML
    private TableColumn<data, String> deviceColumn;
    @FXML
    private TableColumn<data, String> locationColumn;
    @FXML
    private TextField searchField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("device"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        ObservableList<data> observableList= (ObservableList<data>) FXCollections.observableArrayList(
                new data(1, "Simo", "Myllypuro", "Laptop"),
                new data(2, "Auvo", "Myllypuro", "Laptop"),
                new data(3, "Juha", "Myllypuro", "Laptop"),
                new data(4, "Vesa", "Myllypuro", "Laptop"),
                new data(5, "Heikki", "Karamalmi", "Phone")
        );
        TV.setItems(observableList);

        FilteredList<data> filteredData = new FilteredList<>(observableList, b -> true);
            searchField.textProperty().addListener((observable, newValue, oldValue) ->{
                filteredData.setPredicate(data -> {
                    if (newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (data.getId().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if (data.getDevice().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if (data.getLocation().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else if (data.getUser().toLowerCase().indexOf(lowerCaseFilter) != -1){
                        return true;
                    }
                    else
                    return false;
                });
            });
        SortedList<data> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(TV.comparatorProperty());

        TV.setItems(sortedData);
    }






    @FXML
    protected void editClick() {
        System.out.println("Opens the edit menu for this entry");
        System.out.println(TV.getSelectionModel().getSelectedItem());

    }

    @FXML
    protected void aboutClick(){
        System.out.println("Opens a instruction pdf");
    }
    @FXML
    protected void newClick(){
        System.out.println("Opens the menu to add a new entry");
    }




}