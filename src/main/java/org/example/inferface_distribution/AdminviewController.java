package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class AdminviewController {
    @FXML private Button btnOpenDutyTable;
    @FXML private Button btnOpenViewRequest;

    @FXML
    private void openDutyTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("duty-table.fxml"));
            Parent root = loader.load();

            Stage currentStage =  (Stage) btnOpenDutyTable.getScene().getWindow();
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
            changeWindow.initModality(Modality.WINDOW_MODAL);
            changeWindow.initOwner(btnOpenDutyTable.getScene().getWindow());
            changeWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
