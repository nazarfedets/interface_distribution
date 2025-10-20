package org.example.inferface_distribution;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class FreeDaysController {
    @FXML private ComboBox<String> comboBoxCursant;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ChoiceBox<String> reasonChoiceBox;
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;
    private ObservableList<RowData> cadetdList;

    public void setCadetdList(ObservableList<RowData> cadets) {
        this.cadetdList = cadets;
        comboBoxCursant.getItems().clear();
        for (RowData row : cadets) {
            comboBoxCursant.getItems().add(row.getPib());
        }
    }

    @FXML
    private void cancelButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            Stage currentStage = (Stage) cancelBtn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        reasonChoiceBox.getItems().addAll("Індивідуальний план", "Хвороба", "Відрядження");
        reasonChoiceBox.setValue("Виберіть причину");

        reasonChoiceBox.setOnAction(event ->{
            String selectedReason = reasonChoiceBox.getValue();
        });
    }

    @FXML
    private void addFreeDays() {
        String pib = comboBoxCursant.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String reason = reasonChoiceBox.getValue();

        if (pib == null || start == null || end == null || reason == null || reason.equals("Виберіть причину")) {
            return;
        }

        String shortCode = switch (reason) {
            case "Індивідуальний план" -> "ІП";
            case "Хвороба" -> "ХВ";
            case "Відрядження" -> "В";
            default -> "";
        };

        for (RowData row : cadetdList) {
            if (row.getPib().equals(pib)) {
                LocalDate date = start;
                while (!date.isAfter(end)) {
                    int day = date.getDayOfMonth();
                    row.setValueForDay(day, shortCode);
                    date = date.plusDays(1);
                }
                break;
            }
        }

        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

}
