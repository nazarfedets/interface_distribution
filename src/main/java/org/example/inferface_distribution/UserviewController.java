package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UserviewController {
    @FXML private Button requestVocation;
    @FXML private Label errorLabel;
    @FXML private Button btnchangeDuty;
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
    @FXML
    private void openRequestWindows() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("request-vocation.fxml"));
            Parent root = loader.load();

            Stage requestWindow = new Stage();
            requestWindow.setTitle("Подати заявку");
            requestWindow.setScene(new Scene(root));
            requestWindow.initModality(Modality.WINDOW_MODAL);
            requestWindow.initOwner(requestVocation.getScene().getWindow());
            requestWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openChangeDutyWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("change-duty.fxml"));
            Parent root = loader.load();

            Stage changeWindow = new Stage();
            changeWindow.setTitle("Зміна нарядку");
            changeWindow.setScene(new Scene(root));
            changeWindow.initModality(Modality.WINDOW_MODAL);
            changeWindow.initOwner(btnchangeDuty.getScene().getWindow());
            changeWindow.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
