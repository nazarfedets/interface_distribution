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
    private final Map<Integer, ObservableList<RowData>> monthToRows = new HashMap<>();


    public void setSelectedKuranty(List<User> selectKuranty) {
        this.selectedKuranty = selectKuranty;
        loadSelectedKuranty();
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> applyAutoScaling());
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
        tableView.getColumns().clear();

        int year = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        double columnWidth = 30;

        TableColumn<RowData, String> pibCol = new TableColumn<>("ПІБ");
        pibCol.setCellValueFactory(new PropertyValueFactory<>("pib"));
        pibCol.setPrefWidth(150);
        tableView.getColumns().add(pibCol);

        TableColumn<RowData, String> countCol = new TableColumn<>("К-сть нарядів");
        countCol.setCellValueFactory(cellData -> {
            RowData row = cellData.getValue();
            long count = row.getAllValues().stream()
                    .filter(val -> val != null && !val.isEmpty())
                    .count();
            return new SimpleStringProperty(String.valueOf(count));
        });
        countCol.setPrefWidth(120);
        tableView.getColumns().add(countCol);


        for (int i = 1; i <= daysInMonth; i++) {
            TableColumn<RowData, String> dayCol = new TableColumn<>(String.valueOf(i));
            final int day = i;
            dayCol.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getValuesForDays(day)));
            dayCol.setPrefWidth(columnWidth);
            tableView.getColumns().add(dayCol);
        }

        if (monthToRows.containsKey(month)) {
            data = monthToRows.get(month);
        } else {
            DutyDAO dutyDAO = new DutyDAO();
            List<Duty> dutiesFromDB = dutyDAO.getDutiesForMonth(year, month);

            UserDAO userDAO = new UserDAO();
            Map<String, String> loginToPib = userDAO.getAllUsers().stream()
                    .sorted(Comparator.comparing(User::getPib))
                    .collect(Collectors.toMap(User::getLogin, User::getPib,
                            (oldV, newV) -> oldV, LinkedHashMap::new));

            data = FXCollections.observableArrayList();

            for (String login : loginToPib.keySet()) {
                String pib = loginToPib.get(login);
                RowData rowData = new RowData(pib, daysInMonth);
                for (Duty d : dutiesFromDB) {
                    if (d.getUserLogin().equals(login)) {
                        rowData.setValueForDay(d.getDay(), d.getPlace());
                    }
                }
                data.add(rowData);
            }

            monthToRows.put(month, FXCollections.observableArrayList(data));
        }

        tableView.setItems(data);
        tableView.setPrefWidth(300 + daysInMonth * columnWidth);
    }


    @FXML
    private void distributionDuty(String place, int value) {
        DutyDAO dutyDAO = new DutyDAO();
        int year = LocalDate.now().getYear();
        int month = monthSelector.getSelectionModel().getSelectedIndex() + 1;
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        if (data == null || data.isEmpty()) return;// перевірка даних

        // очищення таблиці
        for (int day = 1; day <= daysInMonth; day++) {
            for (RowData row : data) {
                row.setValueForDay(day, "");
            }
        }

        //список минулих нарядів
        Map<RowData, List<Integer>> lastDutyDays = new HashMap<>();
        for (RowData cadet : data) {
            lastDutyDays.put(cadet, new ArrayList<>());
        }

        //кількість нарядів
        Map<RowData, Integer> dutyCount = new HashMap<>();
        for (RowData cadet : data) {
            dutyCount.put(cadet, 0);
        }

        Random random = new Random();

        for (int day = 1; day <= daysInMonth; day++) {
            final int currentDay = day;
            List<RowData> availableCadets = data.stream()//перевірка курсантів які можуть заступити
                    .filter(c -> lastDutyDays.get(c).stream().noneMatch(d -> Math.abs(d - currentDay) < 3))
                    .collect(Collectors.toList());

            int assignments = Math.min(value, availableCadets.size());
            if (assignments == 0) continue;

            for (int i = 0; i < assignments; i++) {
                //пошук мінімальну кількість нарядів серед доступних
                int minCount = availableCadets.stream()
                        .mapToInt(dutyCount::get)
                        .min()
                        .orElse(0);

                List<RowData> leastLoaded = availableCadets.stream()
                        .filter(c -> dutyCount.get(c) == minCount)
                        .collect(Collectors.toList());

                RowData chosen = leastLoaded.get(random.nextInt(leastLoaded.size()));
                availableCadets.remove(chosen);
                // Призначення наряду
                chosen.setValueForDay(day, place);
                lastDutyDays.get(chosen).add(day);
                dutyCount.put(chosen, dutyCount.get(chosen) + 1);

                dutyDAO.addDuty(chosen.getPib(), year, month, day, place);
                monthToRows.put(month, FXCollections.observableArrayList(data));

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

    private void applyAutoScaling() {
        Scene scene = tableView.getScene();
        Parent root = scene.getRoot();
        double baseWidth = 1560.0;
        double baseHeight = 800.0;
        javafx.beans.property.DoubleProperty scale = new javafx.beans.property.SimpleDoubleProperty(1);
        scale.addListener((obs, oldVal, newVal) -> {
            root.setScaleX(newVal.doubleValue());
            root.setScaleY(newVal.doubleValue());
        });
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double wScale = newVal.doubleValue() / baseWidth;
            double hScale = scene.getHeight() / baseHeight;
            scale.set(Math.min(wScale, hScale));
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            double wScale = scene.getWidth() / baseWidth;
            double hScale = newVal.doubleValue() / baseHeight;
            scale.set(Math.min(wScale, hScale));
        });
    }

}