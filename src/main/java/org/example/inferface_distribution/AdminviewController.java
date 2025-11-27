package org.example.inferface_distribution;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class AdminviewController {
    @FXML private Button btnOpenDutyTable;
    @FXML private Button btnOpenViewRequest;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> pibColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> groupColumn;
    @FXML private Button btnCZ11;
    @FXML private Button btnCZ12;
    @FXML private Button btnCZ21;
    @FXML private Button btnCZ31;
    private ObservableList<User> allUsers;
    @FXML private AnchorPane rootPane;
    @FXML private TableColumn<User,String> totalDutyColumn;
    private final UserDAO userDAO = new UserDAO();
    private final DutyDAO dutyDAO = new DutyDAO();


    @FXML
    public void initialize() {
        pibColumn.setCellValueFactory(new PropertyValueFactory<>("pib"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        UserDAO userDAO = new UserDAO();
        allUsers = FXCollections.observableArrayList(userDAO.getAllUsers());
        userTable.setItems(allUsers);
        btnCZ11.setOnAction(e -> filterByGroup("ЦЗ-11"));
        btnCZ12.setOnAction(e -> filterByGroup("ЦЗ-12"));
        btnCZ21.setOnAction(e -> filterByGroup("ЦЗ-21"));
        btnCZ31.setOnAction(e -> filterByGroup("ЦЗ-31"));

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> scaleUI());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> scaleUI());

        totalDutyColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            DutyDAO dutyDAO = new DutyDAO();
            long totalCount = dutyDAO.getAllDutiesForUser(user.getPib()).size();
            return new SimpleStringProperty(String.valueOf(totalCount));
        });

    }

    private void filterByGroup(String groupName) {
        ObservableList<User> filtered = FXCollections.observableArrayList();
        for (User u : allUsers) {
            if (u.getGroupName().equals(groupName)) {
                filtered.add(u);
            }
        }
        userTable.setItems(filtered);
    }

    @FXML
    private void openDutyTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("duty-table.fxml"));
            Parent root = loader.load();

            DutyTableController dutyController = loader.getController();
            dutyController.setSelectedKuranty(new ArrayList<>(userTable.getItems()));

            Stage currentStage = (Stage) btnOpenDutyTable.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Таблиця нарядів");
            currentStage.setMaximized(true);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openViewRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view-request-vocation.fxml"));
            Parent root = loader.load();

            Stage changeWindow = new Stage();
            changeWindow.setTitle("Заявки на звільнення/відпустки");
            changeWindow.setScene(new Scene(root));
            changeWindow.setMaximized(true);
            changeWindow.initModality(Modality.WINDOW_MODAL);
            changeWindow.initOwner(btnOpenDutyTable.getScene().getWindow());
            changeWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void scaleUI() {
        double baseWidth = 1440.0;
        double baseHeight = 800.0;

        double scaleX = rootPane.getWidth() / baseWidth;
        double scaleY = rootPane.getHeight() / baseHeight;
        double scale = Math.min(scaleX, scaleY);

        rootPane.setScaleX(scale);
        rootPane.setScaleY(scale);
    }
}
