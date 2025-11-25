package org.example.inferface_distribution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RequestVocationDAO {

    public void saveRequest(String pib, String reason, String date, String type) {
        String sql = "INSERT INTO request_vocation (pib, reason, date, type, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pib);
            stmt.setString(2, reason);
            stmt.setString(3, date);
            stmt.setString(4, type);
            stmt.setString(5, "Очікує");

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RequestData> getAllRequests() {
        List<RequestData> list = new ArrayList<>();

        String sql = "SELECT id, pib, reason, date, type, status FROM request_vocation";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new RequestData(
                        rs.getInt("id"),
                        rs.getString("pib"),
                        rs.getString("reason"),
                        rs.getString("date"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int id, String newStatus) {
        String sql = "UPDATE request_vocation SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
