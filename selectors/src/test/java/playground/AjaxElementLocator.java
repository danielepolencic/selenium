package playground;

import com.google.common.base.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Field;
import java.util.List;

public class AjaxElementLocator extends DefaultElementLocator {
    private WebDriver driver;

    public AjaxElementLocator(WebDriver driver, Field field) {
        super(driver, field);
        this.driver = driver;
    }

    @Override
    public WebElement findElement() {
        waitForAjaxToComplete();
        return AjaxElementLocator.super.findElement();
    }

    @Override
    public List<WebElement> findElements() {
        waitForAjaxToComplete();
        return AjaxElementLocator.super.findElements();
    }

    private void waitForAjaxToComplete() {
        new WebDriverWait(driver, 10).until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                Long activeAjaxRequests = (Long) ((JavascriptExecutor) webDriver).executeScript("return $.active");
                return activeAjaxRequests == 0;
            }
        });
    }
}
