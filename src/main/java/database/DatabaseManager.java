package database;

import utils.LogUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bot.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            // Initialize database connection
            connection = DriverManager.getConnection(DB_URL);
            LogUtil.info("Database connection established successfully");
            
            // Create necessary tables
            initializeTables();
            
        } catch (SQLException e) {
            LogUtil.error("Failed to initialize database", e);
        }
    }

    private void initializeTables() throws SQLException {
        // Example table creation
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String createGuildsTable = """
            CREATE TABLE IF NOT EXISTS guilds (
                guild_id TEXT PRIMARY KEY,
                guild_name TEXT NOT NULL,
                join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (PreparedStatement stmt = connection.prepareStatement(createUsersTable)) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(createGuildsTable)) {
            stmt.execute();
        }
        
        LogUtil.info("Database tables initialized successfully");
    }

    public void addUser(String userId, String username) {
        String sql = "INSERT OR IGNORE INTO users (user_id, username) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error("Failed to add user to database", e);
        }
    }

    public void addGuild(String guildId, String guildName) {
        String sql = "INSERT OR IGNORE INTO guilds (guild_id, guild_name) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, guildId);
            stmt.setString(2, guildName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error("Failed to add guild to database", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LogUtil.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            LogUtil.error("Failed to close database connection", e);
        }
    }
}