package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ChangeDutyController {
    @FXML private Button btnCancel;
    @FXML private Button btnSend;
    @FXML private TextField textFieldchange;
    @FXML private ChoiceBox<String> choiceBoxDuty;

    private final DutyDAO dutyDAO = new DutyDAO();
    private final UserDAO userDAO = new UserDAO();
    private List<Duty> dutyList;

    @FXML
    public void initialize() {
        String currentUser = UserSession.getCurrentUserLogin();
        this.dutyList = dutyDAO.getAllDutiesForUser(currentUser);

        for (Duty d : dutyList) {
            choiceBoxDuty.getItems().add(
                    d.getId() + " | " + d.getYear() + "-" + d.getMonth() + "-" + d.getDay() + " | " + d.getPlace()
            );
        }
    }

    @FXML
    private void cancelButton(){
        Stage backWindow = (Stage) btnCancel.getScene().getWindow();
        backWindow.close();
    }


    @FXML
    private void sendChangeRequest() {
        String selectedDutyDisplay = choiceBoxDuty.getValue();
        String reason = textFieldchange.getText();
        String userLogin = UserSession.getCurrentUserLogin();

        if (selectedDutyDisplay == null || reason == null || reason.isEmpty() || userLogin == null || userLogin.isEmpty()) {
            showError("Виберіть наряд та обов'язково вкажіть причину.");
            return;
        }

        String userPib;
        try {
            User user = userDAO.getUserByLogin(userLogin);
            if (user == null) {
                showError("Помилка: Не вдалося знайти дані користувача.");
                return;
            }
            userPib = user.getPib();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Помилка при отриманні даних користувача.");
            return;
        }

        int dutyId;
        try {
            dutyId = Integer.parseInt(selectedDutyDisplay.split("\\|")[0].trim());
        } catch (NumberFormatException e) {
            showError("Помилка: невірний формат обраного наряду.");
            return;
        }

        Duty selectedDuty = dutyList.stream()
                .filter(d -> d.getId() == dutyId)
                .findFirst()
                .orElse(null);

        if (selectedDuty == null) {
            showError("Помилка: Не вдалося знайти обраний наряд у системі.");
            return;
        }

        java.sql.Date dutyDate;
        try {
            dutyDate = java.sql.Date.valueOf(java.time.LocalDate.of(
                    selectedDuty.getYear(),
                    selectedDuty.getMonth(),
                    selectedDuty.getDay()
            ));
        } catch (Exception e) {
            showError("Помилка при створенні дати: " + e.getMessage());
            return;
        }

        String requestType = "Зміна наряду";

        try (Connection conn = DatabaseConnection.getConnection()) {
            ChangeDutyDAO changeDutyDAO = new ChangeDutyDAO(conn);
            changeDutyDAO.saveChangeRequest(userPib, reason, dutyDate, requestType);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Помилка при збереженні заявки: " + e.getMessage());
            return;
        }

        showSuccess("Вашу заявку на зміну наряду успішно створено!");
        Stage stage = (Stage) btnSend.getScene().getWindow();
        stage.close();
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.setTitle("Помилка");
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setTitle("Успіх");
        alert.showAndWait();
    }

}
