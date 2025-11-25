package org.example.inferface_distribution;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.plaf.basic.BasicButtonUI;
import java.time.Month;
import java.util.List;

public class ViewDutyController {

    @FXML private TableView<Duty> dutyTable;
    @FXML private TableColumn<Duty, String> monthColumn;
    @FXML private TableColumn<Duty, Integer> dayColumn;
    @FXML private TableColumn<Duty, String> placeColumn;
    @FXML private Button btnBack;

    private String userLogin;

    @FXML
    public void initialize() {
        monthColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        Month.of(cellData.getValue().getMonth()).name()
                ));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
    }

    public void setUserLogin(String login) {
        this.userLogin = login;
        loadDuties();
    }




    @FXML
    private void handleBack() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }


    public void setUser(User user) {
    }

    private void loadDuties() {
        DutyDAO dutyDAO = new DutyDAO();
        ObservableList<Duty> duties = FXCollections.observableArrayList(dutyDAO.getAllDutiesForUser(userLogin));
        dutyTable.setItems(duties);
    }

}
