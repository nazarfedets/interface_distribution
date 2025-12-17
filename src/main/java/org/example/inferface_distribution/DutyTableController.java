package org.example.inferface_distribution;

import javafx.application.Platform;
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
    @FXML private ComboBox<String> monthComboBox;
    @FXML private Button addFreeDays;
    @FXML private ComboBox<String> placeComboBox;
    @FXML private Button distributionBtn;
    @FXML private Button changeDutybtn;
    @FXML private Spinner<Integer> dutiesPerDaySpinner;
    private ObservableList<RowData> data;
    private List<String> kursanty;
    private List<User> selectedKuranty;
    private final Map<Integer, ObservableList<RowData>> monthToRows = new HashMap<>();

    public void setSelectedKuranty(List<User> selectKuranty) {
        this.selectedKuranty = selectKuranty;
        loadSelectedKuranty();
    }

    @FXML
    private void initialize() {
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        dutiesPerDaySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        placeComboBox.setItems(FXCollections.observableArrayList("Їдальня", "Курс", "Варта"));

        monthComboBox.getItems().addAll(
                "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень",
                "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень"
        );

        Month currentMonth = LocalDate.now().getMonth();
        monthComboBox.setValue(currentMonth.getDisplayName(TextStyle.FULL, new Locale("uk")));

        monthComboBox.setOnAction(e -> {
            int monthNumber = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            generateTableForMonth(monthNumber);
        });

        distributionBtn.setOnAction(e -> onAssignClick());

        if (selectedKuranty == null) {
            UserDAO userDAO = new UserDAO();
            selectedKuranty = userDAO.getAllUsers().stream()
                    .filter(u -> !u.getLogin().equals("admin"))
                    .toList();
        }

        Platform.runLater(() -> generateTableForMonth(currentMonth.getValue()));
    }

    private void generateTableForMonth(int month) {
        tableView.getColumns().clear();

        int year = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        double columnWidth = 37;

        TableColumn<RowData, String> pibCol = new TableColumn<>("ПІБ");
        pibCol.setCellValueFactory(new PropertyValueFactory<>("pib"));
        pibCol.setPrefWidth(150);
        tableView.getColumns().add(pibCol);

        TableColumn<RowData, String> countCol = new TableColumn<>("К-сть");
        countCol.setCellValueFactory(cellData -> {
            RowData row = cellData.getValue();
            Set<String> dutyPlaces = Set.of("Їдальня", "Курс", "Варта");

            long actualDutyCount = row.getAllValues().stream()
                    .filter(val -> val != null)
                    .map(String::trim)
                    .filter(dutyPlaces::contains)
                    .count();

            return new SimpleStringProperty(String.valueOf(actualDutyCount));
        });
        countCol.setPrefWidth(60);
        tableView.getColumns().add(countCol);

        for (int i = 1; i <= daysInMonth; i++) {
            TableColumn<RowData, String> dayCol = new TableColumn<>(String.valueOf(i));
            final int day = i;
            dayCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getValuesForDays(day)));

            dayCol.setCellFactory(tc -> new TableCell<RowData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setStyle("-fx-alignment: CENTER;");
                }
            });

            dayCol.setPrefWidth(columnWidth);
            tableView.getColumns().add(dayCol);
        }

        if (!monthToRows.containsKey(month)) {
            DutyDAO dutyDAO = new DutyDAO();
            List<Duty> dutiesFromDB = dutyDAO.getDutiesForMonth(year, month);

            List<User> sortedUsers = new ArrayList<>(selectedKuranty);
            sortedUsers.sort(Comparator.comparing(User::getPib));

            data = FXCollections.observableArrayList();
            for (User user : sortedUsers) {
                RowData rowData = new RowData(user.getPib(), daysInMonth);


                for (Duty d : dutiesFromDB) {
                    if (d.getUserLogin().equals(user.getLogin())) {
                        rowData.setValueForDay(d.getDay(), d.getPlace());
                    }
                }
                data.add(rowData);
            }
            monthToRows.put(month, FXCollections.observableArrayList(data));
        } else {
            data = FXCollections.observableArrayList(monthToRows.get(month));
        }

        tableView.setItems(data);
        tableView.setPrefWidth(250 + daysInMonth * columnWidth);
        tableView.refresh();
    }

    @FXML
    private void distributionDuty(String place, int dutiesPerDay) {
        DutyDAO dutyDAO = new DutyDAO();
        int year = LocalDate.now().getYear();
        int selectedIndex = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
        if (selectedIndex < 0) {
            showAlert("Помилка", "Будь ласка, оберіть місяць");
            return;
        }
        int month = selectedIndex;
        int daysInMonth = YearMonth.of(year, selectedIndex).lengthOfMonth();

        if (data == null || data.isEmpty()) return;

        for (RowData cadet : data) {
            for (int day = 1; day <= daysInMonth; day++) {
                String val = cadet.getValuesForDays(day);
                if (place.equals(val)) {
                    cadet.setValueForDay(day, "");
                }
            }
        }
        dutyDAO.deleteDutiesForMonthByPlace(year, selectedIndex, place);
        monthToRows.put(selectedIndex, FXCollections.observableArrayList(data));

        Map<RowData, List<Integer>> existingDutyDays = new HashMap<>();
        Map<RowData, Integer> totalDutyCount = new HashMap<>();

        for (RowData cadet : data) {
            List<Integer> busyDays = new ArrayList<>();
            int count = 0;

            for (int day = 1; day <= daysInMonth; day++) {
                String val = cadet.getValuesForDays(day);
                if (val != null && !val.trim().isEmpty()) {
                    busyDays.add(day);
                    count++;
                }
            }
            existingDutyDays.put(cadet, busyDays);
            totalDutyCount.put(cadet, count);
        }

        Random random = new Random();

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            List<RowData> availableCadets = data.stream()
                    .filter(c -> {
                        String currentVal = c.getValuesForDays(currentDay);
                        return currentVal == null || currentVal.trim().isEmpty();
                    })
                    .filter(c -> {
                        return existingDutyDays.get(c).stream()
                                .noneMatch(busyDay -> Math.abs(busyDay - currentDay) < 3);
                    })
                    .collect(Collectors.toList());

            if (availableCadets.isEmpty()) continue;

            int assignmentsNeeded = Math.min(dutiesPerDay, availableCadets.size());

            for (int i = 0; i < assignmentsNeeded; i++) {
                if (availableCadets.isEmpty()) break;

                int minCount = availableCadets.stream()
                        .mapToInt(totalDutyCount::get)
                        .min()
                        .orElse(0);

                List<RowData> candidates = availableCadets.stream()
                        .filter(c -> totalDutyCount.get(c) == minCount)
                        .toList();

                RowData chosen = candidates.get(random.nextInt(candidates.size()));

                chosen.setValueForDay(day, place);
                existingDutyDays.get(chosen).add(day);
                totalDutyCount.put(chosen, totalDutyCount.get(chosen) + 1);
                availableCadets.remove(chosen);

                String realLogin = selectedKuranty.stream()
                        .filter(u -> u.getPib().equals(chosen.getPib()))
                        .map(User::getLogin)
                        .findFirst()
                        .orElse(chosen.getPib());

                dutyDAO.addDuty(realLogin, year, selectedIndex, day, place);
            }
        }
        monthToRows.put(selectedIndex, FXCollections.observableArrayList(data));
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
        String place = placeComboBox.getValue();
        Integer dutiesPerDay = dutiesPerDaySpinner.getValue();

        if (place == null || place.isBlank()) {
            showAlert("Помилка", "Оберіть місце наряду");
            return;
        }

        if (dutiesPerDay == null || dutiesPerDay <= 0) {
            showAlert("Помилка", "Кількість нарядів має бути більшою за 0");
            return;
        }

        distributionDuty(place, dutiesPerDay);
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

        String colText = selectedCell.getTableColumn().getText();
        if(!colText.matches("\\d+")) {
            showError("Оберіть клітинку з датою!");
            return;
        }

        int day = Integer.parseInt(colText);

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
        if (selectedKuranty == null || selectedKuranty.isEmpty()) return;

        kursanty = selectedKuranty.stream()
                .map(User::getPib)
                .toList();

        Month currentMonth = LocalDate.now().getMonth();
        monthComboBox.setValue(currentMonth.getDisplayName(TextStyle.FULL, new Locale("uk")));


        monthToRows.clear();
        generateTableForMonth(currentMonth.getValue());
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}