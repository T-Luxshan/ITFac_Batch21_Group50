package com.OnTerminal.pages;

import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;

public class CategoriesPage extends PageObject {

    @FindBy(tagName = "table")
    WebElement categoriesTable;

    public void openPage() {
        openUrl("/ui/categories");
    }

    public boolean isTableVisible() {
        return categoriesTable != null && categoriesTable.isDisplayed();
    }
}
