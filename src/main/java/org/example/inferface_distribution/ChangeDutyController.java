package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class ChangeDutyController {
    @FXML private Button btnCancel;
    @FXML private Button btnSend;
    @FXML private TextField textFieldchange;
    @FXML private ChoiceBox<String> choiceBoxDuty;

    private final DutyDAO dutyDAO = new DutyDAO();
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

        if(selectedDutyDisplay != null && !reason.isEmpty()){
            int dutyId = Integer.parseInt(selectedDutyDisplay.split("\\|")[0].trim());

            Duty selectedDuty = dutyList.stream()
                    .filter(d -> d.getId() == dutyId)
                    .findFirst()
                    .orElse(null);

            if (selectedDuty == null) {
                showError("Помилка: Не вдалося знайти обраний наряд у системі.");
                return;
            }

            ChangeDutyDAO changeDAO = new ChangeDutyDAO();
            changeDAO.saveChangeRequest(
                    UserSession.getCurrentUserLogin(),
                    dutyId,
                    reason
            );

            showSuccess("Вашу заявку на зміну наряду успішно створено!");
            Stage stage = (Stage) btnSend.getScene().getWindow();
            stage.close();
        } else {
            showError("Виберіть наряд та обов'язково вкажіть причину.");
        }
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
