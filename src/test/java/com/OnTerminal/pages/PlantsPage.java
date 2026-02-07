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
    }
}
