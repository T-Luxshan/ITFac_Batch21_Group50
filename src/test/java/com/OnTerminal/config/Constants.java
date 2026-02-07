package com.OnTerminal.config;

/**
 * Application Constants - Centralized Constant Values
 * ====================================================
 * 
 * Modular Framework Principles:
 * All constants in one place (Data-Driven Framework)
 */
public final class Constants {

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }

    // ==================== Test Credentials ====================
    // Note: In production, use environment variables or secure vault
    
    public static final class Credentials {
        public static final String ADMIN_USERNAME = "admin";
        public static final String ADMIN_PASSWORD = "admin123";
        // Correct User credentials for QA Training App
        public static final String USER_USERNAME = "testuser";
        public static final String USER_PASSWORD = "test123";
    }

    // ==================== HTTP Status Codes ====================

    public static final class StatusCodes {
        // 2XX - Success
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
    }

    // ==================== Timeout Values (in seconds) ====================
    
    public static final class Timeouts {
        public static final int SHORT_WAIT = 2;
    }

    // ==================== URL Paths ====================
    
    public static final class UrlPaths {
        // UI Paths
        public static final String UI_LOGIN = "/ui/login";
        public static final String UI_DASHBOARD = "/ui/dashboard";
        
        // API Paths
        public static final String API_AUTH_LOGIN = "/api/auth/login";
        public static final String API_CATEGORIES = "/api/categories";
        public static final String API_PLANTS = "/api/plants";
        public static final String API_SALES = "/api/sales";
    }

    // ==================== Validation Messages ====================
    
    public static final class ValidationMessages {
        public static final String QUANTITY_POSITIVE = "Quantity must be greater than 0";
    }
}
