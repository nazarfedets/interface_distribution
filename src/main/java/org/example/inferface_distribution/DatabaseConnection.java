package org.example.inferface_distribution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/duty_distribution?sslmode=disable";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static User getUserByLogin(String login) {
        String sql = "SELECT login, password, pib, phone, course, group_name, role FROM users WHERE login = ?";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("pib"),
                        rs.getString("phone"),
                        rs.getString("course"),
                        rs.getString("group_name"),
                        rs.getString("role")
                );

            }

        } catch (SQLException e) {
            System.err.println("Помилка при виконанні запиту getUserByLogin:");
            e.printStackTrace();;
        }

        return null;
    }
}
