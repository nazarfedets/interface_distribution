package org.example.inferface_distribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT login, password, pib, phone, course, group_name, role FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getString("pib"),
                        rs.getString("phone"),
                        rs.getString("course"),
                        rs.getString("group_name"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public String getLoginByPib(String pib) {
        String sql = "SELECT login FROM users WHERE pib = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pib);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("login");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByLogin(String login) {
        String sql = "SELECT login, password, pib, phone, course, group_name, role FROM users WHERE login = ?";
        User user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("pib"), // <-- Нам потрібен саме цей стовпець
                            rs.getString("phone"),
                            rs.getString("course"),
                            rs.getString("group_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
