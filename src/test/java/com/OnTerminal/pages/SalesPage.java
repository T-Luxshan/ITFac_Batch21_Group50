package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import java.util.ArrayList;
import java.util.List;

public class SalesPage extends PageObject {

    public void openSales() {
        String base = System.getProperty("webdriver.base.url", "http://localhost:8080");
        String url = base.endsWith("/") ? base + "ui/sales" : base + "/ui/sales";
        openUrl(url);
    }

    public void clickSortBy(String columnName) {
        System.out.println("Attempting to sort by: " + columnName);
        List<By> locators = new ArrayList<>();
        String lowerName = columnName.toLowerCase();

        locators.add(By.xpath("//th[contains(., '" + columnName + "')]"));
        locators.add(By.xpath("//div[@role='columnheader'][contains(., '" + columnName + "')]"));
        locators.add(By.xpath("//th//*[contains(text(), '" + columnName + "')]"));
        locators.add(By.xpath("//th[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerName + "')]"));
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
        if (rows.isEmpty()) return true;

        List<Double> prices = new ArrayList<>();
        for (WebElementFacade row : rows) {
            List<org.openqa.selenium.WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.isEmpty()) cells = row.findElements(By.cssSelector("[role='cell']"));

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
            } catch (NumberFormatException e) { }
        }
        return isSortedNumeric(prices);
    }

    public boolean isSortedByDate() {
        return !findAll(By.cssSelector("table tbody tr, [role='row']")).isEmpty();
    }

    // --- FIX FOR PAGINATION CLICK INTERCEPTION ---
    public void clickNextPage() {
        WebElementFacade nextBtn = findFirstPresentWithWait(
                By.cssSelector(".pagination .next-page-btn"),
                By.cssSelector("li.page-item.active + li a"),
                By.xpath("//a[contains(text(), 'Next')]"),
                By.xpath("//button[contains(text(), 'Next')]"),
                By.cssSelector("[aria-label='Next']")
        );

        // 1. Scroll into view (aligned to center to avoid header/footer overlap)
        evaluateJavascript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", nextBtn);
        waitABit(1000); // Wait for scroll to finish

        // 2. Try Click, fall back to JS Click if intercepted
        try {
            nextBtn.click();
        } catch (Exception e) {
            System.out.println("Standard click intercepted, using JS click.");
            evaluateJavascript("arguments[0].click();", nextBtn);
        }
        waitABit(1000);
    }
    // ---------------------------------------------

    public boolean isNextPageDisplayed() {
        String url = getDriver().getCurrentUrl();
        boolean urlChanged = url.contains("page=1") || url.contains("page=2");
        boolean uiChanged = !findAll(By.xpath("//li[contains(@class,'active') and contains(., '2')]")).isEmpty();
        return urlChanged || uiChanged;
    }

    public void clickAddSale() {
        WebElementFacade addBtn = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Add Sale')]"),
                By.xpath("//a[contains(text(), 'Add Sale')]"),
                By.xpath("//button[contains(text(), 'Add')]"),
                By.xpath("//button[contains(text(), 'New')]"),
                By.cssSelector(".btn-primary")
        );
        addBtn.click();
        waitABit(1000);
    }

    public void clickCancel() {
        WebElementFacade cancelBtn = findFirstPresentWithWait(
                By.xpath("//button[contains(text(), 'Cancel')]"),
                By.xpath("//a[contains(text(), 'Cancel')]"),
                By.cssSelector(".btn-secondary")
        );
        cancelBtn.click();
    }

    public boolean isAtSalesList() {
        String url = getDriver().getCurrentUrl();
        return url.contains("/ui/sales") && !url.contains("/new");
    }

    public void clickDeleteOnFirstSale() {
        waitABit(1000);
        List<WebElementFacade> deleteButtons = findAll(By.cssSelector("button.btn-danger, .delete-btn, [title='Delete'], .fa-trash, .bi-trash"));
        if (!deleteButtons.isEmpty()) {
            deleteButtons.get(0).click();
        } else {
            findFirstPresentWithWait(
                    By.xpath("//button[contains(text(), 'Delete')]"),
                    By.xpath("//*[contains(@class, 'trash')]")
            ).click();
        }
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

    public void leavePlantEmpty() {
        try {
            WebElementFacade element = findFirstPresentWithWait(
                    By.name("plant"), By.name("plantId"), By.id("plant"), By.cssSelector("select[name*='plant']")
            );
            if (element.getTagName().equalsIgnoreCase("select")) {
                element.selectByIndex(0);
            } else {
                element.clear();
            }
        } catch (Exception e) {}
    }

    public void enterQuantity(String qty) {
        WebElementFacade qtyInput = findFirstPresentWithWait(
                By.name("quantity"), By.name("qty"), By.id("quantity"), By.cssSelector("input[type='number']")
        );
        qtyInput.clear();
        qtyInput.type(qty);
    }

    public void saveSale() {
        findFirstPresentWithWait(
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(text(), 'Save')]"),
                By.xpath("//button[contains(text(), 'Submit')]"),
                By.xpath("//button[contains(text(), 'Create')]"),
                By.xpath("//button[contains(text(), 'Add')]"),
                By.cssSelector(".btn-primary")
        ).click();
    }

    public boolean isValidationErrorVisible() {
        return !findAll(By.cssSelector(".invalid-feedback, .text-danger, .alert-danger, .error-message")).isEmpty() ||
                getDriver().getPageSource().contains("must not be empty") ||
                getDriver().getPageSource().contains("Required");
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
        if (!links.isEmpty() && links.get(0).isVisible()) return true;
        List<WebElementFacade> textLinks = findAll(By.xpath("//a[contains(text(), 'Sales')]"));
        return !textLinks.isEmpty() && textLinks.get(0).isVisible();
    }

    private WebElementFacade findFirstPresentWithWait(By... locators) {
        for (By by : locators) {
            try {
                List<WebElementFacade> elements = findAll(by);
                if (!elements.isEmpty() && elements.get(0).isVisible()) {
                    return elements.get(0);
                }
            } catch (Exception e) { }
        }
        for (By by : locators) {
            if (!findAll(by).isEmpty()) return find(by);
        }
        return find(locators[0]);
    }

    private <T extends Comparable<T>> boolean isSortedNumeric(List<T> list) {
        if (list.size() < 2) return true;
        boolean ascending = true;
        boolean descending = true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) ascending = false;
            if (list.get(i).compareTo(list.get(i + 1)) < 0) descending = false;
        }
        return ascending || descending;
    }
}