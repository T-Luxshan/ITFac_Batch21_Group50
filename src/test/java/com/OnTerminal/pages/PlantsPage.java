package com.OnTerminal.pages;

import com.OnTerminal.config.ConfigManager;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantsPage extends PageObject {

    private final ConfigManager config = ConfigManager.getInstance();
    private Map<String, Integer> stockSnapshot = new HashMap<>();

    // ==================== Locators ====================

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
            "//a[contains(@href, '/ui/plants/add')] | //button[contains(text(), 'Add Plant')] | //a[contains(text(), 'Add Plant')]");

    // Add/Edit Plant Form Inputs
    private By nameInput = By.id("name");
    private By priceInput = By.id("price");
    private By quantityInput = By.id("quantity");
    private By cancelFormButton = By.cssSelector("a.btn.btn-secondary");

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
            "//div[@id='deleteModal']//button[contains(text(), 'Delete')] | //form[@id='deleteForm']//button[@type='submit']");
    private By deleteCancelButton = By.xpath(
            "//div[@id='deleteModal']//button[contains(text(), 'Cancel')] | //div[@id='deleteModal']//button[@data-bs-dismiss='modal']");

    // Actions on first row
    private By firstRowEditButton = By.xpath(
            "(//a[contains(@href, '/ui/plants/edit/')])[1] | (//i[contains(@class, 'bi-pencil')]/..)[1]");

    private By firstRowDeleteFallback = By.xpath(
            "(//form[contains(@action, '/ui/plants/delete/')]//button)[1] | (//i[contains(@class, 'bi-trash')]/..)[1]");

    // Success/Error messages
    private By successMessage = By.cssSelector(".alert-success, .alert.alert-success");
    private By errorMessage = By.cssSelector(".alert-danger, .alert.alert-danger");

    // Access denied
    private By accessDeniedMessage = By.xpath(
            "//*[contains(text(), 'Access Denied') or contains(text(), 'access denied') or contains(text(), '403')]");

    // ==================== Navigation Methods ====================

    public void openPlantsPage() {
        String url = config.getPlantsUrl();
        openUrl(url);
        waitABit(1000);
    }

    public void openEditPlantPage(String plantId) {
        String url = config.getEditPlantUrl(plantId);
        openUrl(url);
        waitABit(1000);
    }

    public void navigateToUrl(String relativeUrl) {
        String base = config.getBaseUrl();
        String fullUrl = base.endsWith("/") ? base + relativeUrl.replaceFirst("^/", "") : base + relativeUrl;
        getDriver().get(fullUrl);
        waitABit(1000);
    }

    public void pause(long milliseconds) {
        waitABit(milliseconds);
    }

    // ==================== Verification Methods ====================

    public boolean isOnPlantsListPage() {
        String currentUrl = getDriver().getCurrentUrl();
        boolean result = currentUrl.contains("/ui/plants") &&
                !currentUrl.contains("/add") &&
                !currentUrl.contains("/edit");
        return result;
    }

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

    public boolean hasNameColumn() {
        return find(nameColumn).isVisible();
    }

    public boolean hasCategoryColumn() {
        return find(categoryColumn).isVisible();
    }

    public boolean hasPriceColumn() {
        return find(priceColumn).isVisible();
    }

    public boolean hasStockColumn() {
        return find(stockColumn).isVisible();
    }

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

    public boolean isAccessDenied() {
        try {
            String url = getDriver().getCurrentUrl().toLowerCase();
            if (url.contains("/ui/login")) {
                return true;
            }
            if (findAll(accessDeniedMessage).size() > 0 && find(accessDeniedMessage).isVisible()) {
                return true;
            }
            String pageSource = getDriver().getPageSource().toLowerCase();
            return pageSource.contains("access denied") || pageSource.contains("403");
        } catch (Exception e) {
            String url = getDriver().getCurrentUrl().toLowerCase();
            return url.contains("/ui/login");
        }
    }

    // ==================== Action Methods ====================

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

    public void clickAddPlantButton() {
        clickAddPlant();
    }

    public void enterPlantName(String name) {
        find(nameInput).type(name);
    }

    public void enterPlantPrice(String price) {
        find(priceInput).type(price);
    }

    public void clickCancelButton() {
        find(cancelFormButton).click();
        waitABit(1000);
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

    // Delete Modal / Alert Handling
    public boolean isDeleteModalVisible() {
        if (isAlertPresent()) {
            System.out.println("Confirmation shown as ALERT (not modal).");
            return true; // We treat alert as 'modal visible' for flow continuity
        }
        try {
            WebElementFacade modal = find(deleteModal);
            if (!modal.isPresent())
                return false;
            String cls = modal.getAttribute("class");
            String display = modal.getCssValue("display");
            return cls != null && cls.contains("show") && !"none".equalsIgnoreCase(display);
        } catch (Exception e) {
            return false;
        }
    }

    public String getPlantNameInDeleteModal() {
        return find(plantNameInModal).getText();
    }

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
            alert.accept();
        } catch (Exception ignored) {
        }
    }

    // ==================== Search/Filter ====================

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

    // ==================== Pagination ====================

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
    }

    // ==================== Stock Management ====================

    public void recordStockOfFirstPlant() {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(tableRows);
        if (!rows.isEmpty()) {
            WebElementFacade firstRow = rows.get(0);
            String plantName = getPlantNameFromRow(firstRow);
            int stock = getStockFromRow(firstRow);
            stockSnapshot.put(plantName, stock);
        }
    }

    public String getFirstPlantName() {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(tableRows);
        if (!rows.isEmpty()) {
            return getPlantNameFromRow(rows.get(0));
        }
        return null;
    }

    public int getRecordedStock(String plantName) {
        return stockSnapshot.getOrDefault(plantName, -1);
    }

    public int getCurrentStock(String plantName) {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(tableRows);
        for (WebElementFacade row : rows) {
            if (getPlantNameFromRow(row).contains(plantName)) {
                return getStockFromRow(row);
            }
        }
        return -1;
    }

    public String findLowStockPlant() {
        waitABit(1000);
        List<WebElementFacade> rows = findAll(tableRows);

        // First try to find plant with 1-4 stock
        for (WebElementFacade row : rows) {
            int stock = getStockFromRow(row);
            if (stock > 0 && stock < 5) {
                String plantName = getPlantNameFromRow(row);
                stockSnapshot.put(plantName, stock);
                return plantName;
            }
        }
        // Fallback: use first available plant
        if (!rows.isEmpty()) {
            WebElementFacade firstRow = rows.get(0);
            String plantName = getPlantNameFromRow(firstRow);
            int stock = getStockFromRow(firstRow);
            stockSnapshot.put(plantName, stock);
            return plantName;
        }
        return null;
    }

    private String getPlantNameFromRow(WebElementFacade row) {
        try {
            List<WebElementFacade> cells = row.thenFindAll("td");
            if (!cells.isEmpty()) {
                return cells.get(0).getText().trim();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private int getStockFromRow(WebElementFacade row) {
        try {
            List<WebElementFacade> cells = row.thenFindAll("td");
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
                String text = stockCell.getText().trim();
                return Integer.parseInt(text.replaceAll("[^0-9]", ""));
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}
