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
        System.out.println("Navigating to: " + url);
        openUrl(url);

        // Wait for page to be ready
        waitABit(2000);

        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("After navigation, current URL: " + currentUrl);

        if (!currentUrl.contains("/ui/categories")) {
            System.out.println("WARNING: Not on categories page! Expected /ui/categories but got: " + currentUrl);
        }
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

    public void searchByKeyword(String keyword) {
        // Wait for page to be fully loaded
        waitABit(2000);

        // Print debugging information
        System.out.println("Current URL: " + getDriver().getCurrentUrl());
        System.out.println("Page title: " + getDriver().getTitle());

        // Check if any input fields exist
        int inputCount = findAll(By.cssSelector("input")).size();
        System.out.println("Total input fields found: " + inputCount);

        if (inputCount == 0) {
            throw new AssertionError(
                    "No input fields found on the page. The search functionality may not be available.");
        }

        // Find search input using multiple possible selectors with wait
        WebElementFacade searchInput = null;
        try {
            searchInput = findFirstPresentWithWait(
                    By.cssSelector("input[type='search']"),
                    By.cssSelector("input[placeholder*='Search']"),
                    By.cssSelector("input[placeholder*='search']"),
                    By.cssSelector("input[name='search']"),
                    By.cssSelector("input[id*='search']"),
                    By.cssSelector("input[class*='search']"),
                    By.cssSelector("input[class*='Search']"),
                    By.cssSelector("[role='searchbox']"),
                    By.cssSelector("input[type='text']"),
                    By.cssSelector("input"));
        } catch (Exception e) {
            System.out.println("Failed to find search input. Available inputs:");
            findAll(By.cssSelector("input")).forEach(input -> {
                System.out.println("  - type: " + input.getAttribute("type") +
                        " | placeholder: " + input.getAttribute("placeholder") +
                        " | class: " + input.getAttribute("class"));
            });
            throw new AssertionError("Search input field not found on the page", e);
        }

        searchInput.clear();
        searchInput.type(keyword);

        // Wait a moment for search to process (debounce/filter)
        waitABit(1000);
    }

    public boolean containsCategoryName(String categoryName) {
        // Check if the category name appears in the table/grid
        // This works for both table rows and MUI DataGrid cells
        return containsText(categoryName);
    }

    private WebElementFacade findFirstPresent(By... locators) {
        for (By by : locators) {
            if (!findAll(by).isEmpty()) {
                WebElementFacade element = find(by);
                if (element.isPresent()) {
                    return element;
                }
            }
        }
        // Fallback to first locator (will fail loudly if nothing found)
        return find(locators[0]);
    }

    private WebElementFacade findFirstPresentWithWait(By... locators) {
        // Try each locator with a short wait
        for (By by : locators) {
            try {
                WebElementFacade element = find(by);
                element.waitUntilPresent();
                if (element.isPresent() && element.isVisible()) {
                    return element;
                }
            } catch (Exception e) {
                // Continue to next locator
            }
        }
        // Fallback - try again without visibility check
        for (By by : locators) {
            if (!findAll(by).isEmpty()) {
                WebElementFacade element = find(by);
                if (element.isPresent()) {
                    return element;
                }
            }
        }
        // Last resort - return first locator (will fail with clear error)
        return find(locators[0]);
    }
}
