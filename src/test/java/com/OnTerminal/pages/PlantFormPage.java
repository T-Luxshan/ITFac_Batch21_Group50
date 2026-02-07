package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class PlantFormPage extends PageObject {

    private By nameInput = By.name("name");
    private By categoryDropdown = By.name("categoryId");
    private By priceInput = By.name("price");
    private By quantityInput = By.name("quantity");

    private By saveButton = By.xpath("//button[contains(text(), 'Save')] | //button[@type='submit']");
    private By cancelButton = By.xpath("//a[contains(text(), 'Cancel')] | //button[contains(text(), 'Cancel')]");

    // FIXED: More specific validation error selectors
    private By validationError = By.cssSelector(".invalid-feedback:not([style*='display: none']), .text-danger:not([style*='display: none']), .alert-danger");
    private By nameError = By.xpath("//input[@name='name']/following-sibling::div[contains(@class, 'invalid-feedback')] | //input[@name='name']/following-sibling::span[contains(@class, 'text-danger')]");
    private By priceError = By.xpath("//input[@name='price']/following-sibling::div[contains(@class, 'invalid-feedback')] | //input[@name='price']/following-sibling::span[contains(@class, 'text-danger')]");
    private By quantityError = By.xpath("//input[@name='quantity']/following-sibling::div[contains(@class, 'invalid-feedback')] | //input[@name='quantity']/following-sibling::span[contains(@class, 'text-danger')]");

    private By pageTitle = By.xpath("//h3[contains(text(), 'Add Plant')] | //h3[contains(text(), 'Edit Plant')]");


    public void enterName(String name) {
        WebElementFacade nameField = find(nameInput);
        nameField.clear();
        nameField.type(name);
    }

    public void selectCategory(String category) {
        WebElementFacade dropdown = find(categoryDropdown);
        waitABit(1000);

        Select select = new Select(dropdown);

        System.out.println("=== Available categories in dropdown ===");
        List<org.openqa.selenium.WebElement> options = select.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println(i + ": '" + options.get(i).getText().trim() + "'");
        }

        try {
            select.selectByVisibleText(category.trim());
            System.out.println("Selected category by exact match: " + category);
            return;
        } catch (Exception e) {
            System.out.println("Exact match failed for: " + category);
        }

        for (org.openqa.selenium.WebElement option : options) {
            String optionText = option.getText().trim();
            if (optionText.equalsIgnoreCase(category.trim())) {
                option.click();
                System.out.println("Selected category by case-insensitive match: " + optionText);
                return;
            }
        }

        for (org.openqa.selenium.WebElement option : options) {
            String optionText = option.getText().trim();
            if (optionText.toLowerCase().contains(category.toLowerCase())) {
                option.click();
                System.out.println("Selected category by partial match: " + optionText);
                return;
            }
        }

        System.out.println("WARNING: Category '" + category + "' not found. Selecting first available category.");
        if (options.size() > 1) {
            select.selectByIndex(1);
            System.out.println("Selected category at index 1: " + options.get(1).getText());
        } else {
            System.out.println("ERROR: No categories available in dropdown!");
        }
    }

    public void selectCategoryByIndex(int index) {
        WebElementFacade dropdown = find(categoryDropdown);
        Select select = new Select(dropdown);
        select.selectByIndex(index);
        waitABit(500);
    }

    public void enterPrice(String price) {
        WebElementFacade priceField = find(priceInput);
        priceField.clear();
        priceField.type(price);
    }

    public void enterQuantity(String quantity) {
        WebElementFacade quantityField = find(quantityInput);
        quantityField.clear();
        quantityField.type(quantity);
    }

    public void clickSave() {
        find(saveButton).click();
        waitABit(2000);
    }

    public void clickCancel() {
        find(cancelButton).click();
        waitABit(1000);
    }


    public boolean isAddPlantPage() {
        try {
            String title = find(pageTitle).getText();
            return title.toLowerCase().contains("add");
        } catch (Exception e) {
            return getDriver().getCurrentUrl().contains("/plants/add");
        }
    }

    public boolean isEditPlantPage() {
        try {
            String title = find(pageTitle).getText();
            return title.toLowerCase().contains("edit");
        } catch (Exception e) {
            return getDriver().getCurrentUrl().contains("/plants/edit");
        }
    }

    public boolean hasValidationError() {
        try {
            waitABit(500);

            // Check if we're still on the form page first
            if (!isOnFormPage()) {
                return false;
            }

            // Look for actual validation errors near input fields
            List<WebElementFacade> errors = findAll(validationError);
            for (WebElementFacade error : errors) {
                if (error.isVisible()) {
                    String text = error.getText().trim().toLowerCase();
                    // Ignore common navigation elements
                    if (!text.equals("logout") && !text.equals("dashboard") &&
                            !text.equals("plants") && !text.equals("categories") &&
                            !text.isEmpty()) {
                        System.out.println("Found validation error: " + text);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getValidationErrorMessage() {
        try {
            if (!isOnFormPage()) {
                return "";
            }

            List<WebElementFacade> errors = findAll(validationError);
            for (WebElementFacade error : errors) {
                if (error.isVisible()) {
                    String text = error.getText().trim();
                    // Filter out navigation elements
                    if (!text.equalsIgnoreCase("logout") &&
                            !text.equalsIgnoreCase("dashboard") &&
                            !text.equalsIgnoreCase("plants") &&
                            !text.equalsIgnoreCase("categories") &&
                            !text.isEmpty()) {
                        return text;
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public boolean hasPriceError() {
        try {
            if (!isOnFormPage()) {
                return false;
            }
            WebElementFacade error = find(priceError);
            return error.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPriceErrorMessage() {
        try {
            if (!isOnFormPage()) {
                return "";
            }
            return find(priceError).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isOnFormPage() {
        String url = getDriver().getCurrentUrl();
        return url.contains("/plants/add") || url.contains("/plants/edit");
    }

    public String getCurrentValue(String fieldName) {
        By locator;
        switch (fieldName.toLowerCase()) {
            case "name":
                locator = nameInput;
                break;
            case "price":
                locator = priceInput;
                break;
            case "quantity":
                locator = quantityInput;
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
        return find(locator).getValue();
    }
}