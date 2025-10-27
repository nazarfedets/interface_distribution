package org.example.inferface_distribution;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ViewRequestVocatuonController {
    @FXML private TableView<RequestData> tableView;
    @FXML private TableColumn<RequestData, String> pibColumn;
    @FXML private TableColumn<RequestData, String> dateColumn;
    @FXML private TableColumn<RequestData, String> typeColumn;
    @FXML private TableColumn<RequestData, String> statusColumn;
    @FXML private Button btnCancel;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;

    private ObservableList<RequestData> requests = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        pibColumn.setCellValueFactory(new PropertyValueFactory<>("pib"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Тестові заявки
        requests.addAll(
                new RequestData("Іваненко Іван", "2025-10-28", "Заміна наряду", "Очікує"),
                new RequestData("Петренко Олег", "2025-10-29", "Звільнення", "Очікує")
        );

        tableView.setItems(requests);
    }

    @FXML
    private void approveRequest() {
        RequestData selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Підтверджено");
            tableView.refresh();
        }
    }

    @FXML
    private void rejectRequest() {
        RequestData selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Відхилено");
            tableView.refresh();
        }
    }

    @FXML
    private void cancelButton() {
        Stage backWindow = (Stage) btnCancel.getScene().getWindow();
        backWindow.close();
    }
}
