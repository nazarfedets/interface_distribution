package org.example.inferface_distribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DutyDAO {

    public void addDuty(String userLogin, int year, int month, int day, String place) {
        String sql = "INSERT INTO duties (user_login, year, month, day, place) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userLogin);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            stmt.setInt(4, day);
            stmt.setString(5, place);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Duty> getDutiesForMonth(int year, int month) {
        List<Duty> list = new ArrayList<>();
        String sql = "SELECT * FROM duties WHERE year = ? AND month = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Duty(
                        rs.getInt("id"),
                        rs.getString("user_login"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day"),
                        rs.getString("place")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void clearMonth(int year, int month) {
        String sql = "DELETE FROM duties WHERE year = ? AND month = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
