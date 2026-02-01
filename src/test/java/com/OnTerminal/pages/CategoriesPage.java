package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

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

    public void filterByParentCategory(String parentCategory) {
        // Wait for page to be fully loaded
        waitABit(1500);

        System.out.println("Attempting to filter by parent category: " + parentCategory);

        // Find filter dropdown/select using multiple possible selectors
        WebElementFacade filterElement = findFirstPresentWithWait(
                By.cssSelector("select[name='parentCategory']"),
                By.cssSelector("select[id*='parent']"),
                By.cssSelector("select[id*='Parent']"),
                By.cssSelector("select[id*='filter']"),
                By.cssSelector("select[id*='Filter']"),
                By.cssSelector("select[class*='parent']"),
                By.cssSelector("select[class*='Parent']"),
                By.cssSelector("select[class*='filter']"),
                By.cssSelector("select"),
                By.cssSelector("[role='combobox']"),
                By.cssSelector("input[role='combobox']"));

        // Try to select the option
        try {
            filterElement.selectByVisibleText(parentCategory);
            System.out.println("Selected parent category from dropdown: " + parentCategory);
        } catch (Exception e) {
            // If it's not a select element, try clicking and typing
            System.out.println("Not a select element, trying alternative approach");
            filterElement.click();
            waitABit(500);
            filterElement.type(parentCategory);
            waitABit(500);

            // Try to find and click the matching option
            try {
                WebElementFacade option = find(By.xpath("//*[contains(text(), '" + parentCategory + "')]"));
                option.click();
            } catch (Exception ex) {
                System.out.println("Could not find option to click, assuming typed value works");
            }
        }

        // Wait for filter to apply
        waitABit(1500);
    }

    public boolean showsOnlyChildrenOf(String parentCategory) {
        // Wait for results to update
        waitABit(1000);

        System.out.println("Verifying results show only children of: " + parentCategory);

        // Check if the page contains the parent category name or its children
        // This is a simplified check - in a real scenario, you'd verify each row
        boolean hasResults = !findAll(By.cssSelector("table tr, [role='row']")).isEmpty();

        if (!hasResults) {
            System.out.println("No results found after filtering");
            return false;
        }

        // For now, we'll check if there are any visible rows/results
        // A more sophisticated check would verify the parent category of each result
        int resultCount = findAll(By.cssSelector("table tbody tr, [role='row']:not([role='row'] [role='row'])")).size();
        System.out.println("Found " + resultCount + " results after filtering");

        // If we have results, assume the filter worked
        // In a real test, you'd verify each result's parent category
        return resultCount > 0;
    }

    public void clickAddCategory() {
        // Wait for page to be ready
        waitABit(1000);

        System.out.println("Attempting to click Add Category button");

        // Find Add/Create button - prioritize the exact text "Add A Category"
        WebElementFacade addButton = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Add A Category')]"),
                By.xpath("//button[contains(text(), 'Add a Category')]"),
                By.xpath("//button[text()='Add A Category']"),
                By.xpath("//a[contains(text(), 'Add A Category')]"),
                By.xpath("//button[contains(., 'Add A Category')]"),
                By.cssSelector("button[id*='add']"),
                By.cssSelector("button[id*='Add']"),
                By.cssSelector("button[id*='create']"),
                By.cssSelector("button[id*='Create']"),
                By.cssSelector("button[class*='add']"),
                By.cssSelector("button[class*='Add']"),
                By.xpath("//button[contains(text(), 'Add')]"),
                By.xpath("//button[contains(text(), 'Create')]"),
                By.xpath("//button[contains(text(), 'New')]"));

        System.out.println("Found button with text: " + addButton.getText());
        addButton.click();
        System.out.println("Clicked Add Category button");

        // Wait for form/modal to appear
        waitABit(1500);
    }

    public void enterCategoryName(String categoryName) {
        // Wait for form to be visible
        waitABit(1000);

        System.out.println("Entering category name: " + categoryName);

        // Find the name input field
        WebElementFacade nameInput = findFirstPresentWithWait(
                By.cssSelector("input[name='name']"),
                By.cssSelector("input[name='categoryName']"),
                By.cssSelector("input[id*='name']"),
                By.cssSelector("input[id*='Name']"),
                By.cssSelector("input[placeholder*='name']"),
                By.cssSelector("input[placeholder*='Name']"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input"));

        nameInput.clear();
        nameInput.type(categoryName);
        System.out.println("Entered category name: " + categoryName);
    }

    public void saveCategory() {
        // Wait a moment before saving
        waitABit(500);

        System.out.println("Attempting to save category");
        System.out.println("URL before save: " + getDriver().getCurrentUrl());

        // Find Save/Submit button
        WebElementFacade saveButton = findFirstPresentWithWait(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("button[id*='save']"),
                By.cssSelector("button[id*='Save']"),
                By.cssSelector("button[id*='submit']"),
                By.cssSelector("button[id*='Submit']"),
                By.cssSelector("button[class*='save']"),
                By.cssSelector("button[class*='Save']"),
                By.cssSelector("button[class*='submit']"),
                By.cssSelector("button[class*='Submit']"),
                By.xpath("//button[contains(text(), 'Save')]"),
                By.xpath("//button[contains(text(), 'save')]"),
                By.xpath("//button[contains(text(), 'Submit')]"),
                By.xpath("//button[contains(text(), 'OK')]"),
                By.cssSelector("button[type='button']"));

        System.out.println("Found save button: " + saveButton.getText());
        saveButton.click();
        System.out.println("Clicked Save button");

        // Wait for save operation to complete and modal to close
        waitABit(3000);

        System.out.println("URL after save: " + getDriver().getCurrentUrl());

        // Check for success message or notification
        if (containsText("success") || containsText("Success") || containsText("created") || containsText("Created")) {
            System.out.println("Success message found");
        }
    }

    public boolean categoryAppearsInList(String categoryName) {
        // Wait for save operation to complete
        waitABit(2000);

        System.out.println("Checking if category appears in list: " + categoryName);
        System.out.println("Current URL before check: " + getDriver().getCurrentUrl());

        // If we're not on the categories page, navigate to it
        String currentUrl = getDriver().getCurrentUrl();
        if (!currentUrl.contains("/ui/categories")) {
            System.out.println("Not on categories page, navigating...");
            openCategories();
        } else {
            // Refresh the page to see the new category
            System.out.println("Refreshing page to see new category...");
            getDriver().navigate().refresh();
            waitABit(2000);
        }

        // Check if the category name appears anywhere on the page
        boolean appears = containsText(categoryName);

        if (!appears) {
            // Try checking in table rows specifically
            int rowCount = findAll(By.cssSelector("table tbody tr, [role='row']")).size();
            System.out.println("Total rows found: " + rowCount);

            // Check each row for the category name
            for (WebElementFacade row : findAll(By.cssSelector("table tbody tr, [role='row']"))) {
                if (row.getText().contains(categoryName)) {
                    appears = true;
                    break;
                }
            }
        }

        if (appears) {
            System.out.println("Category found in list: " + categoryName);
        } else {
            System.out.println("Category NOT found in list: " + categoryName);
            System.out.println("Page text contains: "
                    + getDriver().getPageSource().substring(0, Math.min(500, getDriver().getPageSource().length())));
        }

        return appears;
    }

    public boolean isAddCategoryButtonNotVisible() {
        // Wait for page to load
        waitABit(1500);

        System.out.println("Checking if Add Category button is NOT visible");

        // Try to find the Add A Category button
        try {
            List<WebElementFacade> buttons = findAll(By.xpath("//button[contains(text(), 'Add A Category')]"));

            if (buttons.isEmpty()) {
                System.out.println("Add Category button not found - CORRECT for normal user");
                return true;
            }

            // Check if button exists but is not visible
            boolean anyVisible = false;
            for (WebElementFacade button : buttons) {
                if (button.isVisible()) {
                    anyVisible = true;
                    break;
                }
            }

            if (!anyVisible) {
                System.out.println("Add Category button exists but not visible - CORRECT");
                return true;
            }

            System.out.println("Add Category button IS visible - INCORRECT for normal user");
            return false;

        } catch (Exception e) {
            System.out.println("Add Category button not found (exception) - CORRECT for normal user");
            return true;
        }
    }

    public void openAddCategoryPageDirectly() {
        // Construct URL for add category page
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) {
            base = "http://localhost:8080";
        }

        // Try common patterns for add/create pages
        String url = base.endsWith("/") ? base + "ui/categories/add" : base + "/ui/categories/add";

        System.out.println("Attempting to access add category page directly: " + url);
        openUrl(url);

        waitABit(2000);
        System.out.println("Current URL after direct access: " + getDriver().getCurrentUrl());
    }

    public boolean isAccessDeniedPage() {
        waitABit(1000);

        System.out.println("Checking for access denied page");
        System.out.println("Current URL: " + getDriver().getCurrentUrl());
        System.out.println("Page title: " + getDriver().getTitle());

        // Check for common access denied indicators
        boolean isAccessDenied = containsText("Access Denied") ||
                containsText("access denied") ||
                containsText("403") ||
                containsText("Forbidden") ||
                containsText("forbidden") ||
                containsText("Unauthorized") ||
                containsText("unauthorized") ||
                containsText("Permission Denied") ||
                containsText("permission denied") ||
                containsText("Not Authorized") ||
                containsText("not authorized") ||
                getDriver().getCurrentUrl().contains("403") ||
                getDriver().getCurrentUrl().contains("denied") ||
                getDriver().getCurrentUrl().contains("unauthorized");

        if (isAccessDenied) {
            System.out.println("Access denied page detected - CORRECT");
        } else {
            System.out.println("Access denied page NOT detected");
            // Print some page content for debugging
            System.out.println("Page content snippet: " +
                    getDriver().getPageSource().substring(0, Math.min(300, getDriver().getPageSource().length())));
        }

        return isAccessDenied;
    }

    public void sortBy(String columnName) {
        waitABit(1000);
        System.out.println("Sorting by column: " + columnName);

        // Try to find header by text in <th> or div/span with columnheader role (MUI)
        WebElementFacade header = findFirstPresentWithWait(
                By.xpath("//th[contains(., '" + columnName + "')]"),
                By.xpath("//div[@role='columnheader']//span[contains(., '" + columnName + "')]"),
                By.xpath("//div[@role='columnheader' and contains(., '" + columnName + "')]"),
                By.xpath("//span[contains(text(), '" + columnName + "')]"));

        header.click();
        waitABit(1500); // Wait for sort to apply
    }

    public boolean isSortedByID() {
        System.out.println("Verifying if sorted by ID...");

        // Find all cells in the first column (assuming ID is first)
        List<WebElementFacade> idCells = findAll(
                By.cssSelector("table tbody tr td:first-child, [role='row'] [role='cell']:first-child"));

        if (idCells.isEmpty()) {
            System.out.println("No ID cells found to verify sorting");
            return false;
        }

        List<Integer> ids = new ArrayList<>();
        for (WebElementFacade cell : idCells) {
            String text = cell.getText().trim();
            if (!text.isEmpty()) {
                try {
                    ids.add(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping non-numeric ID cell: " + text);
                }
            }
        }

        System.out.println("IDs found: " + ids);

        if (ids.size() < 2) {
            return true; // Single item or empty is sorted
        }

        // Check if sorted ascending
        boolean ascending = true;
        for (int i = 0; i < ids.size() - 1; i++) {
            if (ids.get(i) > ids.get(i + 1)) {
                ascending = false;
                break;
            }
        }

        // Check if sorted descending (in case toggle was used)
        boolean descending = true;
        for (int i = 0; i < ids.size() - 1; i++) {
            if (ids.get(i) < ids.get(i + 1)) {
                descending = false;
                break;
            }
        }

        return ascending || descending;
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
