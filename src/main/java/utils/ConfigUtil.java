package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private final Properties properties;

    public ConfigUtil() throws IOException {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() throws IOException {
        String[] paths = {
            "config.properties",
            "config/config.properties",
            "/app/config/config.properties",
            "src/main/resources/config.properties",
            System.getProperty("config.path") + "/config.properties",
            System.getenv("CONFIG_PATH") + "/config.properties"
        };

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
                replaceWithEnvVars();
                LogUtil.info("Loaded configuration from classpath");
                return;
            }
        }

        for (String path : paths) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        properties.load(fis);
                        replaceWithEnvVars();
                        LogUtil.info("Loaded configuration from: " + file.getAbsolutePath());
                        return;
                    }
                }
            } catch (Exception e) {
                LogUtil.debug("Could not load from " + path + ": " + e.getMessage());
            }
        }

        createDefaultConfig();
    }

    private void createDefaultConfig() {
        properties.setProperty("bot.token", "${BOT_TOKEN}");
        properties.setProperty("bot.activity", "with music");
        properties.setProperty("spotify.client.id", "${SPOTIFY_CLIENT_ID}");
        properties.setProperty("spotify.client.secret", "${SPOTIFY_CLIENT_SECRET}");
        properties.setProperty("spotify.redirect.uri", "${SPOTIFY_REDIRECT_URI}");
        replaceWithEnvVars();
        LogUtil.info("Created default configuration with environment variables");
    }

    private void replaceWithEnvVars() {
        replaceIfExists("bot.token", "BOT_TOKEN");
        replaceIfExists("spotify.client.id", "SPOTIFY_CLIENT_ID");
        replaceIfExists("spotify.client.secret", "SPOTIFY_CLIENT_SECRET");
        replaceIfExists("spotify.redirect.uri", "SPOTIFY_REDIRECT_URI");
    }

    private void replaceIfExists(String prop, String envVar) {
        String envValue = System.getenv(envVar);
        if (envValue != null && !envValue.isEmpty()) {
            properties.setProperty(prop, envValue);
        }
    }

    public String getProperty(String key) {
        String value = System.getenv(key.toUpperCase().replace('.', '_'));
        if (value != null && !value.isEmpty()) {
            return value;
        }
        
        value = properties.getProperty(key);
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String envKey = value.substring(2, value.length() - 1);
            String envValue = System.getenv(envKey);
            return envValue != null ? envValue : value;
        }
        
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}