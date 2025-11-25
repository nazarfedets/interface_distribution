package org.example.inferface_distribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeDutyRequestDAO {

    public List<RequestData> getAllChangeDutyRequests() {
        List<RequestData> list = new ArrayList<>();

        String sql = "SELECT id, user_pib, duty_id, reason, status FROM change_duties";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new RequestData(
                        rs.getInt("id"),
                        rs.getString("user_pib"),
                        rs.getString("reason"),
                        "-",
                        "Зміна наряду",
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
