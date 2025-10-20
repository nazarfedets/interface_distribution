package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ViewRequestVocatuonController {
    @FXML Button btnCancel;

    @FXML
    private void cancelButton(){
        Stage backWindow = (Stage) btnCancel.getScene().getWindow();
        backWindow.close();
    }
}
