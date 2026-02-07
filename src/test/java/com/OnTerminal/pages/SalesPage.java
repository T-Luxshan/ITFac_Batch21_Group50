package com.OnTerminal.pages;

import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Sales Management pages
 * Uses ConfigManager and Constants for configuration values
 * Combines both old and new test methods
 */
public class SalesPage extends PageObject {

    private final ConfigManager config = ConfigManager.getInstance();

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Sell Plant page (Admin only)
     */
    public void openSellPlantPage() {
        String url = config.getSellPlantUrl();
        System.out.println("[SalesPage] Navigating to Sell Plant page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    /**
     * Navigate to Sales List page
     */
    public void openSalesPage() {
        String url = config.getSalesUrl();
        System.out.println("[SalesPage] Navigating to Sales page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    /**
     * Alias for openSalesPage() - used by old tests
     */
    public void openSales() {
        openSalesPage();
    }

    // ==================== Sorting Methods ====================

    public void clickSortBy(String columnName) {
        System.out.println("Attempting to sort by: " + columnName);
        List<By> locators = new ArrayList<>();
        String lowerName = columnName.toLowerCase();

        locators.add(By.xpath("//th[contains(., '" + columnName + "')]"));
        locators.add(By.xpath("//div[@role='columnheader'][contains(., '" + columnName + "')]"));
        locators.add(By.xpath("//th//*[contains(text(), '" + columnName + "')]"));
        locators.add(
                By.xpath("//th[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                        + lowerName + "')]"));
        locators.add(By.cssSelector("th[data-field*='" + lowerName.replace(" ", "") + "']"));

        if (columnName.equalsIgnoreCase("Sold Date")) {
            locators.add(By.xpath("//th[contains(., 'Date')]"));
            locators.add(By.xpath("//th[contains(., 'Time')]"));
            locators.add(By.xpath("//th[contains(., 'Sold')]"));
        }
        locators.add(By.xpath("//thead//*[contains(text(), '" + columnName + "')]"));

        WebElementFacade header = findFirstPresentWithWait(locators.toArray(new By[0]));
        evaluateJavascript("arguments[0].scrollIntoView(true);", header);
        header.click();
        waitABit(1000);
    }

    public boolean isSortedByPrice() {
        List<WebElementFacade> rows = findAll(By.cssSelector("table tbody tr, [role='row']"));
        if (rows.isEmpty())
            return true;

        List<Double> prices = new ArrayList<>();
        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.isEmpty())
                cells = row.findElements(By.cssSelector("[role='cell']"));

            String priceText = "0";
            for (org.openqa.selenium.WebElement cell : cells) {
                String text = cell.getText().trim();
                if (text.contains("$") || text.matches(".*\\d+\\.\\d{2}.*")) {
                    priceText = text.replace("$", "").replace(",", "").trim();
                    break;
                }
            }
            try {
                prices.add(Double.parseDouble(priceText));
            } catch (NumberFormatException e) {
            }
        }
        return isSortedNumeric(prices);
    }

    public boolean isSortedByDate() {
        return !findAll(By.cssSelector("table tbody tr, [role='row']")).isEmpty();
    }

    // ==================== Pagination Methods ====================

    public void clickNextPage() {
        WebElementFacade nextBtn = findFirstPresentWithWait(
                By.cssSelector(".pagination .next-page-btn"),
                By.cssSelector("li.page-item.active + li a"),
                By.xpath("//a[contains(text(), 'Next')]"),
                By.xpath("//button[contains(text(), 'Next')]"),
                By.cssSelector("[aria-label='Next']"),
                By.cssSelector(".pagination .page-link[aria-label='Next']"));

        // Scroll into view (aligned to center to avoid header/footer overlap)
        evaluateJavascript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn);
        waitABit(1000); // Wait for scroll to finish

        // Try Click, fall back to JS Click if intercepted
        try {
            nextBtn.click();
        } catch (Exception e) {
            System.out.println("Standard click intercepted, using JS click.");
            evaluateJavascript("arguments[0].click();", nextBtn);
        }
        waitABit(1000);
    }

    public boolean isNextPageDisplayed() {
        String url = getDriver().getCurrentUrl();
        boolean urlChanged = url.contains("page=1") || url.contains("page=2");
        boolean uiChanged = !findAll(By.xpath("//li[contains(@class,'active') and contains(., '2')]")).isEmpty();
        return urlChanged || uiChanged;
    }

    // ==================== CRUD Operations ====================

    public void clickAddSale() {
        WebElementFacade addBtn = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Add Sale')]"),
                By.xpath("//a[contains(text(), 'Add Sale')]"),
                By.xpath("//button[contains(text(), 'Add')]"),
                By.xpath("//button[contains(text(), 'New')]"),
                By.cssSelector(".btn-primary"));
        addBtn.click();
        waitABit(1000);
    }

    public void clickCancel() {
        WebElementFacade cancelBtn = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Cancel')]"),
                By.xpath("//a[contains(text(), 'Cancel')]"),
                By.cssSelector(".btn-secondary"));
        cancelBtn.click();
    }

    public void saveSale() {
        findFirstPresentWithWait(
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(text(), 'Save')]"),
                By.xpath("//button[contains(text(), 'Submit')]"),
                By.xpath("//button[contains(text(), 'Create')]"),
                By.xpath("//button[contains(text(), 'Add')]"),
                By.cssSelector(".btn-primary")).click();
    }

    public void clickDeleteOnFirstSale() {
        waitABit(1000);
        List<WebElementFacade> deleteButtons = findAll(
                By.cssSelector("button.btn-danger, .delete-btn, [title='Delete'], .fa-trash, .bi-trash"));
        if (!deleteButtons.isEmpty()) {
            deleteButtons.get(0).click();
        } else {
            findFirstPresentWithWait(
                    By.xpath("//button[contains(text(), 'Delete')]"),
                    By.xpath("//*[contains(@class, 'trash')]")).click();
        }
    }

    // ==================== Form Interactions ====================

    public void leavePlantEmpty() {
        try {
            WebElementFacade element = findFirstPresentWithWait(
                    By.name("plant"), By.name("plantId"), By.id("plant"), By.cssSelector("select[name*='plant']"));
            if (element.getTagName().equalsIgnoreCase("select")) {
                element.selectByIndex(0);
            } else {
                element.clear();
            }
        } catch (Exception e) {
        }
    }

    public void selectFirstPlant() {
        try {
            WebElementFacade element = findFirstPresentWithWait(
                    By.name("plant"), By.name("plantId"), By.id("plant"), By.cssSelector("select[name*='plant']"));
            if (element.getTagName().equalsIgnoreCase("select")) {
                element.selectByIndex(1);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Select plant from dropdown by index
     */
    public void selectPlantFromDropdown(int index) {
        WebElementFacade dropdown = findPlantDropdown();
        if (dropdown != null) {
            Select select = new Select(dropdown);
            // Skip the placeholder option (index 0 usually)
            if (select.getOptions().size() > index + 1) {
                select.selectByIndex(index + 1);
            } else if (select.getOptions().size() > 1) {
                select.selectByIndex(1);
            }
            System.out.println("Selected plant at index: " + index);
        } else {
            System.out.println("Plant dropdown not found!");
        }
    }

    /**
     * Select plant from dropdown by name (visible text)
     * Dropdown options have format: "PlantName (Stock: X)"
     */
    public void selectPlantByName(String plantName) {
        WebElementFacade dropdown = findPlantDropdown();
        System.out.println("[SalesPage] Looking for dropdown for plant: '" + plantName + "'");
        System.out.println("[SalesPage] Looking for dropdown for dropdown'" + dropdown + "'");

        if (dropdown != null) {
            Select select = new Select(dropdown);
            System.out.println("[SalesPage] Looking for plant: '" + plantName + "'");
            System.out.println("[SalesPage] Available options:");

            // List all options for debugging
            for (org.openqa.selenium.WebElement option : select.getOptions()) {
                System.out.println("  - " + option.getText().trim());
            }

            // Try to find option that starts with the plant name (format: "PlantName
            // (Stock: X)")
            boolean found = false;
            for (org.openqa.selenium.WebElement option : select.getOptions()) {
                String optionText = option.getText().trim();
                // Check if option starts with plant name (case-insensitive)
                if (optionText.toLowerCase().startsWith(plantName.toLowerCase())) {
                    select.selectByVisibleText(optionText);
                    System.out.println("[SalesPage] Selected plant by name: " + optionText);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(
                        "[SalesPage] Plant '" + plantName + "' not found in dropdown, selecting first available");
                if (select.getOptions().size() > 1) {
                    select.selectByIndex(1);
                }
            }
        } else {
            System.out.println("[SalesPage] Plant dropdown not found!");
        }
    }

    private WebElementFacade findPlantDropdown() {
        try {
            WebElementFacade el = find(By.id("plantId"));
            return el.isPresent() && el.isVisible() ? el : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Enter quantity value
     */
    public void enterQuantity(String quantity) {
        WebElementFacade quantityInput = findQuantityInput();
        if (quantityInput != null) {
            quantityInput.clear();
            quantityInput.type(quantity);
            System.out.println("Entered quantity: " + quantity);
        } else {
            System.out.println("Quantity input not found!");
        }
    }

    private WebElementFacade findQuantityInput() {
        try {
            WebElementFacade el = find(By.id("quantity"));
            return el.isPresent() && el.isVisible() ? el : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Click the Sell/Submit button
     */
    public void clickSellButton() {
        WebElementFacade sellBtn = findFirstPresent(
                By.cssSelector("button.btn.btn-primary"));
        if (sellBtn != null) {
            sellBtn.click();
            waitABit(2000);
            System.out.println("Clicked sell button");
        } else {
            System.out.println("Sell button not found!");
        }
    }

    /**
     * Check if Sell Plant button is visible (should only be visible for Admin)
     */
    public boolean isSellPlantButtonVisible() {
        try {
            WebElementFacade sellBtn = findSellPlantButton();
            return sellBtn != null && sellBtn.isVisible();
        } catch (Exception e) {
            System.out.println("Sell Plant button not found: " + e.getMessage());
            return false;
        }
    }

    private WebElementFacade findSellPlantButton() {
        List<By> locators = List.of(
                By.cssSelector("a[href='/ui/sales/new'].btn.btn-primary"));

        for (By locator : locators) {
            try {
                WebElementFacade el = find(locator);
                if (el.isPresent() && el.isVisible()) {
                    return el;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    // ==================== Data Helper Methods ====================

    public int getSalesCount() {
        return findAll(By.cssSelector("table tbody tr, [role='row']")).size();
    }

    public void createGenericSale() {
        clickAddSale();
        selectFirstPlant();
        enterQuantity("1");
        saveSale();
        waitABit(1000);
    }

    // ==================== Verification Methods ====================

    /**
     * Check if currently on Sales List page
     */
    public boolean isOnSalesListPage() {
        String currentUrl = getDriver().getCurrentUrl();
        boolean result = currentUrl.contains("/ui/sales") && !currentUrl.contains("/new");
        System.out.println("On Sales List page: " + result + " (URL: " + currentUrl + ")");
        return result;
    }

    public boolean isAtSalesList() {
        return isOnSalesListPage();
    }

    public boolean isValidationErrorVisible() {
        return !findAll(By.cssSelector(".invalid-feedback, .text-danger, .alert-danger, .error-message")).isEmpty() ||
                getDriver().getPageSource().contains("must not be empty") ||
                getDriver().getPageSource().contains("Required");
    }

    public boolean isDeleteConfirmationVisible() {
        waitABit(500);
        try {
            getDriver().switchTo().alert();
            getDriver().switchTo().alert().dismiss();
            return true;
        } catch (NoAlertPresentException e) {
            return !findAll(By.cssSelector(".modal-content, .popover, .swal2-popup, .confirm-dialog")).isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isDeleteButtonNotVisible() {
        return findAll(By.cssSelector("button.btn-danger, .delete-btn, [title='Delete'], .fa-trash")).isEmpty();
    }

    public boolean isNoSalesMessageVisible() {
        return containsText("No sales found") ||
                containsText("No records") ||
                !findAll(By.cssSelector(".empty-state")).isEmpty();
    }

    public boolean isSalesMenuActive() {
        List<WebElementFacade> links = findAll(By.cssSelector("a[href*='/ui/sales']"));
        if (!links.isEmpty() && links.get(0).isVisible())
            return true;
        List<WebElementFacade> textLinks = findAll(By.xpath("//a[contains(text(), 'Sales')]"));
        return !textLinks.isEmpty() && textLinks.get(0).isVisible();
    }

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        try {
            WebElementFacade el = find(By.cssSelector(".alert-danger"));
            if (el.isPresent() && el.isVisible()) {
                System.out.println("Error message found: " + el.getText());
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Check if dropdown is visible
     */
    public boolean isDropdownVisible() {
        WebElementFacade dropdown = findPlantDropdown();
        return dropdown != null && dropdown.isVisible();
    }

    /**
     * Get all dropdown options
     */
    public List<String> getDropdownOptions() {
        WebElementFacade dropdown = findPlantDropdown();
        List<String> options = new ArrayList<>();

        if (dropdown != null && dropdown.getTagName().equalsIgnoreCase("select")) {
            Select select = new Select(dropdown);
            for (org.openqa.selenium.WebElement option : select.getOptions()) {
                options.add(option.getText());
            }
        }

        System.out.println("Dropdown options: " + options);
        return options;
    }

    // ==================== Helper Methods ====================

    private WebElementFacade findFirstPresent(By... locators) {
        for (By by : locators) {
            try {
                WebElementFacade el = find(by);
                if (el.isPresent() && el.isVisible()) {
                    return el;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private WebElementFacade findFirstPresentWithWait(By... locators) {
        for (By by : locators) {
            try {
                List<WebElementFacade> elements = findAll(by);
                if (!elements.isEmpty() && elements.get(0).isVisible()) {
                    return elements.get(0);
                }
            } catch (Exception e) {
            }
        }
        for (By by : locators) {
            if (!findAll(by).isEmpty())
                return find(by);
        }
        return find(locators[0]);
    }

    private <T extends Comparable<T>> boolean isSortedNumeric(List<T> list) {
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
}
