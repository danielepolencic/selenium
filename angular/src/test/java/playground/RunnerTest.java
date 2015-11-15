package playground;

import com.google.common.base.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class RunnerTest {

    WebDriver driver;
    PageObjectJQuery page;
    PageObjectAngular page2;

    @Before
    public void setup() {
        driver = new FirefoxDriver();
        page = new PageObjectJQuery(driver);
        page2 = new PageObjectAngular(driver);
    }

    @After
    public void teardown () {
        driver.quit();
    }

//    @Test
//    public void first() {
//        driver.get(System.getProperty("url"));
//        driver.findElement(By.cssSelector("button")).click();
//        new WebDriverWait(driver, 10).until(new Function<WebDriver, Boolean>() {
//            public Boolean apply(WebDriver webDriver) {
//                Long activeAjaxRequests = (Long) ((JavascriptExecutor) webDriver).executeScript("return $.active");
//                return activeAjaxRequests == 0;
//            }
//        });
//        assertTrue(driver.findElements(By.cssSelector("li")).size() > 0);
//    }
//
//    @Test
//    public void second() {
//        driver.get(System.getProperty("url"));
//        driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
//        driver.findElement(By.cssSelector("button")).click();
//        ((JavascriptExecutor) driver).executeAsyncScript("var callback = arguments[arguments.length - 1]; $(document).ajaxStop(callback)");
//        assertTrue(driver.findElements(By.cssSelector("li")).size() > 0);
//    }
//
//    @Test
//    public void three() {
//        driver.get(System.getProperty("url"));
//        driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
//        page.click();
//        assertTrue(page.getPosts().size() > 0);
//    }

    @Test
    public void four() {
        driver.get(System.getProperty("url"));
        driver.manage().timeouts().setScriptTimeout(3, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("button")).click();
        ((JavascriptExecutor) driver).executeAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                "angular.element(document).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);"
        );
        assertTrue(driver.findElements(By.cssSelector("li")).size() > 0);
    }

    @Test
    public void five() {
        driver.get(System.getProperty("url"));
        driver.manage().timeouts().setScriptTimeout(2, TimeUnit.SECONDS);
        page2.click();
        assertTrue(page2.getPosts().size() > 0);
    }
}