package org.example.inferface_distribution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConnection {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/duty_distribution";
        String user = "postgres";
        String password = "12345";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Підключення до бази даних успішне!");
        } catch (SQLException e) {
            System.out.println("Помилка підключення до бази");
            e.printStackTrace();
        }
    }
}