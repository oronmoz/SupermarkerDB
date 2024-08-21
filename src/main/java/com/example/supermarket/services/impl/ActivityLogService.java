package com.example.supermarket.services.impl;

import com.example.supermarket.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogService {
    private DatabaseManager dbManager;

    public ActivityLogService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<String> getRecentActivities(int limit) {
        List<String> activities = new ArrayList<>();
        String query = "SELECT activity, timestamp FROM activity_log ORDER BY timestamp DESC LIMIT ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String activity = rs.getString("activity");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                activities.add(timestamp + ": " + activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        return activities;
    }

    public void logActivity(String activity) {
        String query = "INSERT INTO activity_log (activity, timestamp) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, activity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }
}