package playground;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PageObject {
    private WebDriver driver;

    public PageObject(WebDriver driver) {
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 2), this);
        this.driver = driver;
    }

    @FindBy(css = "form")
    private WebElement form;

    @FindBy(css = "input")
    private WebElement input;

    @FindBy(css = ".success")
    private WebElement successMessage;

    @FindBy(css = "p")
    private List<WebElement> messages;

    @FindBy(css = "timeout")
    private WebElement timeout;

    public void openUrl(String url) {
        driver.get(url);
    }

    public void enterYear(String year) {
        input.sendKeys(year);
    }

    public void submitForm() {
        form.submit();
    }

    public String getSuccessMessage() {
        return successMessage.getText();
    }

    public boolean isErrorMessageDisplayed() {
        return messages.size() > 1;
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    public boolean hasTimedOut() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".timeout")));
        return element.isDisplayed();
    }
}
