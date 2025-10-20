package org.example.inferface_distribution;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DutyTableController {
    @FXML private Button btnCancel;
    @FXML private TableView<RowData> tableView;
    @FXML private ComboBox<String> monthSelector;
    private TableColumn<RowData, String> pibCol;
    private ObservableList<RowData> data;
    private final Map<String, ObservableList<RowData>> dutiesByMonth = new HashMap<>();
    @FXML private Button addFreeDays;

    private final List<String> kursanty = List.of(
            "Петров Іван Сергійович",
            "Ворон Сергій Петрович",
            "Коваленко Вікторія Остапівна",
            "Лисенко Богдан Олексійович",
            "Савченко Анатолій Іванович",
            "Бондар Ірина Ігорівна",
            "Петров Семен Віталійович"
    );

    @FXML
    private void initialize() {
        monthSelector.getItems().addAll(
                "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень",
                "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень"
        );

        Month currentMonth = LocalDate.now().getMonth();
        monthSelector.setValue(currentMonth.getDisplayName(TextStyle.FULL, new Locale("uk")));

        pibCol = new TableColumn<>("ПІБ");
        pibCol.setCellValueFactory(new PropertyValueFactory<>("pib"));
        pibCol.setPrefWidth(150);
        tableView.getColumns().add(pibCol);


        generateTableForMonth(currentMonth.getValue());

        monthSelector.setOnAction(e -> {
            int monthNumber = monthSelector.getSelectionModel().getSelectedIndex() + 1;
            generateTableForMonth(monthNumber);
        });

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    private void generateTableForMonth(int month) {
        tableView.getColumns().removeIf(col ->
                !col.getText().equals("ПІБ"));

        int year = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        double columnWidth = 30;

        for (int i = 1; i <= daysInMonth; i++) {
            TableColumn<RowData, String> dayCol = new TableColumn<>(String.valueOf(i));
            final int day = i;
            dayCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getValuesForDays(day)));
            dayCol.setPrefWidth(columnWidth);
            tableView.getColumns().add(dayCol);
        }

        String monthKey = year + "-" + month;
        if (dutiesByMonth.containsKey(monthKey)) {
            data = dutiesByMonth.get(monthKey);
        } else {
            data = FXCollections.observableArrayList();
            for (String pib : kursanty) {
                data.add(new RowData(pib, daysInMonth));
            }
            dutiesByMonth.put(monthKey, data);
        }
        tableView.setItems(data);
        tableView.setPrefWidth(300 + daysInMonth * columnWidth);
    }

    @FXML
    private void cancelButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ditributionDuty() {
        if (data == null || data.isEmpty()) return;

        int year = LocalDate.now().getYear();
        int month = monthSelector.getSelectionModel().getSelectedIndex() + 1;
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            for (RowData row : data) {
                String current = row.getValuesForDays(day);
                if (current == null || current.isEmpty()) {
                    row.setValueForDay(day, "");
                }
            }
        }

        for (int day = 1; day <= daysInMonth; day++) {
            for (RowData row : data) {
                String current = row.getValuesForDays(day);
                if (current == null || current.isEmpty()) {
                    if (Math.random() < 0.3) {
                        row.setValueForDay(day, "н");
                    }
                }
            }
        }
        tableView.refresh();
    }

    @FXML
    private void openFreeDaysWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("free-days.fxml"));
            Parent root = loader.load();
            FreeDaysController controller = loader.getController();
            controller.setCadetdList(data);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addFreeDays.getScene().getWindow());
            stage.showAndWait();
            tableView.refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openViewRequestWindow() {

    }
}

