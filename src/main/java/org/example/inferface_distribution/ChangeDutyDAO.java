package org.example.inferface_distribution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeDutyDAO {

    public void saveChangeRequest(String userPib, int dutyId, String reason) {
        String sql = "INSERT INTO change_duties (user_pib, duty_id, reason, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userPib);
            stmt.setInt(2, dutyId);
            stmt.setString(3, reason);
            stmt.setString(4, "Очікує");
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
