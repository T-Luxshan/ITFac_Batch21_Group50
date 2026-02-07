package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

public class DashboardPage extends PageObject {

    public void clickLogout() {
        WebElementFacade logoutBtn = findFirstPresent(
                By.cssSelector("a[href*='logout']"),
                By.cssSelector("button.logout-btn"),
                By.xpath("//button[contains(text(), 'Logout')]"),
                By.xpath("//a[contains(text(), 'Logout')]"),
                By.cssSelector(".nav-link[href='/logout']"));

        if (logoutBtn.isPresent()) {
            logoutBtn.click();
        } else {
            // Try clicking a profile menu first if logout is hidden
            WebElementFacade profileMenu = findFirstPresent(
                    By.id("profile-dropdown"),
                    By.cssSelector(".profile-menu"),
                    By.cssSelector(".avatar"));
            if (profileMenu.isPresent()) {
                profileMenu.click();
                waitABit(500);
                find(By.xpath("//*[contains(text(), 'Logout')]")).click();
            }
        }
    }

    public void clickInventory() {
        WebElementFacade inventoryBtn = findFirstPresent(
                By.xpath("//a[contains(., 'Inventory')]"),
                By.xpath("//span[contains(text(), 'Inventory')]"),
                By.cssSelector("a[href*='inventory']"),
                By.cssSelector(".nav-link[href='/inventory']"),
                By.id("inventory-menu"));

        inventoryBtn.waitUntilEnabled().click();
        waitABit(1000);
    }

    public boolean isInventoryPageDisplayed() {
        return getDriver().getCurrentUrl().contains("/inventory") ||
                getDriver().getCurrentUrl().contains("/Inventory") ||
                containsText("Inventory List") ||
                containsText("Stock Management");
    }

    private WebElementFacade findFirstPresent(By... locators) {
        for (By by : locators) {
            WebElementFacade el = find(by);
            if (el.isPresent())
                return el;
        }
        return find(locators[0]); // fallback
    }
}
