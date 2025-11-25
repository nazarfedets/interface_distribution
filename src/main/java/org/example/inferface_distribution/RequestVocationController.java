package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class RequestVocationController {

    @FXML private TextField reasonField;
    @FXML private DatePicker datePicker;
    @FXML public Button btnSend;
    @FXML private ChoiceBox<String> typeVocation;
    @FXML private Button btnCancel;

    private final RequestVocationDAO requestDAO = new RequestVocationDAO();


    @FXML
    public void initialize() {
        typeVocation.getItems().addAll("Відпустка", "Добове звільнення", "Денне звільнення");
        typeVocation.setValue("Виберіть тип відпустки/звільнення");

        typeVocation.setOnAction(event ->{
            String selectedVocation = typeVocation.getValue();
        });
    }

    @FXML
    private void cancelButton(){
        Stage backWindow = (Stage) btnCancel.getScene().getWindow();
        backWindow.close();
    }



    @FXML
    private void btnSend() {

        String pib = UserSession.getCurrentUserPib();
        String reason = reasonField.getText();
        String type = typeVocation.getValue();

        if (pib == null || pib.isEmpty()) {
            showAlert("Помилка", "Не знайдено дані користувача!");
            return;
        }

        if (reason.isEmpty()) {
            showAlert("Помилка", "Введіть причину!");
            return;
        }

        if (type.equals("Виберіть тип відпустки/звільнення")) {
            showAlert("Помилка", "Оберіть тип відпустки/звільнення!");
            return;
        }

        if (datePicker.getValue() == null) {
            showAlert("Помилка", "Оберіть дату!");
            return;
        }

        String date = datePicker.getValue().toString();
        requestDAO.saveRequest(pib, reason, date, type);
        showAlert("Успішно", "Вашу заявку успішно надіслано!");
        Stage stage = (Stage) btnSend.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

}
