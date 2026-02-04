package com.OnTerminal.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration Manager - Centralized Configuration Handling
 * ============================================================
 * Best Practice: Separates configuration from code (Modular Framework)
 */
public class ConfigManager {

    private static Properties properties;
    private static ConfigManager instance;

    // ==================== Singleton Pattern ====================
    
    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            // Load from multiple sources with fallback
            loadFromResource("serenity.properties");
            loadFromResource("application.properties");
        } catch (Exception e) {
            System.out.println("Properties loaded from system defaults");
        }
    }

    private void loadFromResource(String fileName) {
        try (FileInputStream fis = new FileInputStream("src/test/resources/" + fileName)) {
            properties.load(fis);
        } catch (IOException e) {
            // Try classpath
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream(fileName));
            } catch (Exception ex) {
                System.out.println("Could not load " + fileName);
            }
        }
    }

    // ==================== Base URL Configuration ====================

    /**
     * Get base URL with fallback
     * Priority: System property > serenity.properties > default
     */
    public String getBaseUrl() {
        String url = System.getProperty("webdriver.base.url");
        if (url == null || url.isBlank()) {
            url = properties.getProperty("webdriver.base.url");
        }
        if (url == null || url.isBlank()) {
            url = "http://localhost:8080";
        }
        // Remove trailing slash for consistency
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Get API base URL
     */
    public String getApiBaseUrl() {
        String url = System.getProperty("api.base.url");
        if (url == null || url.isBlank()) {
            url = properties.getProperty("api.base.url");
        }
        if (url == null || url.isBlank()) {
            url = getBaseUrl();
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    // ==================== UI URLs ====================

    public String getLoginUrl() {
        return getBaseUrl() + "/ui/login";
    }

    public String getDashboardUrl() {
        return getBaseUrl() + "/ui/dashboard";
    }

    public String getCategoriesUrl() {
        return getBaseUrl() + "/ui/categories";
    }

    public String getPlantsUrl() {
        return getBaseUrl() + "/ui/plants";
    }

    public String getSalesUrl() {
        return getBaseUrl() + "/ui/sales";
    }

    public String getSellPlantUrl() {
        return getBaseUrl() + "/ui/sales/new";
    }

    public String getAddPlantUrl() {
        return getBaseUrl() + "/ui/plants/add";
    }

    public String getEditPlantUrl(String plantId) {
        return getBaseUrl() + "/ui/plants/edit/" + plantId;
    }

    public String getAddCategoryUrl() {
        return getBaseUrl() + "/ui/categories/add";
    }

    public String getLogoutUrl() {
        return getBaseUrl() + "/ui/logout";
    }

    // ==================== API Endpoints ====================

    public String getAuthEndpoint() {
        return "/api/auth/login";
    }

    public String getCategoriesEndpoint() {
        return "/api/categories";
    }

    public String getPlantsEndpoint() {
        return "/api/plants";
    }

    public String getSalesEndpoint() {
        return "/api/sales";
    }

    // ==================== Timeout Configuration ====================

    public int getDefaultTimeout() {
        return getIntProperty("webdriver.wait.timeout", 10);
    }

    public int getPageLoadTimeout() {
        return getIntProperty("webdriver.pageload.timeout", 30);
    }

    public int getImplicitWait() {
        return getIntProperty("webdriver.implicit.wait", 5);
    }

    // ==================== Browser Configuration ====================

    public String getBrowser() {
        return getProperty("webdriver.driver", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("webdriver.headless", "false"));
    }

    // ==================== Utility Methods ====================

    public String getProperty(String key) {
        String value = System.getProperty(key);
        return value != null ? value : properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}
