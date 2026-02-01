package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

public class CategoriesPage extends PageObject {

    public void openCategories() {
        // Construct full URL from base URL property
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) {
            base = "http://localhost:8080";
        }
        String url = base.endsWith("/") ? base + "ui/categories" : base + "/ui/categories";
        openUrl(url);

        // wait until we are on categories page
        waitForCondition().until(driver -> driver.getCurrentUrl().contains("/ui/categories"));
    }

    public boolean isTableVisible() {
        // Allow page to be considered "loaded" if:
        // - table exists OR
        // - grid exists (MUI etc.) OR
        // - "No category found" appears (valid empty state)
        boolean hasTable = !findAll(By.cssSelector("table")).isEmpty();
        boolean hasGridRole = !findAll(By.cssSelector("[role='grid']")).isEmpty();
        boolean hasMuiGrid = !findAll(By.cssSelector("[class*='MuiDataGrid']")).isEmpty();
        boolean hasNoCategoryText = containsText("No category found");

        // If table exists, also ensure it is visible
        if (hasTable) {
            WebElementFacade table = find(By.cssSelector("table"));
            return table.isVisible();
        }

        return hasGridRole || hasMuiGrid || hasNoCategoryText;
    }

    public boolean isPaginationVisible() {
        // Works for Bootstrap + MUI + other common pagination controls
        return !findAll(By.cssSelector(
                ".pagination, ul.pagination, nav[aria-label*='pagination'], [class*='Pagination'], [class*='TablePagination']"))
                .isEmpty();
    }
}
