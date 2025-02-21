package database;

import utils.LogUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bot.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            LogUtil.info("Database connection established successfully");
            
            initializeTables();
            
        } catch (SQLException e) {
            LogUtil.error("Failed to initialize database", e);
        }
    }

    private void initializeTables() throws SQLException {
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

        String createSpotifyTable = """
            CREATE TABLE IF NOT EXISTS spotify_data (
                discord_user_id TEXT PRIMARY KEY,
                spotify_user_id TEXT,
                access_token TEXT,
                refresh_token TEXT,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (PreparedStatement stmt = connection.prepareStatement(createUsersTable)) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(createGuildsTable)) {
            stmt.execute();
        }

        try (PreparedStatement stmt = connection.prepareStatement(createSpotifyTable)) {
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

    public void updateSpotifyTokens(String discordUserId, String spotifyUserId, String accessToken, String refreshToken) {
        String sql = """
            INSERT OR REPLACE INTO spotify_data 
            (discord_user_id, spotify_user_id, access_token, refresh_token, last_updated)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, discordUserId);
            stmt.setString(2, spotifyUserId);
            stmt.setString(3, accessToken);
            stmt.setString(4, refreshToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error("Failed to update Spotify tokens", e);
        }
    }

    public Map<String, String> getSpotifyTokens(String discordUserId) {
        String sql = "SELECT access_token, refresh_token FROM spotify_data WHERE discord_user_id = ?";
        Map<String, String> tokens = new HashMap<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, discordUserId);
            var rs = stmt.executeQuery();
            
            if (rs.next()) {
                tokens.put("access_token", rs.getString("access_token"));
                tokens.put("refresh_token", rs.getString("refresh_token"));
            }
        } catch (SQLException e) {
            LogUtil.error("Failed to get Spotify tokens", e);
        }
        
        return tokens;
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