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
        public static final int NO_CONTENT = 204;
        
        // 4XX - Client Errors
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int METHOD_NOT_ALLOWED = 405;
        
        // 5XX - Server Errors
        public static final int INTERNAL_SERVER_ERROR = 500;
        public static final int BAD_GATEWAY = 502;
        public static final int SERVICE_UNAVAILABLE = 503;
    }

    // ==================== Timeout Values (in seconds) ====================
    
    public static final class Timeouts {
        public static final int SHORT_WAIT = 2;
        public static final int MEDIUM_WAIT = 5;
        public static final int LONG_WAIT = 10;
        public static final int PAGE_LOAD = 30;
        public static final int ELEMENT_WAIT = 15;
        public static final int POLLING_INTERVAL = 500; // milliseconds
    }

    // ==================== URL Paths ====================
    
    public static final class UrlPaths {
        // UI Paths
        public static final String UI_LOGIN = "/ui/login";
        public static final String UI_LOGOUT = "/ui/logout";
        public static final String UI_DASHBOARD = "/ui/dashboard";
        public static final String UI_CATEGORIES = "/ui/categories";
        public static final String UI_CATEGORIES_ADD = "/ui/categories/add";
        public static final String UI_PLANTS = "/ui/plants";
        public static final String UI_PLANTS_ADD = "/ui/plants/add";
        public static final String UI_PLANTS_EDIT = "/ui/plants/edit/";
        public static final String UI_SALES = "/ui/sales";
        public static final String UI_SALES_NEW = "/ui/sales/new";
        
        // API Paths
        public static final String API_AUTH_LOGIN = "/api/auth/login";
        public static final String API_CATEGORIES = "/api/categories";
        public static final String API_CATEGORIES_MAIN = "/api/categories/main";
        public static final String API_CATEGORIES_SUB = "/api/categories/sub-categories";
        public static final String API_PLANTS = "/api/plants";
        public static final String API_PLANTS_SUMMARY = "/api/plants/summary";
        public static final String API_SALES = "/api/sales";
    }

    // ==================== Validation Messages ====================
    
    public static final class ValidationMessages {
        // Login
        public static final String USERNAME_REQUIRED = "Username is required";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String INVALID_CREDENTIALS = "Invalid username or password";
        
        // Category
        public static final String CATEGORY_NAME_REQUIRED = "Category name is required";
        public static final String CATEGORY_NAME_LENGTH = "Category name must be between 3 and 10 characters";
        public static final String NO_CATEGORY_FOUND = "No category found";
        
        // Plant
        public static final String PLANT_NAME_REQUIRED = "Plant name is required";
        public static final String PLANT_NAME_LENGTH = "Plant name must be between 3 and 25 characters";
        public static final String PRICE_REQUIRED = "Price is required";
        public static final String PRICE_POSITIVE = "Price must be greater than 0";
        public static final String QUANTITY_REQUIRED = "Quantity is required";
        public static final String QUANTITY_NON_NEGATIVE = "Quantity cannot be negative";
        public static final String PLANT_CATEGORY_REQUIRED = "Category is required";
        public static final String NO_PLANTS_FOUND = "No plants found";
        
        // Sales
        public static final String PLANT_REQUIRED = "Plant is required";
        public static final String QUANTITY_POSITIVE = "Quantity must be greater than 0";
        public static final String NO_SALES_FOUND = "No sales found";
    }

    // ==================== CSS Selectors (Common) ====================
    
    public static final class Locators {
        // Tables
        public static final String TABLE = "table";
        public static final String TABLE_BODY = "table tbody";
        public static final String TABLE_ROW = "table tbody tr";
        public static final String TABLE_CELL = "td";
        
        // Forms
        public static final String FORM = "form";
        public static final String INPUT_TEXT = "input[type='text']";
        public static final String INPUT_PASSWORD = "input[type='password']";
        public static final String INPUT_NUMBER = "input[type='number']";
        public static final String SELECT = "select";
        public static final String BUTTON_SUBMIT = "button[type='submit']";
        
        // Navigation
        public static final String NAV_ACTIVE = ".active, .nav-link.active, [aria-current='page']";
        
        // Messages
        public static final String ERROR_MESSAGE = ".error, .alert-danger, .text-danger, [class*='error']";
        public static final String SUCCESS_MESSAGE = ".success, .alert-success, .text-success, [class*='success']";
        
        // Pagination
        public static final String PAGINATION = ".pagination, nav[aria-label*='pagination']";
    }

    // ==================== Test Tags ====================
    
    public static final class Tags {
        public static final String UI = "@ui";
        public static final String API = "@api";
        public static final String ADMIN = "@admin";
        public static final String USER = "@user";
        public static final String SMOKE = "@smoke";
        public static final String REGRESSION = "@regression";
        public static final String SECURITY = "@security";
        public static final String NEGATIVE = "@negative";
    }

    // ==================== Data Patterns ====================
    
    public static final class Patterns {
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@(.+)$";
        public static final String NUMERIC = "^[0-9]+$";
        public static final String DECIMAL = "^[0-9]+(\\.[0-9]+)?$";
        public static final String ALPHANUMERIC = "^[A-Za-z0-9]+$";
    }

    // ==================== Low Stock Threshold ====================
    
    public static final int LOW_STOCK_THRESHOLD = 5;
}
