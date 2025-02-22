package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvLoader {
    public static void load() {
        String botToken = System.getenv("BOT_TOKEN");
        if (botToken != null && !botToken.isEmpty()) {
            LogUtil.info("Using environment variables from system");
            return;
        }

        String[] possiblePaths = {
            ".env",
            "config/.env",
            "../.env",
            System.getProperty("user.dir") + "/.env"
        };

        for (String pathStr : possiblePaths) {
            try {
                Path path = Paths.get(pathStr);
                if (path.toFile().exists()) {
                    loadFromFile(path);
                    LogUtil.info("Loaded environment variables from: " + path);
                    return;
                }
            } catch (Exception e) {
                LogUtil.debug("Could not load from " + pathStr);
            }
        }

        LogUtil.warn("No .env file found, will try to use environment variables");
    }

    private static void loadFromFile(Path path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (!System.getenv().containsKey(key)) {
                        System.setProperty(key, value);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("Error loading .env file", e);
        }
    }
}
