package org.example.inferface_distribution;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangeDutyDAO {

    private final Connection connection;

    public ChangeDutyDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveChangeRequest(String userPib, String reason, Date dutyDate, String requestType) throws SQLException {
        String sql = "INSERT INTO change_duties (user_pib, reason, duty_date, request_type, status) VALUES (?, ?, ?, ?, ?)"; // 5 плейсхолдерів

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userPib);
            ps.setString(2, reason);
            ps.setDate(3, dutyDate);
            ps.setString(4, requestType);
            ps.setString(5, "Очікування");

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
