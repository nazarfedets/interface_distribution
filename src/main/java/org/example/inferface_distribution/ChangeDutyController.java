package org.example.inferface_distribution;

import javafx.fxml.FXML;
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

    @FXML
    public void initialize() {
        String currentUser = UserSession.getCurrentUserLogin();
        List<Duty> duties = dutyDAO.getAllDutiesForUser(currentUser);
        System.out.println("Поточний користувач: " + currentUser);
        System.out.println("Скільки нарядів знайдено: " + duties.size());

        for (Duty d : duties) {
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
        String selectedDuty = choiceBoxDuty.getValue();
        String reason = textFieldchange.getText();

        if(selectedDuty != null && !reason.isEmpty()){
            int dutyId = Integer.parseInt(selectedDuty.split("\\|")[0].trim());
            ChangeDutyDAO changeDAO = new ChangeDutyDAO();
            changeDAO.saveChangeRequest(UserSession.getCurrentUserLogin(), dutyId, reason);

            System.out.println("Заявка на зміну наряду створена!");
            Stage stage = (Stage) btnSend.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Виберіть наряд та вкажіть причину");
        }
    }
}
