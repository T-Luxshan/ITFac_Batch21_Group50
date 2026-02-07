package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class PlantsPage extends PageObject {

    // locators

    // Page header
    private By plantsHeader = By.xpath("//h3[contains(text(), 'Plants')]");

    // Plant table
    private By plantTable = By.cssSelector("table.table.table-striped.table-bordered");
    private By tableRows = By.cssSelector("table.table-striped tbody tr");

    // Table columns
    private By nameColumn = By.xpath("//table//thead//th[contains(., 'Name')]");
    private By categoryColumn = By.xpath("//table//thead//th[contains(., 'Category')]");
    private By priceColumn = By.xpath("//table//thead//th[contains(., 'Price')]");
    private By stockColumn = By.xpath("//table//thead//th[contains(., 'Stock')]");

    // Add Plant button
    private By addPlantButton = By.xpath(
            "//a[contains(@href, '/ui/plants/add')] | //button[contains(text(), 'Add Plant')] | //a[contains(text(), 'Add Plant')]"
    );

    // Search/Filter
    private By nameFilterInput = By.name("name");
    private By searchButton = By.xpath("//button[contains(text(), 'Search')] | //button[@type='submit']");

    // Pagination
    private By nextPageButton = By.xpath("//a[contains(text(), 'Next')] | //a[@rel='next']");
    private By prevPageButton = By.xpath("//a[contains(text(), 'Previous')] | //a[@rel='prev']");

    // Delete modal
    private By deleteModal = By.id("deleteModal");
    private By plantNameInModal = By.id("plantName");

    // Buttons inside modal
    private By deleteConfirmButton = By.xpath(
            "//div[@id='deleteModal']//button[contains(text(), 'Delete')] | //form[@id='deleteForm']//button[@type='submit']"
    );
    private By deleteCancelButton = By.xpath(
            "//div[@id='deleteModal']//button[contains(text(), 'Cancel')] | //div[@id='deleteModal']//button[@data-bs-dismiss='modal']"
    );

    // Actions on first row
    private By firstRowEditButton = By.xpath(
            "(//a[contains(@href, '/ui/plants/edit/')])[1] | (//i[contains(@class, 'bi-pencil')]/..)[1]"
    );

    private By firstRowDeleteFallback = By.xpath(
            "(//form[contains(@action, '/ui/plants/delete/')]//button)[1] | (//i[contains(@class, 'bi-trash')]/..)[1]"
    );

    // Success/Error messages
    private By successMessage = By.cssSelector(".alert-success, .alert.alert-success");
    private By errorMessage = By.cssSelector(".alert-danger, .alert.alert-danger");

    // Access denied
    private By accessDeniedMessage = By.xpath(
            "//*[contains(text(), 'Access Denied') or contains(text(), 'access denied') or contains(text(), '403')]"
    );

  //page methods

    public void openPlantsPage() {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) base = "http://localhost:8080";
        String url = base.endsWith("/") ? base + "ui/plants" : base + "/ui/plants";
        openUrl(url);
        waitABit(1000);
    }

    public void navigateToUrl(String relativeUrl) {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        if (base == null || base.isBlank()) base = "http://localhost:8080";
        String fullUrl = base.endsWith("/") ? base + relativeUrl.replaceFirst("^/", "") : base + relativeUrl;
        getDriver().get(fullUrl);
        waitABit(1000);
    }

    // Public wrapper
    public void pause(long milliseconds) {
        waitABit(milliseconds);
    }

    // varification methods

    public boolean isPlantsHeaderVisible() {
        try {
            waitForCondition().withTimeout(Duration.ofSeconds(10))
                    .pollingEvery(Duration.ofMillis(500))
                    .until(d -> find(plantsHeader).isVisible());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPlantTableVisible() {
        return find(plantTable).isVisible();
    }

    public boolean isTableDisplayed() {
        waitForCondition().withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(500))
                .until(d -> find(plantTable).isVisible());
        return find(plantTable).isVisible();
    }

    public boolean hasNameColumn() { return find(nameColumn).isVisible(); }
    public boolean hasCategoryColumn() { return find(categoryColumn).isVisible(); }
    public boolean hasPriceColumn() { return find(priceColumn).isVisible(); }
    public boolean hasStockColumn() { return find(stockColumn).isVisible(); }

    public int getRowCount() {
        try {
            List<WebElementFacade> rows = findAll(tableRows);
            return rows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean hasPlantData() {
        return getRowCount() > 0;
    }

    // action of admin

    public boolean isAddPlantButtonVisible() {
        try {
            return find(addPlantButton).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddPlant() {
        find(addPlantButton).click();
        waitABit(1500);
    }

    public void clickEditOnFirstRow() {
        WebElementFacade editBtn = find(firstRowEditButton);

        editBtn.waitUntilClickable();
        evaluateJavascript("arguments[0].scrollIntoView({block:'center'});", editBtn);
        waitABit(300);

        try {
            editBtn.click();
        } catch (Exception e) {
            evaluateJavascript("arguments[0].click();", editBtn);
        }

        waitABit(1500);
    }

    public void clickDeleteOnFirstRow() {
        WebElementFacade deleteBtn = find(firstRowDeleteFallback);

        deleteBtn.waitUntilClickable();
        evaluateJavascript("arguments[0].scrollIntoView({block:'center'});", deleteBtn);
        waitABit(300);

        try {
            deleteBtn.click();
        } catch (Exception e) {
            evaluateJavascript("arguments[0].click();", deleteBtn);
        }

    }

    public boolean isDeleteModalVisible() {
        // Case 1: ALERT confirmation (your logs show this)
        if (isAlertPresent()) {
            System.out.println("Confirmation shown as ALERT (not modal).");
            return true;
        }

        try {
            WebElementFacade modal = find(deleteModal);
            if (!modal.isPresent()) return false;

            String cls = modal.getAttribute("class");
            String display = modal.getCssValue("display");

            System.out.println("Modal present: " + modal.isPresent());
            System.out.println("Modal class: " + cls);
            System.out.println("Modal display: " + display);

            return cls != null && cls.contains("show") && !"none".equalsIgnoreCase(display);
        } catch (Exception e) {
            System.out.println("Modal check error: " + e.getMessage());
            return false;
        }
    }

    public String getPlantNameInDeleteModal() {
        return find(plantNameInModal).getText();
    }

 //Confirm delete:
    public void confirmDelete() {
        if (isAlertPresent()) {
            acceptDeleteAlertIfPresent();
            waitABit(1000);
            return;
        }

        WebElementFacade confirmBtn = find(deleteConfirmButton);
        confirmBtn.waitUntilClickable();

        try {
            confirmBtn.click();
        } catch (Exception e) {
            evaluateJavascript("arguments[0].click();", confirmBtn);
        }

        waitABit(1500);
    }

    public void cancelDelete() {
        try {
            find(deleteCancelButton).click();
        } catch (Exception e) {
            // ignore
        }
        waitABit(500);
    }

    private boolean isAlertPresent() {
        try {
            getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private void acceptDeleteAlertIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(3));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Alert text: " + alert.getText());
            alert.accept();
            System.out.println("Alert accepted.");
        } catch (Exception ignored) {
            // no alert
        }
    }

    // search and filter

    public void searchByName(String name) {
        WebElementFacade nameInput = find(nameFilterInput);
        nameInput.clear();
        nameInput.type(name);

        try {
            find(searchButton).click();
        } catch (Exception e) {
            nameInput.sendKeys(Keys.ENTER);
        }
        waitABit(1500);
    }

    public void filterByName(String name) {
        searchByName(name);
    }

    public boolean isPlantInList(String plantName) {
        try {
            List<WebElementFacade> rows = findAll(tableRows);
            for (WebElementFacade row : rows) {
                if (row.getText().toLowerCase().contains(plantName.toLowerCase())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasSuccessMessage() {
        try {
            return find(successMessage).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            return find(successMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean hasErrorMessage() {
        try {
            return find(errorMessage).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    // control the access

    public boolean isAccessDenied() {
        try {
            String url = getDriver().getCurrentUrl().toLowerCase();
            if (url.contains("/ui/login")) {
                // Redirected to login = no permission
                return true;
            }
            if (find(accessDeniedMessage).isVisible()) {
                return true;
            }
            String pageSource = getDriver().getPageSource().toLowerCase();
            return pageSource.contains("access denied") || pageSource.contains("403");
        } catch (Exception e) {
            String url = getDriver().getCurrentUrl().toLowerCase();
            return url.contains("/ui/login");
        }
    }



    // pagination

    public boolean hasNextButton() {
        try {
            return find(nextPageButton).isVisible() && find(nextPageButton).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasPrevButton() {
        try {
            return find(prevPageButton).isVisible() && find(prevPageButton).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickNextPage() {
        find(nextPageButton).click();
        waitABit(1000);
    }

    public void clickPrevPage() {
        find(prevPageButton).click();
        waitABit(1000);
import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page Object for Plants Management pages
 * Uses ConfigManager and Constants for configuration values
 * Handles interactions with:
 * - Plants List Page (/ui/plants)
 * - Add Plant Page (/ui/plants/add)
 * - Edit Plant Page (/ui/plants/edit/{id})
 * Page Object Model + Modular Framework
 */
public class PlantsPage extends PageObject {

    private final ConfigManager config = ConfigManager.getInstance();
    private Map<String, Integer> stockSnapshot = new HashMap<>();

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Plants List page
     */
    public void openPlantsPage() {
        String url = config.getPlantsUrl();
        System.out.println("[PlantsPage] Navigating to Plants page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    /**
     * Navigate to Edit Plant page (Admin only)
     */
    public void openEditPlantPage(String plantId) {
        String url = config.getEditPlantUrl(plantId);
        System.out.println("[PlantsPage] Navigating to Edit Plant page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    // ==================== Stock Management ====================

    /**
     * Record stock quantity of first plant for later comparison
     */
    public void recordStockOfFirstPlant() {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(By.cssSelector("table"));

        if (!rows.isEmpty()) {
            WebElementFacade firstRow = rows.get(0);
            String plantName = getPlantNameFromRow(firstRow);
            int stock = getStockFromRow(firstRow);
            stockSnapshot.put(plantName, stock);
            System.out.println("Recorded stock for '" + plantName + "': " + stock);
        } else {
            System.out.println("No plants found in table!");
        }
    }

    /**
     * Get the name of the first plant in the list
     */
    public String getFirstPlantName() {
        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr"));
        if (!rows.isEmpty()) {
            return getPlantNameFromRow(rows.get(0));
        }
        return null;
    }

    /**
     * Get recorded stock value for a plant
     */
    public int getRecordedStock(String plantName) {
        return stockSnapshot.getOrDefault(plantName, -1);
    }

    /**
     * Get current stock value for a plant from the UI
     */
    public int getCurrentStock(String plantName) {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(By.cssSelector("table.table-striped tbody tr"));

        for (WebElementFacade row : rows) {
            String name = getPlantNameFromRow(row);
            if (name != null && name.contains(plantName)) {
                return getStockFromRow(row);
            }
        }
        return -1;
    }

    private String getPlantNameFromRow(WebElementFacade row) {
        List<WebElementFacade> cells = row.thenFindAll(By.tagName("td"));

        if (!cells.isEmpty()) {
            String name = cells.get(0).getText().trim();
            if (!name.isEmpty()) {
                return name;
            }
        }
        return null;
    }

    private int getStockFromRow(WebElementFacade row) {
        List<WebElementFacade> cells = row.thenFindAll(By.tagName("td"));

        // Stock is in the 4th column (index 3)
        if (cells.size() >= 4) {
            WebElementFacade stockCell = cells.get(3);

            // Try to get the stock value from span element first
            try {
                WebElementFacade spanElement = stockCell.thenFind("span");
                if (spanElement.isPresent()) {
                    String stockText = spanElement.getText().trim();
                    return Integer.parseInt(stockText.replaceAll("[^0-9]", ""));
                }
            } catch (Exception ignored) {
            }

            // Fallback: get text directly from cell
            try {
                String text = stockCell.getText().trim();
                return Integer.parseInt(text.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ignored) {
            }
        }

        return 0;
    }


    /**
     * Find a plant with low stock (< 5) for testing
     */
    public String findLowStockPlant() {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(By.cssSelector("table.table-striped tbody tr"));

        // First try to find plant with 1-4 stock (look for low stock badge)
        for (WebElementFacade row : rows) {
            try {
                // Check if row has "Low" badge indicating low stock
                WebElementFacade lowBadge = row.thenFind("td span.badge.bg-danger");
                if (lowBadge.isPresent() && lowBadge.getText().contains("Low")) {
                    String plantName = getPlantNameFromRow(row);
                    int stock = getStockFromRow(row);
                    stockSnapshot.put(plantName, stock);
                    System.out.println("Found low stock plant: '" + plantName + "' with stock: " + stock);
                    return plantName;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback: find plant with stock between 1-4
        for (WebElementFacade row : rows) {
            int stock = getStockFromRow(row);
            if (stock > 0 && stock < 5) {
                String plantName = getPlantNameFromRow(row);
                stockSnapshot.put(plantName, stock);
                System.out.println("Found low stock plant: '" + plantName + "' with stock: " + stock);
                return plantName;
            }
        }

        // If no low stock plant, use first available plant
        if (!rows.isEmpty()) {
            WebElementFacade firstRow = rows.get(0);
            String plantName = getPlantNameFromRow(firstRow);
            int stock = getStockFromRow(firstRow);
            stockSnapshot.put(plantName, stock);
            System.out.println("Using first plant: '" + plantName + "' with stock: " + stock);
            return plantName;
        }

        return null;
    }

    // ==================== Verification Methods ====================

    /**
     * Check if currently on Plants List page
     */
    public boolean isOnPlantsListPage() {
        String currentUrl = getDriver().getCurrentUrl();
        boolean result = currentUrl.contains("/ui/plants") &&
                        !currentUrl.contains("/add") &&
                        !currentUrl.contains("/edit");
        System.out.println("On Plants List page: " + result + " (URL: " + currentUrl + ")");
        return result;
    }

    // ==================== Add/Edit Plant Form Methods ====================

    /**
     * Click Add Plant button to navigate to add plant form
     */
    public void clickAddPlantButton() {
        WebElementFacade addBtn = findFirstPresent(
            By.cssSelector("a[href*='/plants/add']")
        );
        if (addBtn != null) {
            addBtn.click();
            waitABit(1000);
            System.out.println("[PlantsPage] Clicked Add Plant button");
        } else {
            System.out.println("[PlantsPage] Add Plant button not found!");
        }
    }

    /**
     * Enter plant name in the form
     */
    public void enterPlantName(String name) {
        WebElementFacade nameInput = findFirstPresent(
            By.id("name")
        );
        if (nameInput != null) {
            nameInput.clear();
            nameInput.type(name);
            System.out.println("[PlantsPage] Entered plant name: " + name);
        } else {
            System.out.println("[PlantsPage] Plant name input not found!");
        }
    }

    /**
     * Enter plant price in the form
     */
    public void enterPlantPrice(String price) {
        WebElementFacade priceInput = findFirstPresent(
            By.id("price")
        );
        if (priceInput != null) {
            priceInput.clear();
            priceInput.type(price);
            System.out.println("[PlantsPage] Entered plant price: " + price);
        } else {
            System.out.println("[PlantsPage] Plant price input not found!");
        }
    }

    /**
     * Click Cancel button to return to plants list
     */
    public void clickCancelButton() {
        WebElementFacade cancelBtn = findFirstPresent(
            By.cssSelector("a.btn.btn-secondary")
        );
        if (cancelBtn != null) {
            cancelBtn.click();
            waitABit(1000);
            System.out.println("[PlantsPage] Clicked Cancel button");
        } else {
            System.out.println("[PlantsPage] Cancel button not found!");
        }
    }

    /**
     * Check if a plant exists in the list by name
     */
    public boolean isPlantInList(String plantName) {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(By.cssSelector("table.table-striped.table-bordered tbody tr"));
        for (WebElementFacade row : rows) {
            String name = getPlantNameFromRow(row);
            if (name != null && name.equalsIgnoreCase(plantName)) {
                System.out.println("[PlantsPage] Found plant '" + plantName + "' in list");
                return true;
            }
        }
        System.out.println("[PlantsPage] Plant '" + plantName + "' NOT found in list");
        return false;
    }

    // ==================== Helper Methods ====================

    private WebElementFacade findFirstPresent(By... locators) {
        for (By by : locators) {
            try {
                WebElementFacade el = find(by);
                if (el.isPresent() && el.isVisible()) {
                    return el;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
}
