package org.example.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public final class Config {
    private static Map<String, Object> config;
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    static {
        try {
            InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.yaml");
            if (inputStream == null) {
                // If not in classpath, try common locations relative to project root
                String[] paths = { "config.yaml", "src/main/resources/config.yaml"};
                for (String path : paths) {
                    try {
                        FileInputStream fis = new FileInputStream(path);
                        config = new Yaml().load(fis);
                        log.info("Config loaded from: {}", path);
                        break;
                    } catch (Exception ignored) {
                    }
                }
            } else {
                config = new Yaml().load(inputStream);
                log.info("Config loaded from classpath");
            }

            if (config == null) {
                throw new java.io.FileNotFoundException("config.yaml not found in classpath or common local paths");
            }
        } catch (Exception e) {
            log.error("Fatal: Could not load config.yaml", e);
            System.exit(1);
        }
    }

    private Config() {
    }

    /**
     * Get a configuration value using dot notation (e.g., "db.host").
     *
     * @param path The path to the configuration value
     * @return The value
     * @throws RuntimeException if the path is not found
     */
    @SuppressWarnings("unchecked")
    public static Object getRequired(String path) {
        String[] keys = path.split("\\.");
        Object current = config;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
            } else {
                throw new RuntimeException("Missing required configuration: " + path);
            }
        }
        if (current == null) {
            throw new RuntimeException("Missing required configuration: " + path);
        }
        return current;
    }

    public static String getString(String path) {
        return getRequired(path).toString();
    }

    public static int getInt(String path) {
        Object value = getRequired(path);
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new RuntimeException("Configuration " + path + " must be a number");
    }

    public static String getDbHost() {
        return getString("db.host");
    }

    public static int getDbPort() {
        return getInt("db.port");
    }

    public static String getDbName() {
        return getString("db.name");
    }

    public static String getDbUser() {
        return getString("db.user");
    }

    public static String getDbPassword() {
        return getString("db.password");
    }
}
