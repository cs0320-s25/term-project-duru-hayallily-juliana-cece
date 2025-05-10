package main.edu.brown.cs.student.main.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
  private static final Properties properties = new Properties();
  private static AppConfig instance;

  // Private constructor for singleton pattern
  private AppConfig() {
    try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        System.err.println("Unable to find config.properties file");
        return;
      }

      // Load properties file
      properties.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  // Singleton pattern to get the instance
  public static synchronized AppConfig getInstance() {
    if (instance == null) {
      instance = new AppConfig();
    }
    return instance;
  }

  /**
   * Get a property from the configuration file
   * @param key the property key
   * @return the property value or null if not found
   */
  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  /**
   * Get the API key for Spoonacular
   * @return the API key
   */
  public String getSpoonacularApiKey() {
    return getProperty("spoonacular.api.key");
  }

  /**
   * Get the server port
   * @return the server port
   */
  public int getServerPort() {
    String port = getProperty("server.port");
    return port != null ? Integer.parseInt(port) : 8080;
  }

  /**
   * Check if CORS is enabled
   * @return true if CORS is enabled, false otherwise
   */
  public boolean isCorsEnabled() {
    String cors = getProperty("cors.enabled");
    return cors != null && Boolean.parseBoolean(cors);
  }

  /**
   * Get allowed CORS origins
   * @return comma-separated list of allowed origins
   */
  public String getCorsAllowedOrigins() {
    return getProperty("cors.allowed.origins");
  }
}
