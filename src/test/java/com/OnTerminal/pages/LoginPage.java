package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends PageObject {

    @FindBy(name = "username")
    WebElementFacade username;

    @FindBy(name = "password")
    WebElementFacade password;

    @FindBy(css = "button[type='submit']")
    WebElementFacade loginButton;

    public void login(String user, String pass) {
        openUrl("/ui/login");
        username.type(user);
        password.type(pass);
        loginButton.click();
    }
}
