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
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setText("");

        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            User user = Database.getUserByLogin(login);
            if (user != null && user.getPassword().equals(password)) {
                try {
                    FXMLLoader loader;
                    Stage stage = new Stage();

                    if (user.getLogin().equals("admin")) {
                        loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
                        stage.setTitle("Меню адміністратора");
                        stage.setMaximized(true);
                    } else {
                        loader = new FXMLLoader(getClass().getResource("user-view.fxml"));
                        stage.setTitle("Меню користувача");
                    }

                    Parent root = loader.load();

                    // Передаємо користувача у контролер
                    Object controller = loader.getController();
                    if (controller instanceof UserviewController) {
                        ((UserviewController) controller).setUser(user);
                    }

                    stage.setScene(new Scene(root));
                    stage.show();

                    closeLoginWindow();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    errorLabel.setText("Помилка завантаження вікна!");
                }
            } else {
                errorLabel.setText("Невірний логін або пароль!");
            }
        });
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}
