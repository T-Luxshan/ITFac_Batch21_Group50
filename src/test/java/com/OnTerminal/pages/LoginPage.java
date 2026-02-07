package com.OnTerminal.pages;

import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

/**
 * LoginPage - Handles login functionality
 * Uses ConfigManager and Constants for configuration values
 * Page Object Model + Data-Driven Framework
 */
public class LoginPage extends PageObject {

    private final ConfigManager config = ConfigManager.getInstance();

    /**
     * Login with specified username and password
     */
    public void login(String username, String password) {
        String loginUrl = config.getLoginUrl();
        System.out.println("[LoginPage] Navigating to: " + loginUrl);
        openUrl(loginUrl);

        WebElementFacade usernameField = findFirstPresent(
                By.name("username"),
                By.id("username"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input"));

        WebElementFacade passwordField = findFirstPresent(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"));

        WebElementFacade loginBtn = findFirstPresent(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("button"));

        usernameField.type(username);
        passwordField.type(password);
        loginBtn.click();

        // Wait for login to complete
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);

        // Wait for dashboard or any page to load after login
        waitForCondition().until(driver -> 
            driver.getCurrentUrl().contains(Constants.UrlPaths.UI_DASHBOARD) ||
            driver.getCurrentUrl().contains("/ui/"));

        System.out.println("[LoginPage] Login completed. Current URL: " + getDriver().getCurrentUrl());
    }

    /**
     * Login as Admin using credentials from Constants
     */
    public void loginAsAdmin() {
        login(Constants.Credentials.ADMIN_USERNAME, Constants.Credentials.ADMIN_PASSWORD);
    }

    /**
     * Login as User using credentials from Constants
     */
    public void loginAsUser() {
        login(Constants.Credentials.USER_USERNAME, Constants.Credentials.USER_PASSWORD);
    }

    /**
     * Login by role name
     */
    public void loginAs(String role) {
        if (role.equalsIgnoreCase("admin")) {
            loginAsAdmin();
        } else {
            // Login as User with correct credentials
            loginAsUser();
        }
    }

    /**
     * Navigate to logout
     */
    public void logout() {
        String logoutUrl = config.getLogoutUrl();
        openUrl(logoutUrl);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    /**
     * Check if on login page
     */
    public boolean isOnLoginPage() {
        return getDriver().getCurrentUrl().contains(Constants.UrlPaths.UI_LOGIN);
    }

    private WebElementFacade findFirstPresent(By... locators) {
        for (By by : locators) {
            WebElementFacade el = find(by);
            if (el.isPresent())
                return el;
        }
        // fallback (will fail loudly if nothing found)
        return find(locators[0]);
    }
}
