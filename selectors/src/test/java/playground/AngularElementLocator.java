package playground;

import com.google.common.base.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Field;
import java.util.List;

public class AngularElementLocator extends DefaultElementLocator {
    private WebDriver driver;

    public AngularElementLocator(WebDriver driver, Field field) {
        super(driver, field);
        this.driver = driver;
    }

    @Override
    public WebElement findElement() {
        waitForAjaxToComplete();
        return AngularElementLocator.super.findElement();
    }

    @Override
    public List<WebElement> findElements() {
        waitForAjaxToComplete();
        return AngularElementLocator.super.findElements();
    }

    private void waitForAjaxToComplete() {
        ((JavascriptExecutor) driver).executeAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                        "angular.element(document).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);"
        );
    }
}