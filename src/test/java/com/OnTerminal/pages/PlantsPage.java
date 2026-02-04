package com.OnTerminal.pages;

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
    // @TC_SALES_ADM_UI_001
    public void openPlantsPage() {
        String url = config.getPlantsUrl();
        System.out.println("[PlantsPage] Navigating to Plants page: " + url);
        openUrl(url);
        waitABit(Constants.Timeouts.SHORT_WAIT * 1000L);
    }

    // ==================== Stock Management ====================

    /**
     * Record stock quantity of first plant for later comparison
     */
    // @TC_SALES_ADM_UI_001
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
    // @TC_SALES_ADM_UI_001
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
    // @TC_SALES_ADM_UI_001
    public int getRecordedStock(String plantName) {
        return stockSnapshot.getOrDefault(plantName, -1);
    }

    /**
     * Get current stock value for a plant from the UI
     */
    // @TC_SALES_ADM_UI_001
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

    // @TC_SALES_ADM_UI_001
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

    // @TC_SALES_ADM_UI_001
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

    // ==================== Verification Methods ====================

    /**
     * Check if currently on Plants List page
     */
    // @TC_SALES_ADM_UI_001
    public boolean isOnPlantsListPage() {
        String currentUrl = getDriver().getCurrentUrl();
        boolean result = currentUrl.contains("/ui/plants") && 
                        !currentUrl.contains("/add") && 
                        !currentUrl.contains("/edit");
        System.out.println("On Plants List page: " + result + " (URL: " + currentUrl + ")");
        return result;
    }
}
