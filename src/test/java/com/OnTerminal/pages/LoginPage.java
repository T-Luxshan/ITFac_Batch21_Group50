package com.OnTerminal.pages;

import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

import java.time.Duration;

public class LoginPage extends PageObject {

    public void openLogin() {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) {
            base = "http://localhost:8080";
        }
        String url = base.endsWith("/") ? base + "ui/login" : base + "/ui/login";
        openUrl(url);
    }

    /**
     * Login as specified role using predefined credentials
     */
    public void loginAs(String role) {
        if ("admin".equalsIgnoreCase(role)) {
            login(Constants.Credentials.ADMIN_USERNAME, Constants.Credentials.ADMIN_PASSWORD);
        } else if ("user".equalsIgnoreCase(role) || "testuser".equalsIgnoreCase(role)) {
            login(Constants.Credentials.USER_USERNAME, Constants.Credentials.USER_PASSWORD);
        } else {
            throw new IllegalArgumentException("Unknown role for login: " + role);
        }
    }

    public void login(String user, String pass) {
        openLogin();

        WebElementFacade username = findFirstPresent(
                By.name("username"),
                By.id("username"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input"));

        WebElementFacade password = findFirstPresent(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"));

        WebElementFacade loginBtn = findFirstPresent(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("button"));

        username.type(user);
        password.type(pass);
        loginBtn.click();

        // Wait for login to complete and redirect to happen
        waitABit(2000);

        // Wait for dashboard or any page to load after login
        waitForCondition().until(driver -> driver.getCurrentUrl().contains("/ui/dashboard") ||
                driver.getCurrentUrl().contains("/ui/"));

        System.out.println("Login completed. Current URL: " + getDriver().getCurrentUrl());
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
