package org.example.inferface_distribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeDutyRequestDAO {

    public List<RequestData> getAllChangeDutyRequests() {
        List<RequestData> list = new ArrayList<>();

        String sql = "SELECT id, user_pib, reason, status, duty_date, request_type FROM change_duties";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new RequestData(
                        rs.getInt("id"),
                        rs.getString("user_pib"),
                        rs.getString("reason"),
                        rs.getString("duty_date"),
                        rs.getString("request_type"),
                        rs.getString("status")
                ));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int id, String newStatus) {
        String sql = "UPDATE change_duties SET status = ? WHERE id = ?";
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
