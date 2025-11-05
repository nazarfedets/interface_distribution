package org.example.inferface_distribution;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class DutyTableController {
    @FXML private Button btnCancel;
    @FXML private TableView<RowData> tableView;
    @FXML private ComboBox<String> monthSelector;
    private TableColumn<RowData, String> pibCol;
    private ObservableList<RowData> data;
    private final Map<String, ObservableList<RowData>> dutiesByMonth = new HashMap<>();
    @FXML private Button addFreeDays;
    @FXML private ComboBox<String> placeDuty;
    @FXML private Spinner<Integer> dutyCount;
    @FXML private Button distributionBtn;
    @FXML private Button changeDutybtn;
    private List<String> kursanty;
    private List<User> selectedKuranty;

    public void setSelectedKuranty(List<User> selectKuranty) {
        this.selectedKuranty = selectKuranty;
        loadSelectedKuranty();
    }

    @FXML
    private void initialize() {
        if (selectedKuranty == null) {
            UserDAO userDAO = new UserDAO();
            selectedKuranty = userDAO.getAllUsers().stream()
                    .filter(u -> !u.getLogin().equals("admin"))
                    .toList();
        }

        kursanty = selectedKuranty.stream()
                .map(User::getPib)
                .toList();

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

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        dutyCount.setValueFactory(valueFactory);

        placeDuty.setItems(FXCollections.observableArrayList("Їдільння","Курс", "Варта"));

        distributionBtn.setOnAction(e -> onAssignClick());
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
        DutyDAO dutyDAO = new DutyDAO();
        List<Duty> dutiesFromDB = dutyDAO.getDutiesForMonth(year, month);
        UserDAO userDAO = new UserDAO();
        Map<String, String> pibToLogin = new HashMap<>();
        for (User u : userDAO.getAllUsers()) {
            pibToLogin.put(u.getPib(), u.getLogin());
        }

        data = FXCollections.observableArrayList();
        for (String pib : kursanty) {
            RowData rowData = new RowData(pib, daysInMonth);

            for (Duty d : dutiesFromDB) {
                if (d.getUserLogin().equals(pibToLogin.get(pib))) {
                    rowData.setValueForDay(d.getDay(), d.getPlace());
                }
            }

            data.add(rowData);
        }

        dutiesByMonth.put(monthKey, data);
        tableView.setItems(data);
        tableView.setPrefWidth(300 + daysInMonth * columnWidth);
    }


    @FXML
    private void distributionDuty(String place, int value) {
        if (data == null || data.isEmpty()) return;

        int year = LocalDate.now().getYear();
        int month = monthSelector.getSelectionModel().getSelectedIndex() + 1;
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            for (RowData row : data) {
                row.setValueForDay(day, "");
            }
        }

        Map<RowData, List<Integer>> lastDutyDays = new HashMap<>();
        for (RowData cadet : data) {
            lastDutyDays.put(cadet, new ArrayList<>());
        }

        Random random = new Random();

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            List<RowData> availableCadets = data.stream()
                    .filter(c -> lastDutyDays.get(c).stream().noneMatch(d -> Math.abs(d - currentDay) < 3))
                    .collect(Collectors.toList());

            int assignments = Math.min(value, availableCadets.size());

            for (int i = 0; i < assignments; i++) {
                int index = random.nextInt(availableCadets.size());
                RowData chosen = availableCadets.remove(index);

                chosen.setValueForDay(day, place);
                lastDutyDays.get(chosen).add(day);
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
    private void onAssignClick() {
        String place = placeDuty.getValue();
        Integer count = dutyCount.getValue();

        if (place == null) {
            showError("Оберіть місце наряду!");
            return;
        }
        int dailyCount = dutyCount.getValue();
        distributionDuty(place, count);
    }

    private void showError(String mwssage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mwssage);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @FXML
    private void onEditDuty() {
        RowData selectedRow = tableView.getSelectionModel().getSelectedItem();
        TablePosition<?, ?> selectedCell = tableView.getSelectionModel().getSelectedCells().isEmpty()
                ? null
                : tableView.getSelectionModel().getSelectedCells().get(0);

        if (selectedRow == null || selectedCell == null) {
            showError("Оберіть курсанта та день для редагування!");
            return;
        }

        int day = Integer.parseInt(selectedCell.getTableColumn().getText());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-duty.fxml"));
            Parent root = loader.load();

            EditDutyController controller = loader.getController();
            controller.setData(data, selectedRow, day);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            stage.showAndWait();

            tableView.refresh();

        } catch (IOException e) {
            e.printStackTrace();
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

            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSelectedKuranty() {
        if (selectedKuranty == null || selectedKuranty.isEmpty()) {
            return;
        }

        kursanty = selectedKuranty.stream()
                .map(User::getPib)
                .toList();

        Month currentMonth = LocalDate.now().getMonth();
        monthSelector.setValue(currentMonth.getDisplayName(TextStyle.FULL, new Locale("uk")));
        generateTableForMonth(currentMonth.getValue());
    }


}

