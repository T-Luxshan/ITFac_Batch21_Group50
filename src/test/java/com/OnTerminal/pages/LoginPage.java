package com.OnTerminal.pages;

import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import java.time.Duration;

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
     * Open the login page without submitting credentials
     */
    public void openLogin() {
        String loginUrl = config.getLoginUrl();
        System.out.println("[LoginPage] Navigating to: " + loginUrl);
        openUrl(loginUrl);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
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

    // After successful login — common Bootstrap classes
    // Try these in order (most likely first)
    // public WebElementFacade getSuccessMessageElement() {
    // return findFirstPresent(
    // By.cssSelector(".alert-success"), // Bootstrap standard success alert
    // By.cssSelector(".success-message"), // custom class
    // By.cssSelector("[role='alert'].alert-success"),// ARIA + Bootstrap
    // By.cssSelector(".toast-success"), // if using toasts
    // By.cssSelector(".alert.alert-success"), // sometimes double class
    // By.xpath("//*[contains(text(), 'You have been logged in successfully')]")
    // );
    // }

    // New reusable method: type only password (for negative cases)
    public void enterPassword(String password) {
        WebElementFacade passwordField = findFirstPresent(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"));
        passwordField.clear();
        passwordField.type(password);
    }

    // New reusable method: click the login button only
    public void clickLogin() {
        WebElementFacade loginBtn = findFirstPresent(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("button.btn-primary"), // common Bootstrap submit button
                By.cssSelector("button"));
        loginBtn.click();
    }

    // New: clear username field
    public void leaveUsernameEmpty() {
        // Wait for ANY input field to be visible (positive login finds them)
        waitForCondition().withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(500))
                .until(d -> find(By.name("username")).isVisible() ||
                        find(By.cssSelector("input[type='text']")).isVisible());

        System.out.println("Page loaded - URL: " + getDriver().getCurrentUrl());

        WebElementFacade username = findFirstPresent(
                By.name("username"), // from your HTML
                By.cssSelector("input[name='username']"),
                By.cssSelector("input.form-control"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input[placeholder='Enter your username']"), // exact placeholder match
                By.cssSelector("input") // last resort
        );

        if (username == null || !username.isPresent()) {
            throw new RuntimeException(
                    "Username field not found after wait. Current URL: " + getDriver().getCurrentUrl());
        }

        username.clear();
    }

    // New: get username error message text (adjust selectors after manual inspect)
    private WebElementFacade findFirstVisible(By... locators) {
        for (By by : locators) {
            WebElementFacade el = find(by);
            if (el.isCurrentlyVisible()) {
                return el;
            }
        }
        return null;
    }

    public String getUsernameErrorMessage() {
        WebElementFacade errorElement = findFirstVisible(
                By.cssSelector(".invalid-feedback"),
                By.cssSelector(".text-danger"),
                By.cssSelector("[for='username'] ~ .error"),
                By.cssSelector("input[name='username'] ~ .error"),
                By.cssSelector(".form-text.text-danger"),
                By.xpath("//*[contains(text(), 'Username is required')]"));

        if (errorElement != null) {
            return errorElement.getText().trim();
        }
        return "";
    }

    public boolean isUsernameErrorRed() {
        WebElementFacade errorElement = findFirstVisible(
                By.cssSelector(".invalid-feedback"),
                By.cssSelector(".text-danger"),
                By.cssSelector("[for='username'] ~ .error"),
                By.cssSelector("input[name='username'] ~ .error"),
                By.cssSelector(".form-text.text-danger"),
                By.xpath("//*[contains(text(), 'Username is required')]"));

        if (errorElement == null) {
            return false;
        }

        String color = errorElement.getCssValue("color");
        return color.contains("255, 0, 0") ||
                color.contains("220, 53, 69") ||
                color.contains("220, 38, 38") || // Tailwind red-600
                color.toLowerCase().contains("red");
    }

    public void enterUsername(String username) {
        WebElementFacade usernameField = findFirstPresent(
                By.name("username"),
                By.cssSelector("input[type='text']"));
        usernameField.clear();
        usernameField.type(username);
    }

    public void leavePasswordEmpty() {
        WebElementFacade passwordField = findFirstPresent(
                By.name("password"),
                By.cssSelector("input[type='password']"));
        passwordField.clear();
    }

    public String getPasswordErrorMessage() {
        WebElementFacade errorElement = findFirstVisible(
                By.cssSelector("input[name='password'] ~ .invalid-feedback"),
                By.cssSelector("input[name='password'] ~ .text-danger"),
                By.cssSelector("#password ~ .invalid-feedback"),
                By.cssSelector("#password ~ .text-danger"),
                By.cssSelector(".password-error"),
                By.cssSelector("[for='password'] ~ .error"),
                By.cssSelector("input[name='password'] ~ .error"),
                By.cssSelector(".alert.alert-danger"),
                By.cssSelector(".invalid-message"),
                By.xpath("//*[contains(text(), 'Password is required')]"),
                By.xpath("//*[contains(text(), 'Invalid username or password')]"));

        if (errorElement != null) {
            return errorElement.getText().trim();
        }
        return "";
    }

    public boolean isPasswordErrorRed() {
        WebElementFacade errorElement = findFirstVisible(
                By.cssSelector("input[name='password'] ~ .invalid-feedback"),
                By.cssSelector("input[name='password'] ~ .text-danger"),
                By.cssSelector("#password ~ .invalid-feedback"),
                By.cssSelector("#password ~ .text-danger"),
                By.cssSelector(".password-error"),
                By.cssSelector("[for='password'] ~ .error"),
                By.cssSelector("input[name='password'] ~ .error"),
                By.cssSelector(".alert.alert-danger"),
                By.cssSelector(".invalid-message"),
                By.xpath("//*[contains(text(), 'Password is required')]"),
                By.xpath("//*[contains(text(), 'Invalid username or password')]"));

        if (errorElement == null) {
            return false;
        }

        String color = errorElement.getCssValue("color");
        String bgColor = errorElement.getCssValue("background-color");

        return color.contains("255, 0, 0") ||
                color.contains("220, 53, 69") || // Bootstrap danger text
                color.contains("114, 28, 36") || // Bootstrap alert-danger text proper
                color.contains("220, 38, 38") || // Tailwind red-600
                color.toLowerCase().contains("red") ||
                bgColor.contains("248, 215, 218"); // Bootstrap alert-danger bg
    }

    public boolean isStillOnLoginPage() {
        String url = getDriver().getCurrentUrl();
        return url.contains("/ui/login") || url.endsWith("/login");
    }
}
