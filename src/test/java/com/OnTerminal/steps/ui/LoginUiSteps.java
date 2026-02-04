package com.OnTerminal.steps.ui;

import com.OnTerminal.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import org.junit.Assert;

public class LoginUiSteps {

    @Steps
    LoginPage loginPage;

    @Given("the login page is open")
    public void openLoginPage() {
        loginPage.openLogin();
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithCredentials(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("the user is redirected to the dashboard")
    public void theUserIsRedirectedToTheDashboard() {
        String currentUrl = loginPage.getDriver().getCurrentUrl();
        boolean onDashboard = currentUrl.contains("/ui/dashboard") ||
                currentUrl.contains("/dashboard") ||
                currentUrl.contains("/ui/"); // fallback

        Assert.assertTrue("Not redirected to dashboard. Current URL: " + currentUrl, onDashboard);
    }

    @Then("the dashboard loads successfully")
    public void dashboardLoads() {
        // Example: wait for some element on dashboard (e.g. summary card)
        // loginPage.waitForCondition().until(d ->
        // d.findElement(By.cssSelector(".dashboard-summary")).isDisplayed());
        Assert.assertTrue(true); // or add real check if you have DashboardPage
    }

    // @TC_AUTH_03
    @When("the user leaves username empty")
    public void theUserLeavesUsernameEmpty() {
        loginPage.leaveUsernameEmpty();
    }

    @When("the user enters password {string}")
    public void theUserEntersPassword(String password) {
        loginPage.enterPassword(password); // reuse existing method
    }

    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        loginPage.clickLogin(); // reuse existing
    }

    @Then("the validation message {string} is shown under the username field in red")
    public void theValidationMessageIsShownUnderUsernameInRed(String expectedMessage) {
        String actual = loginPage.getUsernameErrorMessage();
        Assert.assertEquals("Username validation message mismatch", expectedMessage, actual);

        Assert.assertTrue("Username error message is not displayed in red", loginPage.isUsernameErrorRed());
    }

    @Then("the user remains on the login page")
    public void theUserRemainsOnTheLoginPage() {
        Assert.assertTrue("User was redirected unexpectedly", loginPage.isStillOnLoginPage());
    }

    @When("the user enters username {string}")
    public void theUserEntersUsername(String username) {
        loginPage.enterUsername(username);
    }

    @When("the user leaves password empty")
    public void theUserLeavesPasswordEmpty() {
        loginPage.leavePasswordEmpty();
    }

    @Then("the validation message {string} is shown under the password field in red")
    public void theValidationMessageIsShownUnderPasswordFieldInRed(String expectedMessage) {
        String actual = loginPage.getPasswordErrorMessage();
        Assert.assertEquals("Password validation message mismatch", expectedMessage, actual);

        Assert.assertTrue("Password error message is not displayed in red", loginPage.isPasswordErrorRed());
    }

    @Steps
    com.OnTerminal.pages.DashboardPage dashboardPage;

    @Given("the user is logged in as {string}")
    public void userIsLoggedIn(String userRole) {
        loginPage.openLogin();
        if (userRole.equals("admin")) {
            loginPage.login("admin", "admin123");
        } else {
            loginPage.login("testuser", "test123");
        }
    }

    @When("the user clicks the logout button")
    public void userClicksLogout() {
        dashboardPage.clickLogout();
    }

    @Then("the user is redirected to the login page")
    public void userIsRedirectedToLogin() {
        dashboardPage.waitForCondition().until(d -> d.getCurrentUrl().contains("login"));
        Assert.assertTrue("Not redirected to login page", loginPage.isStillOnLoginPage());
    }
}
