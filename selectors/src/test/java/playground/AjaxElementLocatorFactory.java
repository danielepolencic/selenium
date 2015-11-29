package playground;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public class AjaxElementLocatorFactory implements ElementLocatorFactory {
    private WebDriver driver;

    public AjaxElementLocatorFactory(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new AjaxElementLocator(driver, field);
    }
}
