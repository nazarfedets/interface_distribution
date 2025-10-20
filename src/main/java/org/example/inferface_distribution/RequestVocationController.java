package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;


public class RequestVocationController {

    @FXML public Button btnSand;
    @FXML private ChoiceBox<String> typeVocation;
    @FXML private Button btnCancel;



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


}
