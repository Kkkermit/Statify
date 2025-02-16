package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private final Properties properties;

    public ConfigUtil() throws IOException {
        properties = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
        if (input == null) {
            throw new IOException("config.properties not found!");
        }
        properties.load(input);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}