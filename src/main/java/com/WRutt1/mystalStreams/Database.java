package com.WRutt1.mystalStreams;

import org.bukkit.plugin.java.JavaPlugin;
import java.sql.*;
import java.util.UUID;

public class Database {
    private final JavaPlugin plugin;
    private Connection connection;

    public Database(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/streams.db");
            createTable();
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка БД: " + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS active_streams (" +
                    "uuid TEXT PRIMARY KEY," +
                    "url TEXT NOT NULL)");
        }
    }

    public void addActiveStream(UUID uuid, String url) {
        String sql = "INSERT OR REPLACE INTO active_streams(uuid, url) VALUES(?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, url);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка добавления стрима: " + e.getMessage());
        }
    }

    public void removeActiveStream(UUID uuid) {
        String sql = "DELETE FROM active_streams WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка удаления стрима: " + e.getMessage());
        }
    }

    public boolean isStreaming(UUID uuid) {
        String sql = "SELECT 1 FROM active_streams WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid.toString());
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка проверки стрима: " + e.getMessage());
        }
        return false;
    }

    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка закрытия БД: " + e.getMessage());
        }
    }
}