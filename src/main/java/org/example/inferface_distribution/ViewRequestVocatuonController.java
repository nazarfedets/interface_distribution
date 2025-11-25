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
    @FXML private TableColumn<RequestData, String> idColumn;
    @FXML private TableColumn<RequestData, String> pibColumn;
    @FXML private TableColumn<RequestData, String> reasonColumn;
    @FXML private TableColumn<RequestData, String> dateColumn;
    @FXML private TableColumn<RequestData, String> typeColumn;
    @FXML private TableColumn<RequestData, String> statusColumn;
    @FXML private Button btnCancel;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;
    private final ChangeDutyRequestDAO changeDutyRequestDAO = new ChangeDutyRequestDAO();


    private ObservableList<RequestData> requests = FXCollections.observableArrayList();
    private final RequestVocationDAO requestDAO = new RequestVocationDAO();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pibColumn.setCellValueFactory(new PropertyValueFactory<>("pib"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        requests.addAll(requestDAO.getAllRequests());
        requests.addAll(changeDutyRequestDAO.getAllChangeDutyRequests());
        tableView.setItems(requests);
    }

    @FXML
    private void approveRequest() {
        RequestData selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestDAO.updateStatus(selected.getId(), "Підтверджено");
            selected.setStatus("Підтверджено");
            tableView.refresh();
        }
    }

    @FXML
    private void rejectRequest() {
        RequestData selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            requestDAO.updateStatus(selected.getId(), "Відхилено");
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
