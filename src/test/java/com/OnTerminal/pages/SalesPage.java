package com.OnTerminal.pages;

import com.OnTerminal.config.ConfigManager;
import com.OnTerminal.config.Constants;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for Sales Management pages
 * Uses ConfigManager and Constants for configuration values
 * Page Object Model + Modular Framework
 */
public class SalesPage extends PageObject {

    private final ConfigManager config = ConfigManager.getInstance();

    // ==================== Navigation Methods ====================

    /**
     * Navigate to Sell Plant page (Admin only)
     */
    // @TC_SALES_ADM_UI_001 
    public void openSellPlantPage() {
        String url = config.getSellPlantUrl();
        System.out.println("[SalesPage] Navigating to Sell Plant page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    // ==================== Element Interactions ====================

    /**
     * Select plant from dropdown by index
     */
    // @TC_SALES_ADM_UI_001
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
    // @TC_SALES_ADM_UI_001
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
            
            // Try to find option that starts with the plant name (format: "PlantName (Stock: X)")
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
                System.out.println("[SalesPage] Plant '" + plantName + "' not found in dropdown, selecting first available");
                if (select.getOptions().size() > 1) {
                    select.selectByIndex(1);
                }
            }
        } else {
            System.out.println("[SalesPage] Plant dropdown not found!");
        }
    }

    // @TC_SALES_ADM_UI_001
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
    // @TC_SALES_ADM_UI_001
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

    // @TC_SALES_ADM_UI_001
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
    // @TC_SALES_ADM_UI_001
    public void clickSellButton() {
        WebElementFacade sellBtn = findFirstPresent(
            By.cssSelector("button.btn.btn-primary")
        );
        if (sellBtn != null) {
            sellBtn.click();
            waitABit(2000);
            System.out.println("Clicked sell button");
        } else {
            System.out.println("Sell button not found!");
        }
    }

    // ==================== Verification Methods ====================

    /**
     * Check if currently on Sales List page
     */
    // @TC_SALES_ADM_UI_001
    public boolean isOnSalesListPage() {
        String currentUrl = getDriver().getCurrentUrl();
        boolean result = currentUrl.contains("/ui/sales") && !currentUrl.contains("/new");
        System.out.println("On Sales List page: " + result + " (URL: " + currentUrl + ")");
        return result;
    }

    // ==================== Helper Methods ====================

    // @TC_SALES_ADM_UI_001
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
