package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

public class CategoriesPage extends PageObject {

    public void openCategories() {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        String url = base.endsWith("/") ? base + "ui/categories" : base + "/ui/categories";
        openUrl(url);
    }

    public boolean isTableVisible() {
        WebElementFacade table = find(By.cssSelector("table"));
        return table.isPresent() && table.isVisible();
    }

    public boolean isPaginationVisible() {
        return !findAll(By.cssSelector(
                ".pagination, ul.pagination, nav[aria-label*='Pagination'], nav[aria-label*='pagination'], [class*='pagination']"))
                .isEmpty();
    }
}
