package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import com.OnTerminal.utils.SoftAssertionCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
        // - grid exists OR
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

        // Find search input using multiple possible selectors with wait
        WebElementFacade searchInput = find(By.name("name"));

        searchInput.clear();
        searchInput.type(keyword);
        searchInput.sendKeys(org.openqa.selenium.Keys.ENTER);

        // Click the Search button using XPath
        WebElementFacade searchButton = findFirstPresentWithWait(
                By.cssSelector("button.btn.btn-primary[type='submit']"));
        searchButton.click();
        System.out.println("Clicked Search button after entering keyword: " + keyword);

        // Wait for search results to load
        waitABit(2000);
    }

    public boolean containsCategoryName(String categoryName) {
        // Final wait and check
        waitABit(2000);
        System.out.println("Checking if results contain: " + categoryName);

        boolean found = containsText(categoryName);

        if (!found) {
            System.out.println("Text not found. Refreshing results check...");
            // Try specific table check
            found = findAll(By.xpath("//td[contains(text(), '" + categoryName + "')]")).size() > 0;
        }

        System.out.println("Verification result for " + categoryName + ": " + found);
        return found;
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
            if (filterElement.getTagName().equalsIgnoreCase("select")) {
                filterElement.selectByVisibleText(parentCategory);
            } else {
                throw new RuntimeException("Not a select element");
            }
            System.out.println("Selected parent category from dropdown: " + parentCategory);
        } catch (Exception e) {
            // If it's not a select element, try clicking and typing
            System.out.println("Not a select element or select failed, trying alternative approach");
            try {
                filterElement.click();
            } catch (Exception clickEx) {
                // Ignore click error
            }
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

        // Click the Search button to apply the filter
        WebElementFacade searchButton = findFirstPresentWithWait(
                By.cssSelector("button.btn.btn-primary[type='submit']"),
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(text(), 'Search')]"));

        searchButton.click();
        System.out.println("Clicked Search button to apply parent category filter");

        // Wait for filter to apply
        waitABit(1500);
    }

    public boolean showsOnlyChildrenOf(String parentCategory) {
        // Wait for results to update
        waitABit(1000);

        System.out.println("Verifying results show only children of: " + parentCategory);

        // Assuming structure: ID | Name | Parent Category
        // Column indices: 0 = ID, 1 = Name, 2 = Parent Category
        int parentColIndex = 2;
        int nameColIndex = 1;

        // Get all visible rows in the table or grid
        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));

        if (rows.isEmpty()) {
            System.out.println("No results found after filtering");
            return false;
        }

        System.out.println("Found " + rows.size() + " rows after filtering");

        int matchingRows = 0;
        int totalDataRows = 0;
        boolean emptyStateDetected = false;

        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));

            String rowText = row.getText().trim().toLowerCase();
            if (rowText.contains("no category found") || rowText.contains("no categories found")
                    || rowText.contains("no data") || rowText.contains("no rows")) {
                emptyStateDetected = true;
                System.out.println("Empty-state row detected, skipping data verification for it");
                continue;
            }

            // Skip rows that clearly are not data rows
            if (cells.isEmpty() || cells.size() <= parentColIndex) {
                continue;
            }

            totalDataRows++;

            String categoryName = (cells.size() > nameColIndex) ? cells.get(nameColIndex).getText().trim()
                    : "(unknown)";
            String parentInRow = cells.get(parentColIndex).getText().trim();

            System.out.println("Row " + totalDataRows + ": Category='" + categoryName + "', Parent='" + parentInRow
                    + "'");

            boolean matches = parentInRow.equalsIgnoreCase(parentCategory) || parentInRow.equals(parentCategory)
                    || parentInRow.toLowerCase().contains(parentCategory.toLowerCase());

            if (matches) {
                matchingRows++;
                System.out.println("Matches filter (parent: " + parentInRow + ")");
            } else {
                System.out.println("Does NOT match filter! Expected parent: '" + parentCategory + "', but got: '"
                        + parentInRow + "'");
            }
        }

        if (totalDataRows == 0 && emptyStateDetected) {
            System.out.println("Filter applied but UI shows empty state for parent '" + parentCategory
                    + "'. Treating as valid (no children to display).");
            return true;
        }

        if (totalDataRows == 0) {
            System.out.println("No data rows found to verify and no empty state detected");
            return false;
        }

        System.out.println("Filter verification: " + matchingRows + " out of " + totalDataRows + " rows match parent '"
                + parentCategory + "'");

        boolean allMatch = matchingRows == totalDataRows;

        if (!allMatch) {
            System.out.println("FILTER FAILED: Not all rows have parent '" + parentCategory + "'");
        } else {
            System.out.println("FILTER PASSED: All rows are children of '" + parentCategory + "'");
        }

        return allMatch;
    }

    public void clickAddCategory() {
        // Check current URL and navigate if needed
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("Current URL before clicking Add: " + currentUrl);

        // If we're on an add/edit form, navigate back to the list
        if (currentUrl.contains("/add") || currentUrl.contains("/edit")) {
            System.out.println("Currently on add/edit page, navigating back to categories list...");
            openCategories();
            waitABit(2000);
        } else if (!currentUrl.contains("/ui/categories")) {
            System.out.println("Not on categories page, navigating before clicking Add...");
            openCategories();
            waitABit(2000);
        }

        // Try to dismiss any modals/alerts that might be blocking the button
        try {
            List<WebElementFacade> modals = findAll(
                    By.cssSelector(".modal, .alert, [role='dialog'], .toast, .notification"));
            for (WebElementFacade modal : modals) {
                if (modal.isVisible()) {
                    System.out.println("Found visible modal/alert, attempting to dismiss...");
                    // Try to find and click close/dismiss button
                    List<WebElement> closeButtons = modal.findElements(By
                            .cssSelector("button.close, .btn-close, button[aria-label='Close'], button[data-dismiss]"));
                    if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
                        closeButtons.get(0).click();
                        waitABit(500);
                        System.out.println("Dismissed modal/alert");
                    }
                }
            }
        } catch (Exception e) {
            // Ignore if no modals found
            System.out.println("No modals to dismiss");
        }

        // Wait for page to be ready
        waitABit(1500);

        System.out.println("Attempting to click Add Category button");

        // Find Add/Create button - OPTIMIZED based on actual HTML structure
        // The actual element is: <a href="/ui/categories/add" class="btn
        // btn-primary">Add A Category</a>
        WebElementFacade addButton = findFirstPresentWithWait(
                // Prioritize the actual structure first (fastest)
                By.cssSelector("a.btn.btn-primary[href*='/categories/add']"),
                By.xpath("//a[contains(@href, '/categories/add') and contains(text(), 'Add A Category')]"),
                By.xpath("//a[contains(text(), 'Add A Category')]"),
                By.cssSelector("a[href*='/categories/add']"),
                // Fallback to button selectors (in case UI changes)
                By.xpath("//button[contains(text(), 'Add A Category')]"),
                By.xpath("//button[contains(text(), 'Add a Category')]"),
                By.xpath("//button[text()='Add A Category']"),
                By.xpath("//button[contains(., 'Add A Category')]"),
                // Generic fallbacks (last resort)
                By.cssSelector("button[id*='add'], a[id*='add']"),
                By.cssSelector("button[class*='add'], a[class*='add']"),
                By.xpath("//button[contains(text(), 'Add')] | //a[contains(text(), 'Add')]"));

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
                By.cssSelector("input[type='text']")); // Removed broad input selector

        nameInput.clear();
        nameInput.type(categoryName);
        System.out.println("Entered category name: " + categoryName);
    }

    public void leaveParentCategoryEmpty() {
        System.out.println("Leaving parent category empty");

        // Find the parent category dropdown
        WebElementFacade parentSelect = findFirstPresentWithWait(
                By.cssSelector("select[name='parentCategory']"),
                By.cssSelector("select[id*='parent']"),
                By.cssSelector("select[id*='Parent']"),
                By.cssSelector("select[class*='parent']"),
                By.cssSelector("select[class*='Parent']"),
                By.cssSelector("[role='combobox']"),
                By.cssSelector("select"));

        // Try to select the first option (often "Select Parent", "None", or empty)
        try {
            if (parentSelect.getTagName().equalsIgnoreCase("select")) {
                parentSelect.selectByIndex(0);
                System.out.println("Selected first option from parent dropdown (assuming empty/none)");
            } else {
                if (parentSelect.getTagName().equalsIgnoreCase("input")) {
                    parentSelect.clear();
                    System.out.println("Cleared parent input");
                }
            }
        } catch (Exception e) {
            System.out.println("Could not select/clear parent category: " + e.getMessage());
        }
    }

    public void clickCancel() {
        System.out.println("Attempting to click Cancel button");

        WebElementFacade cancelButton = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Cancel')]"),
                By.xpath("//button[contains(text(), 'cancel')]"),
                By.xpath("//a[contains(text(), 'Cancel')]"),
                By.cssSelector("button[class*='cancel']"),
                By.cssSelector("button[id*='cancel']"),
                By.xpath("//button[contains(., 'Cancel')]"));

        System.out.println("Found cancel button: " + cancelButton.getText());
        cancelButton.click();
        waitABit(1500);
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

        // Ensure we're back on the categories list page
        String currentUrl = getDriver().getCurrentUrl();
        if (currentUrl.contains("/add") || currentUrl.contains("/edit")) {
            System.out.println("Still on add/edit page after save, navigating back to list...");
            openCategories();
            waitABit(2000);
        }
    }

    public boolean categoryAppearsInList(String categoryName) {
        // Wait for save operation to complete
        waitABit(2000);

        System.out.println("Checking if category appears in list: " + categoryName);
        System.out.println("Current URL before check: " + getDriver().getCurrentUrl());

        // If we're not on the categories list page (but maybe on add/edit page),
        // navigate to it
        String currentUrl = getDriver().getCurrentUrl();
        if (!currentUrl.endsWith("/ui/categories") && !currentUrl.endsWith("/ui/categories/")) {
            System.out.println("Not on categories list page (Current: " + currentUrl + "), navigating...");
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
            // Print page source for debugging
            // System.out.println(getDriver().getPageSource());
        }

        return appears;
    }

    public boolean isMainCategory(String categoryName) {
        System.out.println("Verifying if category is a main category: " + categoryName);

        // Ensure we're on the list page before searching
        if (!getDriver().getCurrentUrl().contains("/ui/categories") || getDriver().getCurrentUrl().contains("/add")) {
            System.out.println("Not on list page, navigating to categories list...");
            openCategories();
        }

        // Search for the category first to make sure it's on the current page
        searchByKeyword(categoryName);
        waitABit(2000);

        // Iterate through rows to find the category and check its parent column
        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        System.out.println("Rows found after search: " + rows.size());

        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));
            if (cells.size() >= 3) {
                String name = cells.get(1).getText().trim();
                String parent = cells.get(2).getText().trim();

                System.out.println("Checking row - Name: '" + name + "', Parent: '" + parent + "'");

                if (name.equalsIgnoreCase(categoryName)) {
                    System.out.println("Found match! Checking parent value...");
                    // Main category usually has '-', empty, 'None', 'No Parent' or 'root'
                    boolean isMain = parent.isEmpty() ||
                            parent.equals("-") ||
                            parent.equalsIgnoreCase("None") ||
                            parent.equalsIgnoreCase("No Parent") ||
                            parent.equalsIgnoreCase("root");

                    System.out.println("Is main category? " + isMain);
                    return isMain;
                }
            }
        }

        System.out.println("Category '" + categoryName + "' not found in table after search.");
        return false;
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

    public void openEditCategoryPage(String categoryId) {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) {
            base = "http://localhost:8080";
        }

        String url = base.endsWith("/") ? base + "ui/categories/edit/" + categoryId
                : base + "/ui/categories/edit/" + categoryId;

        System.out.println("Attempting to access edit category page directly: " + url);
        openUrl(url);
        waitABit(2000);
        System.out.println("Current URL after direct access: " + getDriver().getCurrentUrl());
    }

    public String getFirstCategoryId() {
        if (!getDriver().getCurrentUrl().contains("/ui/categories")) {
            openCategories();
        }

        waitABit(1500);

        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        for (WebElementFacade row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));
            if (!cells.isEmpty()) {
                String idText = cells.get(0).getText().trim();
                if (!idText.isEmpty() && idText.matches("\\d+")) {
                    System.out.println("Found category ID: " + idText);
                    return idText;
                }
            }
        }

        System.out.println("No category ID found from list");
        return null;
    }

    public boolean isAccessDeniedPage() {
        System.out.println("Checking for access denied indicators...");
        waitABit(1000);
        boolean isAccessDenied = containsText("Access Denied") ||
                containsText("Forbidden") ||
                containsText("403") ||
                getDriver().getTitle().contains("Access Denied") ||
                getDriver().getCurrentUrl().contains("403");

        if (isAccessDenied) {
            System.out.println("Access denied page detected - CORRECT");
        } else {
            System.out.println("Access denied page NOT detected");
        }

        return isAccessDenied;
    }

    public boolean isAtCategoriesList() {
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("Checking if at categories list. Current URL: " + currentUrl);
        // List page usually ends with /ui/categories
        return (currentUrl.endsWith("/ui/categories") || currentUrl.endsWith("/ui/categories/"))
                && isTableVisible();
    }

    /**
     * Check if no results/empty state is displayed after search
     */
    public boolean isNoResultsDisplayed(String expectedMessage) {
        waitABit(1000);

        // Check for empty state row with colspan and text-center class
        List<WebElementFacade> emptyStateRows = findAll(By.cssSelector("table tbody tr td.text-center.text-muted"));
        System.out.println("[CategoriesPage] Found " + emptyStateRows.size() + " potential empty state rows");

        for (WebElementFacade cell : emptyStateRows) {
            String cellText = cell.getText().trim().toLowerCase();
            System.out.println("[CategoriesPage] Found empty state cell: " + cellText);

            if (cellText.contains("no") && (cellText.contains("found") || cellText.contains("category"))) {
                System.out.println("[CategoriesPage] Empty state message found: " + cellText);
                return true;
            }
        }

        System.out.println("[CategoriesPage] No empty/no-results state detected");
        return false;
    }

    public void sortBy(String columnName) {
        waitABit(1000);
        System.out.println("Sorting by column: " + columnName);

        // Log all available headers for debugging
        List<WebElementFacade> allHeaders = findAll(By.cssSelector("th, [role='columnheader']"));
        System.out.println("Available headers: ");
        for (WebElementFacade h : allHeaders) {
            System.out.println("- " + h.getText());
        }

        String lowerColumn = columnName.toLowerCase();
        // XPath 1.0 case-insensitive text check
        String xpathCaseInsensitive = "translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";

        // Try to find header - specifically look for sortable indicators or buttons
        // inside
        WebElementFacade header = findFirstPresentWithWait(
                By.xpath("//th[contains(" + xpathCaseInsensitive + ", '" + lowerColumn + "')]"),
                By.xpath(
                        "//th[contains(" + xpathCaseInsensitive + ", '" + lowerColumn.replace(" category", "") + "')]"),
                By.xpath("//div[@role='columnheader' and contains(" + xpathCaseInsensitive + ", '" + lowerColumn
                        + "')]"),
                By.xpath("//div[@role='columnheader' and contains(" + xpathCaseInsensitive + ", '"
                        + lowerColumn.replace(" category", "") + "')]"),
                By.xpath("//span[contains(" + xpathCaseInsensitive + ", '" + lowerColumn + "')]"));

        System.out.println("Found header: " + header.getText());

        // Helper to get clickable from header
        By innerSelector = By.cssSelector("a, button, .sort-icon, [role='button'], span.MuiTableSortLabel-root");

        List<org.openqa.selenium.WebElement> innerClickables = header.findElements(innerSelector);
        if (!innerClickables.isEmpty()) {
            System.out.println("Clicking inner element for sorting");
            innerClickables.get(0).click();
            waitABit(1500);

            // Re-find because the first click might refresh the page/table
            try {
                // Simple re-find
                header = find(By.xpath("//th[contains(" + xpathCaseInsensitive + ", '" + lowerColumn + "')]"));
                innerClickables = header.findElements(innerSelector);
                if (!innerClickables.isEmpty()) {
                    innerClickables.get(0).click();
                } else {
                    header.click();
                }
            } catch (Exception e) {
                System.out.println("Could not perform second click (probably okay if sorted): " + e.getMessage());
            }
        } else {
            header.click();
            waitABit(1500);
            try {
                header = find(By.xpath("//th[contains(" + xpathCaseInsensitive + ", '" + lowerColumn + "')]"));
                header.click();
            } catch (Exception e) {
                // Ignore
            }
        }

        waitABit(2500); // Increased wait
    }

    public boolean isSortedByID() {
        System.out.println("Verifying if sorted by ID...");

        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        List<Integer> ids = new ArrayList<>();

        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));
            if (!cells.isEmpty()) {
                String text = cells.get(0).getText().trim();
                try {
                    ids.add(Integer.parseInt(text));
                } catch (Exception e) {
                }
            }
        }

        System.out.println("IDs found: " + ids);
        return isNumericListSorted(ids);
    }

    public boolean isSortedByName() {
        System.out.println("Verifying if sorted by Name...");

        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        List<String> names = new ArrayList<>();

        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));
            if (cells.size() > 1) {
                names.add(cells.get(1).getText().trim().toLowerCase());
            }
        }

        System.out.println("Names found: " + names);
        return isStringListSorted(names);
    }

    private boolean isNumericListSorted(List<Integer> list) {
        if (list.size() < 2)
            return true;
        boolean ascending = true;
        boolean descending = true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1))
                ascending = false;
            if (list.get(i) < list.get(i + 1))
                descending = false;
        }
        return ascending || descending;
    }

    public boolean isGroupedByParent() {
        System.out.println("Verifying if grouped/sorted by Parent Category...");

        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        List<String> parents = new ArrayList<>();

        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.cssSelector("td, [role='cell']"));
            if (cells.size() > 2) {
                parents.add(cells.get(2).getText().trim().toLowerCase());
            }
        }

        System.out.println("Parents found: " + parents);
        return isStringListSorted(parents);
    }

    private boolean isStringListSorted(List<String> list) {
        if (list.size() < 2)
            return true;
        boolean ascending = true;
        boolean descending = true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0)
                ascending = false;
            if (list.get(i).compareTo(list.get(i + 1)) < 0)
                descending = false;
        }
        return ascending || descending;
    }

    private int getColumnIndexByHeaderKeyword(String keyword, int defaultIndex) {
        List<WebElementFacade> headers = findAll(By.cssSelector("th, [role='columnheader']"));

        for (int i = 0; i < headers.size(); i++) {
            String headerText = headers.get(i).getText().trim().toLowerCase();
            if (headerText.contains(keyword.toLowerCase())) {
                return i;
            }
        }

        return defaultIndex;
    }

    public boolean isValidationErrorVisible() {
        // Detects common validation error indicators in different UI frameworks
        return !findAll(
                By.cssSelector(".invalid-feedback, .text-danger, .alert-danger, .error-message, .alert-warning"))
                .isEmpty() ||
                getDriver().getPageSource().contains("already exists") ||
                getDriver().getPageSource().contains("must not be empty");
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
        // Try each locator with a SHORT wait (2 seconds instead of default 10+)
        for (By by : locators) {
            try {
                // Use withTimeoutOf to limit wait time to 2 seconds
                WebElementFacade element = find(by).withTimeoutOf(java.time.Duration.ofSeconds(2));
                if (element.isPresent() && element.isVisible()) {
                    System.out.println("[PERFORMANCE] Found element using locator: " + by);
                    return element;
                }
            } catch (Exception e) {
                // Continue to next locator - this is expected when element not found
            }
        }

        // Fallback - try again without visibility check using shorter timeout
        for (By by : locators) {
            try {
                List<WebElementFacade> elements = findAll(by);
                if (!elements.isEmpty()) {
                    WebElementFacade element = elements.get(0).withTimeoutOf(java.time.Duration.ofSeconds(1));
                    if (element.isPresent()) {
                        System.out.println("[PERFORMANCE] Found element (fallback) using locator: " + by);
                        return element;
                    }
                }
            } catch (Exception e) {
                // Continue to next locator
            }
        }

        // Last resort - return first locator (will fail with clear error)
        System.out.println("[WARNING] No element found with any locator, returning first locator (will likely fail)");
        return find(locators[0]);
    }

    public void verifyActiveMenuItem(String menuName) {
        waitABit(1000);

        System.out.println("Verifying active menu item for: " + menuName);

        try {
            WebElementFacade menuItem = findFirstPresentWithWait(
                    By.xpath("//a[contains(@href, '/ui/" + menuName + "')]"));

            String className = menuItem.getAttribute("class");
            String ariaCurrent = menuItem.getAttribute("aria-current");
            boolean isActive = (className != null && className.contains("active"))
                    || ("page".equalsIgnoreCase(ariaCurrent));

            System.out.println("Menu item '" + menuName + "' class: " + className);
            System.out.println("Menu item '" + menuName + "' aria-current: " + ariaCurrent);
            System.out.println("Menu item '" + menuName + "' active: " + isActive);

            String message = "Menu item for " + menuName + " should be highlighted/active"
                    + " (class=" + className + ", aria-current=" + ariaCurrent + ")";
            SoftAssertionCollector.checkTrue(message, isActive);
        } catch (Exception e) {
            String message = "Menu item for " + menuName + " should be highlighted/active"
                    + " (error: " + e.getMessage() + ")";
            SoftAssertionCollector.checkTrue(message, false);
        }
    }

    public boolean isMessageDisplayed(String expectedMessage) {
        waitABit(1000);
        System.out.println("[CategoriesPage] Checking for message: " + expectedMessage);

        // Check if the message appears anywhere on the page
        boolean messageFound = containsText(expectedMessage);

        // Also check for "No category found" message specifically
        if (expectedMessage.toLowerCase().contains("found")) {
            messageFound = messageFound || isNoResultsDisplayed(expectedMessage);
        }

        System.out.println("[CategoriesPage] Message '" + expectedMessage + "' found: " + messageFound);
        return messageFound;
    }

    // ==================== Security Verification Methods ====================

    /**
     * Verifies that Edit buttons are NOT visible for regular users
     * This is a SECURITY check - regular users should not have edit access
     */
    public boolean areEditButtonsNotVisible() {
        waitABit(2000);
        System.out.println("[SECURITY CHECK] Verifying Edit buttons are NOT visible to regular user");

        // 1. Check for standard Edit text buttons/links
        List<WebElementFacade> editButtons = findAll(By.xpath(
                "//button[contains(translate(text(), 'EDIT', 'edit'), 'edit')] | " +
                        "//a[contains(translate(text(), 'EDIT', 'edit'), 'edit')] | " +
                        "//*[contains(@class, 'edit')] | " +
                        "//*[contains(@id, 'edit')] | " +
                        "//*[contains(@title, 'Edit')] | " +
                        "//*[contains(@aria-label, 'Edit')]"));

        // 2. Check all buttons/links in the table for icons that look like Edit
        // (pencil, etc.)
        List<WebElementFacade> tableIcons = findAll(By.cssSelector("table i, .table i, table svg, .table svg"));
        for (WebElementFacade icon : tableIcons) {
            String html = icon.getAttribute("outerHTML").toLowerCase();
            if (html.contains("edit") || html.contains("pencil") || html.contains("fa-edit")
                    || html.contains("fa-pencil") || html.contains("write")) {
                editButtons.add(icon);
            }
        }

        // 3. Broad search for ANY button/link in the data rows (usually users shouldn't
        // see any)
        List<WebElementFacade> dataRowActions = findAll(By.cssSelector("table tbody tr button, table tbody tr a"));
        for (WebElementFacade action : dataRowActions) {
            String text = action.getText().toLowerCase();
            if (text.contains("edit")) {
                editButtons.add(action);
            }
        }

        if (editButtons.isEmpty()) {
            System.out.println("[PASS] No Edit buttons/indicators found in the DOM.");
            return true;
        }

        int visibleCount = 0;
        for (WebElementFacade btn : editButtons) {
            if (btn.isCurrentlyVisible()) {
                visibleCount++;
                String desc = btn.getText().isEmpty() ? btn.getAttribute("outerHTML") : btn.getText();
                System.out.println("[FAIL] SECURITY BUG: Possible Edit button IS visible - " + desc);
            }
        }

        if (visibleCount > 0) {
            System.out.println("[FAIL] Found " + visibleCount + " visible Edit-related elements - SECURITY BUG!");
            return false;
        }

        System.out.println("[PASS] Edit-related elements are either missing or hidden - correct");
        return true;
    }

    /**
     * Verifies that Delete buttons are NOT visible for regular users
     * This is a SECURITY check - regular users should not have delete access
     */
    public boolean areDeleteButtonsNotVisible() {
        waitABit(2000);
        System.out.println("[SECURITY CHECK] Verifying Delete buttons are NOT visible to regular user");

        // 1. Check for standard Delete text buttons/links
        List<WebElementFacade> deleteButtons = new java.util.ArrayList<>(findAll(By.xpath(
                "//button[contains(translate(text(), 'DELETE', 'delete'), 'delete')] | " +
                        "//a[contains(translate(text(), 'DELETE', 'delete'), 'delete')] | " +
                        "//*[contains(@class, 'delete')] | " +
                        "//*[contains(@class, 'remove')] | " +
                        "//*[contains(@class, 'trash')] | " +
                        "//*[contains(@id, 'delete')] | " +
                        "//*[contains(@title, 'Delete')] | " +
                        "//*[contains(@aria-label, 'Delete')]")));

        // 2. Check all icons in the table for trash cans, etc.
        List<WebElementFacade> tableIcons = findAll(By.cssSelector("table i, .table i, table svg, .table svg"));
        for (WebElementFacade icon : tableIcons) {
            String html = icon.getAttribute("outerHTML").toLowerCase();
            if (html.contains("delete") || html.contains("trash") || html.contains("remove")
                    || html.contains("fa-trash") || html.contains("fa-remove")) {
                deleteButtons.add(icon);
            }
        }

        // 3. Check for buttons with 'danger' or 'red' classes in the table which are
        // often delete buttons
        List<WebElementFacade> dangerButtons = findAll(
                By.cssSelector("table .btn-danger, table .text-danger, table [class*='danger']"));
        deleteButtons.addAll(dangerButtons);

        if (deleteButtons.isEmpty()) {
            System.out.println("[PASS] No Delete buttons/indicators found in the DOM.");
            return true;
        }

        int visibleCount = 0;
        for (WebElementFacade btn : deleteButtons) {
            if (btn.isCurrentlyVisible()) {
                visibleCount++;
                String desc = btn.getText().isEmpty() ? "Element with icon/class" : btn.getText();
                System.out.println("[FAIL] SECURITY BUG: Possible Delete button IS visible - " + desc);
            }
        }

        if (visibleCount > 0) {
            System.out.println("[FAIL] Found " + visibleCount + " visible Delete-related elements - SECURITY BUG!");
            return false;
        }

        System.out.println("[PASS] Delete-related elements are either missing or hidden - correct");
        return true;
    }

}
