package org.example.inferface_distribution;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.List;

public class EditDutyController {

    @FXML private ComboBox<String> cadetSelector;
    @FXML private ComboBox<String> placeSelector;
    @FXML private Spinner<Integer> daySpinner;
    @FXML private Button saveBtn;
    @FXML private Button deleteBtn;
    @FXML private Button swapBtn;

    private ObservableList<RowData> data;
    private RowData selectedRow;
    private int selectedDay;

    public void setData(ObservableList<RowData> data, RowData selectedRow, int selectedDay) {
        this.data = data;
        this.selectedRow = selectedRow;
        this.selectedDay = selectedDay;

        cadetSelector.setItems(FXCollections.observableArrayList(
                data.stream().map(RowData::getPib).toList()
        ));
        cadetSelector.setValue(selectedRow.getPib());

        placeSelector.setItems(FXCollections.observableArrayList("Їдальня", "Курс", "Варта"));
        placeSelector.setValue(selectedRow.getValuesForDays(selectedDay));

        int daysInMonth = YearMonth.now().lengthOfMonth();
        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, daysInMonth, selectedDay);
        daySpinner.setValueFactory(factory);
    }

    @FXML
    private void onSave() {
        String newCadet = cadetSelector.getValue();
        String newPlace = placeSelector.getValue();
        int newDay = daySpinner.getValue();

        RowData target = data.stream()
                .filter(r -> r.getPib().equals(newCadet))
                .findFirst()
                .orElse(null);

        if (target != null) {
            target.setValueForDay(newDay, newPlace);
        }
        closeWindow();
    }

    @FXML
    private void onDelete() {
        selectedRow.setValueForDay(selectedDay, "");
        closeWindow();
    }

    @FXML
    private void onSwap() {
        String cadetToSwap = cadetSelector.getValue();
        RowData other = data.stream()
                .filter(r -> r.getPib().equals(cadetToSwap))
                .findFirst()
                .orElse(null);

        if (other != null && !other.equals(selectedRow)) {
            String temp = selectedRow.getValuesForDays(selectedDay);
            selectedRow.setValueForDay(selectedDay, other.getValuesForDays(selectedDay));
            other.setValueForDay(selectedDay, temp);
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
