package org.example.inferface_distribution;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML private Button loginButton;
    @FXML private  TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLable;

    @FXML
    public void initialize() {
        errorLable.setText("");

        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            try {
                if (login.equals("user") && password.equals("123")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setTitle("Меню користувача");
                    stage.setScene(new Scene(root));
                    stage.show();

                    closeLoginWindow();

                } else if (login.equals("admin") && password.equals("admin123")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setTitle("Меню адміністратора");
                    stage.setScene(new Scene(root));
                    stage.setMaximized(true);
                    stage.show();

                    closeLoginWindow();

                } else {
                    errorLable.setText("Невірний логін або пароль!");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                errorLable.setText("Помилка завантаження вікна!");
            }
        });
    }

    private void closeLoginWindow() {
        Stage closeloginStage = (Stage) loginButton.getScene().getWindow();
        closeloginStage.close();
    }
}