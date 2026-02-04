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
}
