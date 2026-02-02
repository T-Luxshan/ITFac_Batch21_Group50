package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

public class LoginPage extends PageObject {

    public void login(String user, String pass) {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) {
            base = "http://localhost:8080";
        }
        String url = base.endsWith("/") ? base + "ui/login" : base + "/ui/login";
        openUrl(url);

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
    public WebElementFacade getSuccessMessageElement() {
        return findFirstPresent(
                By.cssSelector(".alert-success"),              // Bootstrap standard success alert
                By.cssSelector(".success-message"),            // custom class
                By.cssSelector("[role='alert'].alert-success"),// ARIA + Bootstrap
                By.cssSelector(".toast-success"),              // if using toasts
                By.cssSelector(".alert.alert-success"),        // sometimes double class
                By.xpath("//*[contains(text(), 'You have been logged in successfully')]")
        );
    }

//    public String getSuccessMessageText() {
//        WebElementFacade msg = getSuccessMessageElement();
//        if (msg.isCurrentlyVisible()) {
//            return msg.getText().trim();
//        }
//        return "";
//    }
//
//    public boolean isSuccessMessageGreen() {
//        WebElementFacade msg = getSuccessMessageElement();
//        if (!msg.isCurrentlyVisible()) {
//            return false;
//        }
//
//        String bgColor = msg.getCssValue("background-color");
//        String textColor = msg.getCssValue("color");
//
//        // Bootstrap .alert-success usually has:
//        // background: light green (rgba(209, 231, 221, ...) or #d1e7dd)
//        // text: darker green
//        return (bgColor.contains("231, 221") || bgColor.contains("#d1e7dd") || bgColor.contains("green")) &&
//                (textColor.contains("0, 128, 0") || textColor.contains("green") || textColor.contains("darkgreen"));
//    }
}
